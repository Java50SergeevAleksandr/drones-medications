package telran.drones.api;

public interface ServiceExceptionMessages {
	String DRONE_NOT_FOUND = "Drone Not Found";
	String MEDICATION_NOT_FOUND = "Medication Not Found";
	String NOT_IDLE_STATE = "Loading may be done only in IDLE state";
	String LOW_BATTERY_CAPACITY = "Low battery capacity, below 25%";
	String WEIGHT_LIMIT_VIOLATION = "Drone weight limit exceeded";
	String DRONE_ALREADY_EXISTS = "Drone already exists";
}