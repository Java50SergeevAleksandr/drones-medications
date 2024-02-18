package telran.drones.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.drones.dto.DroneItemsAmount;
import telran.drones.model.EventLog;

public interface EventLogRepo extends JpaRepository<EventLog, Long> {

	List<EventLog> findByDroneNumber(String droneNumber);

	@Query("""
			select droneNumber as number, count(medicationCode) as amount
			from EventLog group by droneNumber
			order by amount desc
			""")
	List<DroneItemsAmount> findDroneLoadedItemAmounts();
}
