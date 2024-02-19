package telran.drones.api;

public interface UrlConstants {
	String HOST = "http://localhost:8080/";
	String DRONES = "drones";
	String LOAD_DRONE = DRONES + "/load";
	String DRONE_NUMBER = "droneNumber";
	String DRONE_MEDICATION_ITEMS = DRONES + "/items/";
	String AVAILABLE_DRONES = DRONES + "/available";
	String DRONE_BATTERY_CAPACITY = DRONES + "/battery/";
	String DRONES_AMOUNT_ITEMS = DRONES + "/amount/items";
}
