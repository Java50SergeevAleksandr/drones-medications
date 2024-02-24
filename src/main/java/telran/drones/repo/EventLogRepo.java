package telran.drones.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.drones.dto.reflections.DroneItemsAmount;
import telran.drones.model.EventLog;

public interface EventLogRepo extends JpaRepository<EventLog, Long> {
	List<EventLog> findByDroneNumber(String droneNumber);
	//List<MedicationCode> findByDroneNumber(String droneNumber);

	EventLog findFirst1ByDroneNumberOrderByTimestampDesc(String number);

	@Query("""
			select medicationCode from EventLog where droneNumber =:droneNumber order by id desc limit 1
			""")
	String findLastMedicationCode(String droneNumber);

	@Query("""
			select d.number as number, count(e.droneNumber) as amount
			from Drone as d left join EventLog as e on e.droneNumber = d.number
			group by d.number
			order by count(e.droneNumber) desc
			""")
	List<DroneItemsAmount> findDroneLoadedItemAmounts();
}
