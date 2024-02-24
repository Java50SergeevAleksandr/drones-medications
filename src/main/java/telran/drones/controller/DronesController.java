package telran.drones.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.drones.api.UrlConstants;
import telran.drones.dto.DroneDto;
import telran.drones.dto.DroneMedication;
import telran.drones.dto.EventLogDto;
import telran.drones.dto.reflections.DroneItemsAmount;
import telran.drones.service.DronesService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DronesController {
	final DronesService dronesService;

	@PostMapping(UrlConstants.DRONES)
	DroneDto registerDrone(@RequestBody @Valid DroneDto dto) {
		log.debug("registerDrone: received drone data: {}", dto);
		return dronesService.registerDrone(dto);
	}

	@PostMapping(UrlConstants.LOAD_DRONE)
	DroneMedication loadDrone(@RequestBody @Valid DroneMedication dto) {
		log.debug("loadDrone: received Drone Medication data: {}", dto);
		return dronesService.loadDrone(dto);
	}

	@GetMapping(UrlConstants.DRONE_MEDICATION_ITEMS + "{" + UrlConstants.DRONE_NUMBER + "}")
	List<String> checkMedicationItems(@PathVariable(UrlConstants.DRONE_NUMBER) String droneNumber) {
		log.debug("checkMedicationItems controller for drone {}", droneNumber);
		return dronesService.checkMedicationItems(droneNumber);
	}

	@GetMapping(UrlConstants.AVAILABLE_DRONES)
	List<String> checkAvailableDrones() {
		log.debug("checkAvailableDrones controller");
		return dronesService.checkAvailableDrones();
	}

	@GetMapping(UrlConstants.DRONE_BATTERY_CAPACITY + "{" + UrlConstants.DRONE_NUMBER + "}")
	int checkBatteryCapacity(@PathVariable(UrlConstants.DRONE_NUMBER) String droneNumber) {
		log.debug("checkBatteryCapacity controller for drone {}", droneNumber);
		return dronesService.checkBatteryCapacity(droneNumber);
	}

	@GetMapping(UrlConstants.DRONES_AMOUNT_ITEMS)
	List<DroneItemsAmount> checkDronesMedItems() {
		log.debug("checkDronesMedItems controller");
		return dronesService.checkDroneLoadedItemAmounts();
	}

	@GetMapping(UrlConstants.DRONE_HISTORY_LOGS + "{" + UrlConstants.DRONE_NUMBER + "}")
	List<EventLogDto> checkHistoryLogs(@PathVariable(UrlConstants.DRONE_NUMBER) String droneNumber) {
		log.debug("checkHistoryLogs controller for drone {}", droneNumber);
		return dronesService.checkHistoryLogs(droneNumber);

	}
}
