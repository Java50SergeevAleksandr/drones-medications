package telran.drones.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.drones.dto.State;
import telran.drones.dto.reflections.DroneNumber;
import telran.drones.model.Drone;

public interface DronesRepo extends JpaRepository<Drone, String> {
	@Query("select batteryCapacity from Drone where number=:droneNumber")
	Integer findBatteryCapacity(String droneNumber);

	List<DroneNumber> findByStateAndBatteryCapacityGreaterThanEqual(State state, int capacityThreshold);
}
