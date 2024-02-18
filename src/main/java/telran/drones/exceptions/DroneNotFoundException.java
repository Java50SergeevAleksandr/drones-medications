package telran.drones.exceptions;

import telran.drones.api.ServiceExceptionMessages;

@SuppressWarnings("serial")
public class DroneNotFoundException extends NotFoundException {
	public DroneNotFoundException() {
		super(ServiceExceptionMessages.DRONE_NOT_FOUND);

	}

}
