package telran.drones.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.drones.model.Drone;

public interface DronesRepo extends JpaRepository<Drone, String> {
	List<Drone> findByState();
}
