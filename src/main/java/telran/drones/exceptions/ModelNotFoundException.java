package telran.drones.exceptions;

import telran.drones.api.ServiceExceptionMessages;
import telran.exceptions.NotFoundException;

@SuppressWarnings("serial")
public class ModelNotFoundException extends NotFoundException {

	public ModelNotFoundException() {
		super(ServiceExceptionMessages.MODEL_NOT_FOUND);
	}

}