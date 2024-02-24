package telran.drones;

import telran.drones.dto.*;
import telran.drones.model.*;

import telran.drones.repo.*;
import telran.drones.service.DronesService;
import telran.drones.api.PropertiesNames;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(properties = { PropertiesNames.PERIODIC_DELTA_MILLIS_UNIT + "=10" })
@Sql(scripts = "classpath:test_idle_data.sql")
class DronesServicePeriodicTaskTest {
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
	void loadDroneNormal() throws InterruptedException {
		dronesService.loadDrone(droneMedication1);
		Thread.sleep(2000);
		List<EventLog> logs = logRepo.findAll();
		assertEquals(23, logs.size()); // 1-log (method loadDrone) + 11 (Full cycle of delivering and returning) + 11
										// (full cycle of battery charging)
		State[] statesChain = getStatesChain();

		assertStates(statesChain, logs);
		Drone drone = droneRepo.findById(DRONE1).orElseThrow();
		assertEquals(State.IDLE, drone.getState());
		assertEquals(100, drone.getBatteryCapacity());
	}

	private State[] getStatesChain() {
		State[] stateValues = State.values();
		State[] statesChain = Arrays.copyOfRange(stateValues, 1, stateValues.length + 12);
		for (int i = 0; i < statesChain.length; i++) {
			if (statesChain[i] == null) {
				statesChain[i] = State.IDLE;
			}
		}
		return statesChain;
	}

	private void assertStates(State[] statesChain, List<EventLog> logs) {
		final int[] indexValues = { 0 };
		logs.forEach(l -> assertEquals(statesChain[indexValues[0]++], l.getState()));

	}

}