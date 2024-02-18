package telran.drones.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.drones.model.EventLog;

public interface EventLogRepo extends JpaRepository<EventLog, Long> {

}
