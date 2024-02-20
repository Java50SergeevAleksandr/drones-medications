package telran.drones;

import telran.drones.dto.*;
import telran.drones.model.*;
import telran.drones.exceptions.*;

import telran.drones.repo.*;
import telran.drones.service.DronesService;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import jakarta.transaction.Transactional;

@SpringBootTest
@Sql(scripts = "classpath:test_data.sql")
class DronesServiceTest {
	private static final String DRONE1 = "Drone-1";
	private static final String DRONE2 = "Drone-2";
	private static final String DRONE3 = "Drone-3";
	private static final String DRONE4 = "Drone-4";

	private static final String MED1 = "MED_1";
	private static final String MED2 = "MED_2";
	private static final String MED3 = "MED_3";

	private static final String SERVICE_TEST = "Service: ";

	@Autowired
	DronesService dronesService;

	@Autowired
	DronesRepo droneRepo;

	@Autowired
	EventLogRepo logRepo;

	DroneDto droneDto = new DroneDto(DRONE4, ModelType.Cruiserweight);
	DroneDto droneDto1 = new DroneDto(DRONE1, ModelType.Middleweight);
	DroneMedication droneMedication1 = new DroneMedication(DRONE1, MED1);
	DroneMedication droneMedication2 = new DroneMedication(DRONE1, MED2);
	DroneMedication droneMedication3 = new DroneMedication(DRONE1, MED3);
	DroneMedication droneMedication4 = new DroneMedication(DRONE2, MED1);
	DroneMedication droneMedication5 = new DroneMedication(DRONE3, MED1);

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.PERIODIC_TASK)
	void periodicTaskTest() throws InterruptedException {
		dronesService.loadDrone(droneMedication3);
		Thread.sleep(12000);
		dronesService.loadDrone(droneMedication4);
		Thread.sleep(10000);
	}

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
		dronesService.loadDrone(droneMedication1);
		assertThrowsExactly(IllegalDroneStateException.class,
				() -> dronesService.loadDrone(new DroneMedication(DRONE1, MED1)));
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
		assertTrue(droneRepo.existsById(DRONE4));

	}

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.REGISTER_DRONE_ALREADY_EXISTS)
	void registerDroneAlreadyExists() {
		assertThrowsExactly(DroneAlreadyExistException.class, () -> dronesService.registerDrone(droneDto1));
	}

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.CHECK_BATTERY_CAPACITY_NORMAL)
	void checkBatteryCapacity_normalFlow_success() {
		assertEquals(droneRepo.findById(DRONE2).get().getBatteryCapacity(), dronesService.checkBatteryCapacity(DRONE2));
	}

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.CHECK_BATTERY_CAPACITY_DRONE_NOT_EXISTS)
	void checkBatteryCapacity_droneNotExists_exception() {
		assertThrowsExactly(DroneNotFoundException.class, () -> dronesService.checkBatteryCapacity(DRONE4));
	}

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.CHECK_MEDICATION_ITEMS_NORMAL)
	@Transactional
	void checkMedicationItems_normalFlow_success() {
		Drone drone = droneRepo.findById(DRONE1).get();
		dronesService.loadDrone(droneMedication1);
		drone.setState(State.IDLE);
		dronesService.loadDrone(droneMedication3);
		drone.setState(State.IDLE);
		dronesService.loadDrone(droneMedication1);
		List<String> exp = List.of(MED1, MED3, MED1);
		List<String> testList = dronesService.checkMedicationItems(DRONE1);
		assertEquals(exp, testList);
	}

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.CHECK_MEDICATION_ITEMS_EMPTY)
	void checkMedicationItems_emptyList_success() {
		List<String> testList = dronesService.checkMedicationItems(DRONE1);
		assertTrue(testList.isEmpty());
	}

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.CHECK_MEDICATION_ITEMS_DRONE_NOT_EXISTS)
	void checkMedicationItems_droneNotExists_exception() {
		assertThrowsExactly(DroneNotFoundException.class, () -> dronesService.checkMedicationItems(DRONE4));
	}

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.AVAILABLE_DRONES)
	void checkAvailableDrones_normalFlow_success() {
		List<String> availableExpected = List.of(DRONE1, DRONE3);
		List<String> availableActual = dronesService.checkAvailableDrones();
		assertIterableEquals(availableExpected, availableActual);
		dronesService.loadDrone(droneMedication1);
		dronesService.loadDrone(droneMedication5);
		assertTrue(dronesService.checkAvailableDrones().isEmpty());
	}

	@Test
	@DisplayName(SERVICE_TEST + TestDisplayNames.CHECK_DRONES_ITEMS_AMOUNT)
	void checkDroneLoadedItemAmounts_normalFlow_success() {
		dronesService.loadDrone(droneMedication1);
		Map<String, Long> resultMap = dronesService.checkDroneLoadedItemAmounts().stream()
				.collect(Collectors.toMap(da -> da.getNumber(), da -> da.getAmount()));
		assertEquals(3, resultMap.size());
		assertEquals(1, resultMap.get(DRONE1));
		assertEquals(0, resultMap.get(DRONE2));
		assertEquals(0, resultMap.get(DRONE3));

	}
}