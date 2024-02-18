package telran.drones.model;

import jakarta.persistence.*;
import lombok.*;
import telran.drones.dto.*;

@Entity
@Table(name = "drones")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Getter
public class Drone {
	@Id
	@Column(length = 100, name = "drone_number")
	String number;

	@ManyToOne
	@JoinColumn(name = "model_name")
	@Setter
	DroneModel model;

	@Column(name = "battery_capacity")
	int batteryCapacity;

	@Setter
	@Enumerated(EnumType.STRING)
	State state;

	static public Drone of(DroneDto droneDto) {
		return new Drone(droneDto.number(), null, 100, State.IDLE);
	}

	public DroneDto build() {
		return new DroneDto(number, model.getModelName());
	}
}