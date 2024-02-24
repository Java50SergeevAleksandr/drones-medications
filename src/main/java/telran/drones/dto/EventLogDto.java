package telran.drones.dto;

import java.time.LocalDateTime;

public record EventLogDto(LocalDateTime tempstamp, String droneNumber, String medicationCode, State state,
		int batteryCapacity) {

}