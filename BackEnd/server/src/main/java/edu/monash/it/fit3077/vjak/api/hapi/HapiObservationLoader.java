package edu.monash.it.fit3077.vjak.api.hapi;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import edu.monash.it.fit3077.vjak.model.ObservationLoaderInterface;
import edu.monash.it.fit3077.vjak.model.ObservationModelInterface;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Observation;

public class HapiObservationLoader implements ObservationLoaderInterface {
    private IGenericClient client;

    public HapiObservationLoader() {
        FhirContext ctx = FhirContext.forDstu3();
        String serverBaseUrl = "http://hapi-fhir.erc.monash.edu:8080/baseDstu3";
        ctx.getRestfulClientFactory().setConnectTimeout(120 * 1000);
        ctx.getRestfulClientFactory().setSocketTimeout(120 * 1000);
        this.client = ctx.newRestfulGenericClient(serverBaseUrl);
    }

    public ObservationModelInterface getLatestObservation(String patientId, String measurementCode) {
        Bundle response = this.client.search()
            .forResource(Observation.class)
            .where(Observation.PATIENT.hasId(patientId))
            .and(Observation.CODE.exactly().code(measurementCode))
            .sort().descending(Observation.DATE)
            .returnBundle(Bundle.class)
            .execute();

        try {
            return new HapiObservationModel((Observation) response.getEntry().get(0).getResource());
        } catch (IndexOutOfBoundsException err) {
            return null;
        }
    }
}
