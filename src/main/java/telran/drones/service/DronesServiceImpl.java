package telran.drones.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.drones.api.PropertiesNames;
import telran.drones.dto.*;
import telran.drones.dto.reflections.DroneItemsAmount;
import telran.drones.dto.reflections.DroneNumber;
import telran.drones.dto.reflections.MedicationCode;
import telran.drones.exceptions.*;

import telran.drones.model.*;
import telran.drones.repo.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@EnableScheduling
public class DronesServiceImpl implements DronesService {
	final DronesRepo droneRepo;
	final MedicationRepo medicationRepo;
	final EventLogRepo logRepo;
	final DronesModelRepo droneModelRepo;
	final Map<State, State> statesMachine;

	@Value("${" + PropertiesNames.CAPACITY_THRESHOLD + ":25}")
	int capacityThreshold;

//	@Value("${" + PropertiesNames.PERIODIC_DELTA_MILLIS_UNIT + ":1000}")
//	String periodicTimeDelta;

	@Value("${" + PropertiesNames.CAPACITY_DELTA_TIME_UNIT + ":2}")
	private int capacityDeltaPerTimeUnit;

	@Value("${" + PropertiesNames.MAX_CAPACITY + ":100}")
	private int maxCapacity;

	@Scheduled(timeUnit = TimeUnit.MILLISECONDS, fixedDelayString = "${" + PropertiesNames.PERIODIC_DELTA_MILLIS_UNIT
			+ ":1000}")
	@Transactional(readOnly = false)
	void periodicTask() {
		List<Drone> listOfDrones = droneRepo.findAll();
		listOfDrones.stream().forEach(d -> executeTask(d));
	}

	private void executeTask(Drone d) {
		log.debug("periodic status on drone : {}", d);
		State currentState = d.getState();
		int currentBatteryCapacity = d.getBatteryCapacity();

		if (currentState == State.IDLE && currentBatteryCapacity < maxCapacity) {
			chargeBattery(d, currentState, currentBatteryCapacity);
		}

		if (currentState != State.IDLE) {
			drainBatteryandChangeState(d, currentState, currentBatteryCapacity);
		}
	}

	private void chargeBattery(Drone d, State currentState, int currentBatteryCapacity) {
		log.debug("execute chargeBattery on drone : {}", d);
		int newBatteryCapacity = currentBatteryCapacity + capacityDeltaPerTimeUnit;
		d.setBatteryCapacity(Math.min(maxCapacity, newBatteryCapacity));
		droneRepo.save(d);

		EventLog eventLog = new EventLog(LocalDateTime.now(), d.getNumber(), currentState, newBatteryCapacity, null);
		logRepo.save(eventLog);
		log.debug("saved log: {}", eventLog);
	}

	private void drainBatteryandChangeState(Drone d, State currentState, int currentBatteryCapacity) {
		log.debug("execute drain Battery and ChangeState on drone : {}", d);
		int newBatteryCapacity = currentBatteryCapacity - capacityDeltaPerTimeUnit;
		d.setBatteryCapacity(Math.max(0, newBatteryCapacity));
		State newState = statesMachine.get(currentState);
		d.setState(newState);
		droneRepo.save(d);
		String lastMedication = logRepo.findLastMedicationCode(d.getNumber());
		log.debug("LastMedicationCode string : {}", lastMedication);

		if (newState == State.DELIVERED) {
			lastMedication = null;
		}

		EventLog eventLog = new EventLog(LocalDateTime.now(), d.getNumber(), newState, newBatteryCapacity,
				lastMedication);
		logRepo.save(eventLog);
		log.debug("saved log: {}", eventLog);
	}

	@Override
	@Transactional(readOnly = false)
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
	@Transactional(readOnly = false)
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
		if (!droneRepo.existsById(droneNumber)) {
			throw new DroneNotFoundException();
		}
		log.debug("drone exists: {}", droneNumber);
		List<MedicationCode> codes = logRepo.findByDroneNumber(droneNumber);
		List<String> res = codes.stream().map(MedicationCode::getMedicationCode).toList();

		if (res.isEmpty()) {
			log.warn("list of Medication Items for droneNumber{} is emty", droneNumber);
		} else {
			log.debug("get list Medication Items on drone {} are : {}", droneNumber, res);
		}
		return res;
	}

	@Override
	public List<String> checkAvailableDrones() {
		List<DroneNumber> numbers = droneRepo.findByStateAndBatteryCapacityGreaterThanEqual(State.IDLE,
				capacityThreshold);
		List<String> res = numbers.stream().map(DroneNumber::getNumber).toList();
		log.debug("Available drones are {}", res);
		return res;
	}

	@Override
	public int checkBatteryCapacity(String droneNumber) {
		Integer batteryCapacity = droneRepo.findBatteryCapacity(droneNumber);
		if (batteryCapacity == null) {
			throw new DroneNotFoundException();
		}

		log.debug("battery capacity of drone {} is {}", droneNumber, batteryCapacity);
		return batteryCapacity;
	}

	@Override
	public List<DroneItemsAmount> checkDroneLoadedItemAmounts() {
		List<DroneItemsAmount> list = logRepo.findDroneLoadedItemAmounts();
		list.forEach(d -> log.debug("drone number is {}, amount of items {}", d.getNumber(), d.getAmount()));
		return list;
	}

}