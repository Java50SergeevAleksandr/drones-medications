package telran.drones.api;

public interface DronesValidationErrorMessages {
	int MAX_DRONE_NUMBER_LENGTH = 100;
	String DRONE_NUMBER_WRONG_LENGTH = "Length of drone number cannot be greater than " + MAX_DRONE_NUMBER_LENGTH;
	String MISSING_MODEL = "Missing drone model";
	String EMPTY_MEDICATION_CODE = "Missing Medication code";
	String EMPTY_DRONE_NUMBER_MESSAGE = "Missing drone number";
	String WRONG_MEDICATION_CODE = "Wrong Medication Code";

}
