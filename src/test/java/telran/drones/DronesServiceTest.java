package telran.drones;

import telran.drones.api.*;
import telran.drones.dto.*;
import telran.drones.model.*;
import telran.drones.exceptions.*;

import telran.drones.repo.*;
import telran.drones.service.DronesService;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(properties = { PropertiesNames.PERIODIC_UNIT_MILLIS + "=1000000" })
@Sql(scripts = "classpath:test_data.sql")
class DronesServiceTest {
	private static final String DRONE1 = "Drone-1";
	private static final String DRONE2 = "Drone-2";
	private static final String MED1 = "MED_1";
	private static final String DRONE3 = "Drone-3";
	private static final String SERVICE_TEST = "Service: ";
	private static final String DRONE4 = "Drone-4";
	private static final String MED2 = "MED_2";
	@Autowired
	DronesService dronesService;
	@Autowired
	DronesRepo droneRepo;
	@Autowired
	EventLogRepo logRepo;
	DroneDto droneDto = new DroneDto(DRONE4, ModelType.Cruiserweight);
	DroneDto drone1 = new DroneDto(DRONE1, ModelType.Middleweight);
	DroneMedication droneMedication1 = new DroneMedication(DRONE1, MED1);

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.LOAD_DRONE_NORMAL)
	void loadDroneNormal() {
		dronesService.loadDrone(droneMedication1);
		List<EventLog> logs = logRepo.findAll();
		assertEquals(1, logs.size());
		EventLog loadingLog = logs.get(0);
		String droneNumber = loadingLog.getDroneNumber();
		State state = loadingLog.getState();
		String medicationCode = loadingLog.getMedicationCode();
		assertEquals(DRONE1, droneNumber);
		assertEquals(State.LOADING, state);
		assertEquals(MED1, medicationCode);
	}

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.LOAD_DRONE_NOT_MATCHING_STATE)
	void loadDroneWrongState() {
		assertThrowsExactly(IllegalDroneStateException.class,
				() -> dronesService.loadDrone(new DroneMedication(DRONE3, MED1)));
	}

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.LOAD_DRONE_MEDICATION_NOT_FOUND)
	void loadDroneMedicationNotFound() {
		assertThrowsExactly(MedicationNotFoundException.class,
				() -> dronesService.loadDrone(new DroneMedication(DRONE1, "wrongMed")));
	}

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.LOAD_DRONE_NOT_FOUND)
	void loadDroneNotFound() {
		assertThrowsExactly(DroneNotFoundException.class,
				() -> dronesService.loadDrone(new DroneMedication(DRONE4, MED1)));
	}

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.REGISTER_DRONE_NORMAL)
	void registerDroneNormal() {
		assertEquals(droneDto, dronesService.registerDrone(droneDto));
		assertNotNull(droneRepo.findById(DRONE4).orElse(null));

	}

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.REGISTER_DRONE_ALREADY_EXISTS)
	void registerDroneAlreadyExists() {
		assertThrowsExactly(DroneAlreadyExistException.class, () -> dronesService.registerDrone(drone1));
	}

}