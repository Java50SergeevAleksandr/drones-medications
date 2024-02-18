package telran.drones.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.drones.api.PropertiesNames;
import telran.drones.dto.*;
import telran.drones.exceptions.*;

import telran.drones.model.*;
import telran.drones.repo.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DronesServiceImpl implements DronesService {
	final DronesRepo droneRepo;
	final MedicationRepo medicationRepo;
	final EventLogRepo logRepo;
	final DronesModelRepo droneModelRepo;

	@Value("${" + PropertiesNames.CAPACITY_THRESHOLD + ":25}")
	int capacityThreshold;

	@Value("${" + PropertiesNames.CAPACITY_DELTA_TIME_UNIT + ":2}")
	private int capacityDeltaPerTimeUnit;

	@Override
	@Transactional
	public DroneDto registerDrone(DroneDto droneDto) {
		log.debug("service got drone DTO: {}", droneDto);

		if (droneRepo.existsById(droneDto.number())) {
			throw new DroneAlreadyExistException();
		}

		Drone drone = Drone.of(droneDto);
		DroneModel droneModel = droneModelRepo.findById(droneDto.modelType())
				.orElseThrow(() -> new ModelNotFoundException());
		drone.setModel(droneModel);
		log.debug("drone object is {}", drone);
		droneRepo.save(drone);
		return droneDto;
	}

	@Override
	@Transactional
	public DroneMedication loadDrone(DroneMedication droneMedication) {
		String droneNumber = droneMedication.droneNumber();
		String medicationCode = droneMedication.medicationCode();
		log.debug("received: droneNumber={}, medicationCode={}", droneNumber, droneMedication.medicationCode());
		log.debug("capacity threshold is {}", capacityThreshold);
		Drone drone = droneRepo.findById(droneNumber).orElseThrow(() -> new DroneNotFoundException());
		log.debug("found drone: {}", drone);
		Medication medication = medicationRepo.findById(medicationCode)
				.orElseThrow(() -> new MedicationNotFoundException());
		log.debug("found medication: {}", medication);

		if (drone.getState() != State.IDLE) {
			throw new IllegalDroneStateException();
		}

		if (drone.getBatteryCapacity() < capacityThreshold) {
			throw new LowBatteryCapacityException();
		}

		if (drone.getModel().getWeight() < medication.getWeight()) {
			throw new IllegalMedicationWeightException();
		}

		drone.setState(State.LOADING);

		EventLog eventLog = new EventLog(LocalDateTime.now(), drone.getNumber(), drone.getState(),
				drone.getBatteryCapacity(), medicationCode);
		logRepo.save(eventLog);

		log.debug("saved log: {}", eventLog);

		return droneMedication;
	}

	@Override
	public List<String> checkMedicationItems(String droneNumber) {
		Drone drone = droneRepo.findById(droneNumber).orElseThrow(() -> new DroneNotFoundException());
		log.debug("drone exists: {}", drone);
		List<EventLog> list = logRepo.findByDroneNumber(droneNumber);
		List<String> res = list.stream().map(el -> el.getMedicationCode()).collect(Collectors.toList());

		if (res.isEmpty()) {
			log.warn("list of Medication Items for droneNumber{} is emty", droneNumber);
		} else {
			log.debug("get list Medication Items: {}", res);
		}
		return res;
	}

	@Override
	public List<String> checkAvailableDrones() {
		List<Drone> list = droneRepo.findByState();
		List<String> res = list.stream().map(d -> d.getNumber()).collect(Collectors.toList());
		if (res.isEmpty()) {
			log.warn("list of Available Drones is emty");
		} else {
			log.debug("get list of Available Drones: {}", res);
		}
		return res;
	}

	@Override
	public int checkBatteryCapacity(String droneNumber) {
		Drone drone = droneRepo.findById(droneNumber).orElseThrow(() -> new DroneNotFoundException());
		int res = drone.getBatteryCapacity();
		log.debug("Battery Capacity for Drone {}  is : {}%", drone, res);
		return res;
	}

	@Override
	public List<DroneItemsAmount> checkDroneLoadedItemAmounts() {
		List<DroneItemsAmount> list = logRepo.findDroneLoadedItemAmounts();
		list.forEach(d -> log.debug("drone number is {}, amount of items {}", d.getNumber(), d.getAmount()));
		return list;
	}

}