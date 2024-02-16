package telran.drones.service;

import telran.drones.dto.*;

public interface DronesService {
	/**
	 * adds new Drone into Database
	 * 
	 * @ @param droneDto
	 * @return DroneDto for success
	 * @throws DroneIllegalStateException (drone with a given number already exists)
	 */
	DroneDto registerDrone(DroneDto droneDto);

	/**
	 * checks whether a given drone available for loading (state IDLE, battery
	 * capacity >= 25%, weight match) checks whether a given medication exists
	 * checks whether a given drone model exists creates event log with the state
	 * LOADING and current battery capacity
	 * 
	 * @param droneMedication
	 * @return DroneMedication for success
	 * @throws appropriate exception in accordance with the required checks
	 */
	DroneMedication loadDrone(DroneMedication droneMedication);
}
