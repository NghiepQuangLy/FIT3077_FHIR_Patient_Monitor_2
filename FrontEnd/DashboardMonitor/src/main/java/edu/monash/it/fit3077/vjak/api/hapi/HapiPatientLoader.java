package edu.monash.it.fit3077.vjak.api.hapi;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import edu.monash.it.fit3077.vjak.model.PatientLoader;
import org.hl7.fhir.dstu3.model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public class HapiPatientLoader implements PatientLoader {
    private IGenericClient client;
    private Bundle currentEncounterPage;
    private HashSet<String> patientIdsCache;
    private String practionerId;

    public HapiPatientLoader(String practitionerId) {
        this.practionerId = practitionerId;
        this.patientIdsCache = new HashSet<String>();
        this.initializeHapiClient();
    }

    private void initializeHapiClient() {
        FhirContext ctx = FhirContext.forDstu3();
        String serverBaseUrl = "http://hapi-fhir.erc.monash.edu:8080/baseDstu3";
        ctx.getRestfulClientFactory().setConnectTimeout(120 * 1000);
        ctx.getRestfulClientFactory().setSocketTimeout(120 * 1000);
        this.client = ctx.newRestfulGenericClient(serverBaseUrl);
    }

    private ArrayList<String> fetchNewPatientIds() {
        ArrayList<String> patientIds = new ArrayList<String>();

        while (patientIds.size() < 5 && (this.currentEncounterPage == null || this.currentEncounterPage.getLink(Bundle.LINK_NEXT) != null)) {
            if (this.currentEncounterPage == null) {
                this.currentEncounterPage = client.search()
                        .forResource(org.hl7.fhir.dstu3.model.Encounter.class)
                        .where(Encounter.PRACTITIONER.hasId(this.practionerId))
                        .returnBundle(Bundle.class)
                        .execute();
            } else {
                this.currentEncounterPage = client.loadPage().next(this.currentEncounterPage).execute();
            }

            this.currentEncounterPage.getEntry()
                                        .stream()
                                        .forEach(entry -> {
                                            Encounter e = (Encounter) entry.getResource();
                                            Reference r = e.getSubject();

                                            String patientId = r.getReference().replaceAll("\\D+","");

                                            // Check if new patient id (note: new patient ids are cached once the patient resouce has been downloaded.
                                            if (!this.patientIdsCache.contains(patientId) && !patientIds.contains(patientId)) {
                                                patientIds.add(patientId);
                                            }
                                        });
        }
        return patientIds;
    }

    private ArrayList<org.hl7.fhir.dstu3.model.Patient> downloadNewPatients(ArrayList<String> patientIds) {
        if (patientIds.size() == 0) return new ArrayList<org.hl7.fhir.dstu3.model.Patient>();

        Bundle response = client.search()
                    .forResource(org.hl7.fhir.dstu3.model.Patient.class)
                    .where(Patient.RES_ID.exactly().systemAndValues("", patientIds))
                    .returnBundle(Bundle.class)
                    .execute();
        ArrayList<org.hl7.fhir.dstu3.model.Patient> rawHapiPatients = response.getEntry()
                                                                        .stream()
                                                                        .map(entry -> (Patient) entry.getResource())
                                                                        .collect(Collectors.toCollection(ArrayList::new));
        return rawHapiPatients;
    }

    private void cachePatientIds(ArrayList<String> patientIds) {
        patientIds.stream()
                    .forEach(patientId -> this.patientIdsCache.add(patientId));
    }

    public ArrayList<edu.monash.it.fit3077.vjak.model.Patient> loadPatients() {
        ArrayList<String> patientIds = this.fetchNewPatientIds();
        ArrayList<org.hl7.fhir.dstu3.model.Patient> rawHapiPatients = this.downloadNewPatients(patientIds);
        this.cachePatientIds(patientIds);

        ArrayList<edu.monash.it.fit3077.vjak.model.Patient> hapiPatients = rawHapiPatients.stream()
                                                .map(p -> new HapiPatient(p))
                                                .collect(Collectors.toCollection(ArrayList::new));

        return hapiPatients;
    }
}
