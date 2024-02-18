package telran.drones.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import static telran.drones.api.DronesValidationErrorMessages.*;
import static telran.drones.api.ConstraintConstants.*;

public record DroneMedication(
		@Size(max = MAX_DRONE_NUMBER_LENGTH, message = DRONE_NUMBER_WRONG_LENGTH) @NotEmpty(message = EMPTY_DRONE_NUMBER_MESSAGE) String droneNumber,
		@NotEmpty(message = EMPTY_MEDICATION_CODE) @Pattern(regexp = MEDICATION_CODE_REGEXP, message = WRONG_MEDICATION_CODE) String medicationCode) {

}