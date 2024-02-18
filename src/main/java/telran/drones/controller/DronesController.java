package telran.drones.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.drones.api.UrlConstants;
import telran.drones.dto.DroneDto;
import telran.drones.dto.DroneMedication;
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
}
