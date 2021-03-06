package edu.monash.it.fit3077.akql.model;

import edu.monash.it.fit3077.akql.model.BloodPressureModel.BloodPressurePatientMonitorModel;
import edu.monash.it.fit3077.akql.model.CholesterolModel.CholesterolPatientMonitorModel;
import edu.monash.it.fit3077.akql.model.OralTemperatureModel.OralTemperaturePatientMonitorModel;
import edu.monash.it.fit3077.akql.model.TobaccoUseModel.TobaccoUsePatientMonitorModel;

/**
 * This class focuses on creating specific monitors (eg. Cholesterol). This class can be extended to
 * many other monitors in future.
 */
class PatientMonitorCreator {

    /**
     * Creates different types of patient monitors based on the monitor request information.
     * @param requestMonitorInfoModel: the monitor request information.
     * @return the patient monitor as requested in the monitor request information.
     */
    public static PatientMonitorModel createMonitor(RequestMonitorInfoModel requestMonitorInfoModel) {

        if (requestMonitorInfoModel.getMeasurementType().equals("Cholesterol")) {
            return new CholesterolPatientMonitorModel(requestMonitorInfoModel.getPatientId(), requestMonitorInfoModel.getClientId());
        } else if (requestMonitorInfoModel.getMeasurementType().equals("TobaccoUse")) {
            return new TobaccoUsePatientMonitorModel(requestMonitorInfoModel.getPatientId(), requestMonitorInfoModel.getClientId());
        } else if (requestMonitorInfoModel.getMeasurementType().equals("SystolicBloodPressure")) {
            return new BloodPressurePatientMonitorModel(requestMonitorInfoModel.getPatientId(), requestMonitorInfoModel.getClientId(), requestMonitorInfoModel.getMeasurementType());
        } else if (requestMonitorInfoModel.getMeasurementType().equals("DiastolicBloodPressure")) {
            return new BloodPressurePatientMonitorModel(requestMonitorInfoModel.getPatientId(), requestMonitorInfoModel.getClientId(), requestMonitorInfoModel.getMeasurementType());
        } else if (requestMonitorInfoModel.getMeasurementType().equals("OralTemperature")) {
            return new OralTemperaturePatientMonitorModel(requestMonitorInfoModel.getPatientId(), requestMonitorInfoModel.getClientId());
        }

        return null;
    }
}
