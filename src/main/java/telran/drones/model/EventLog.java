package telran.drones.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import telran.drones.dto.State;

@Entity
@Table(name = "event_logs")
@NoArgsConstructor
@ToString
@Getter
public class EventLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;

	@Temporal(TemporalType.TIMESTAMP)
	LocalDateTime timestamp;

	@Column(name = "drone_number")
	String droneNumber;

	@Column(name = "medication_code")
	String medicationCode;

	@Enumerated(EnumType.STRING)
	State state;

	@Column(name = "battery_capacity")
	int batteryCapacity;

	public EventLog(LocalDateTime timestamp, String droneNumber, State state, int batteryCapacity,
			String medicationCode) {
		this.timestamp = timestamp;
		this.droneNumber = droneNumber;
		this.state = state;
		this.batteryCapacity = batteryCapacity;
		this.medicationCode = medicationCode;
	}

}