package telran.drones.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import static telran.drones.api.DronesValidationErrorMessages.*;

public record DroneDto(
		@Size(max = MAX_DRONE_NUMBER_LENGTH, message = DRONE_NUMBER_WRONG_LENGTH) @NotEmpty(message = EMPTY_DRONE_NUMBER_MESSAGE) String number,

		@NotNull(message = MISSING_MODEL) ModelType modelType) {

}
