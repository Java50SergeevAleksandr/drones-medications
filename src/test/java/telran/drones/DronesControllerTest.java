package telran.drones;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import telran.drones.api.DronesValidationErrorMessages;
import telran.drones.api.ServiceExceptionMessages;
import telran.drones.api.UrlConstants;
import telran.drones.dto.DroneDto;
import telran.drones.dto.DroneMedication;
import telran.drones.dto.ModelType;
import telran.drones.exceptions.*;
import telran.exceptions.controller.DronesExceptionsController;
import telran.drones.service.DronesService;

record DroneDtoWrongEnum(String number, String modelType) {

}

@WebMvcTest
class DronesControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	DronesService dronesService;

	@Autowired
	ObjectMapper mapper;

	private static final String CONTROLLER_TEST = "Controller:";
	private static final String HOST = UrlConstants.HOST;
	private static final String DRONE_NUMBER_1 = "DRONE-1";
	private static final String MEDICATION_CODE = "MED_1";
	static final String URL_DRONE_REGISTER = HOST + UrlConstants.DRONES;
	private static final String URL_DRONE_LOAD = HOST + UrlConstants.LOAD_DRONE;

	DroneDto droneDto1 = new DroneDto(DRONE_NUMBER_1, ModelType.Cruiserweight);
	DroneDtoWrongEnum droneDtoWrongFields = new DroneDtoWrongEnum(new String(new char[1000]), "wrongType");
	DroneDto droneDtoMissingFields = new DroneDto(null, null);

	DroneMedication droneMedication = new DroneMedication(DRONE_NUMBER_1, MEDICATION_CODE);
	DroneMedication droneMedicationWrongFields = new DroneMedication(new String(new char[1000]), "mED_1");
	DroneMedication droneMedicationMissingFields = new DroneMedication(null, null);

	String[] errorMessagesDroneWrongFields = { DronesExceptionsController.JSON_TYPE_MISMATCH_MESSAGE };
	String[] errorMessagesDroneMissingFields = { DronesValidationErrorMessages.EMPTY_DRONE_NUMBER_MESSAGE,
			DronesValidationErrorMessages.MISSING_MODEL };
	String[] errorMessagesDroneMedicationWrongFields = { DronesValidationErrorMessages.DRONE_NUMBER_WRONG_LENGTH,
			DronesValidationErrorMessages.WRONG_MEDICATION_CODE };
	String[] errorMessagesDroneMedicationMissingFields = { DronesValidationErrorMessages.EMPTY_DRONE_NUMBER_MESSAGE,
			DronesValidationErrorMessages.EMPTY_MEDICATION_CODE, };

	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.REGISTER_DRONE_NORMAL)
	void droneRegister_normalFlow_success() throws Exception {
		when(dronesService.registerDrone(droneDto1)).thenReturn(droneDto1);
		String droneJSON = mapper.writeValueAsString(droneDto1);
		String response = mockMvc
				.perform(post(URL_DRONE_REGISTER).contentType(MediaType.APPLICATION_JSON).content(droneJSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(droneJSON, response);
	}

	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.REGISTER_DRONE_MISSING_FIELDS)
	void droneRegister_missingFields_errorMessage() throws Exception {
		String droneJSON = mapper.writeValueAsString(droneDtoMissingFields);
		String response = mockMvc
				.perform(post(URL_DRONE_REGISTER).contentType(MediaType.APPLICATION_JSON).content(droneJSON))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertErrorMessages(response, errorMessagesDroneMissingFields);
	}

	private void assertErrorMessages(String response, String[] expectedMessages) {
		String[] actualMessages = response.split(";");
		Arrays.sort(actualMessages);
		Arrays.sort(expectedMessages);
		assertArrayEquals(expectedMessages, actualMessages);
	}

	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.REGISTER_DRONE_VALIDATION_VIOLATION)
	void droneRegister_wrongFields_errorMessage() throws Exception {
		String droneJSON = mapper.writeValueAsString(droneDtoWrongFields);
		String response = mockMvc
				.perform(post(URL_DRONE_REGISTER).contentType(MediaType.APPLICATION_JSON).content(droneJSON))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertErrorMessages(response, errorMessagesDroneWrongFields);
	}

	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.REGISTER_DRONE_ALREADY_EXISTS)
	void droneRegister_alreadyExists_exception() throws Exception {
		when(dronesService.registerDrone(droneDto1)).thenThrow(new DroneAlreadyExistException());
		String droneJSON = mapper.writeValueAsString(droneDto1);
		String response = mockMvc
				.perform(post(URL_DRONE_REGISTER).contentType(MediaType.APPLICATION_JSON).content(droneJSON))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertEquals(ServiceExceptionMessages.DRONE_ALREADY_EXISTS, response);

	}

	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.LOAD_DRONE_NORMAL)
	void loadMedication_normalFlow_success() throws Exception {
		when(dronesService.loadDrone(droneMedication)).thenReturn(droneMedication);
		String droneMedicationJSON = mapper.writeValueAsString(droneMedication);
		String response = mockMvc
				.perform(post(URL_DRONE_LOAD).contentType(MediaType.APPLICATION_JSON).content(droneMedicationJSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(droneMedicationJSON, response);

	}

	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.LOAD_DRONE_MEDICATION_NOT_FOUND)
	void loadMedicationMedicationNotFound() throws Exception {
		serviceExceptionRequest(new MedicationNotFoundException(), 404, ServiceExceptionMessages.MEDICATION_NOT_FOUND);

	}

	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.LOAD_DRONE_NOT_FOUND)
	void loadMedicationDroneNotFound() throws Exception {
		serviceExceptionRequest(new DroneNotFoundException(), 404, ServiceExceptionMessages.DRONE_NOT_FOUND);

	}

	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.LOAD_DRONE_LOW_BATTERY_CAPCITY)
	void loadMedicationLowBatteryCapacity() throws Exception {
		serviceExceptionRequest(new LowBatteryCapacityException(), 400, ServiceExceptionMessages.LOW_BATTERY_CAPACITY);

	}

	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.LOAD_DRONE_NOT_MATCHING_STATE)
	void loadMedicationNotMatchingState() throws Exception {
		serviceExceptionRequest(new IllegalDroneStateException(), 400, ServiceExceptionMessages.NOT_IDLE_STATE);

	}

	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.LOAD_DRONE_NOT_MATCHING_WEIGHT)
	void loadMedicationNotMatchingWeight() throws Exception {
		serviceExceptionRequest(new IllegalMedicationWeightException(), 400,
				ServiceExceptionMessages.WEIGHT_LIMIT_VIOLATION);

	}

	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.LOAD_DRONE_WRONG_FIELDS)
	void loadMedicationWrongFields() throws Exception {
		validationExceptionRequest(errorMessagesDroneMedicationWrongFields, droneMedicationWrongFields);
	}

	@Test
	@DisplayName(CONTROLLER_TEST + TestDisplayNames.LOAD_DRONE_MISSING_FIELDS)
	void loadMedicationMissingFields() throws Exception {
		validationExceptionRequest(errorMessagesDroneMedicationMissingFields, droneMedicationMissingFields);
	}

	private void serviceExceptionRequest(RuntimeException serviceException, int statusCode, String errorMessage)
			throws Exception {
		when(dronesService.loadDrone(droneMedication)).thenThrow(serviceException);
		String droneMedicationJSON = mapper.writeValueAsString(droneMedication);
		String response = mockMvc
				.perform(post(URL_DRONE_LOAD).contentType(MediaType.APPLICATION_JSON).content(droneMedicationJSON))
				.andExpect(status().is(statusCode)).andReturn().getResponse().getContentAsString();
		assertEquals(errorMessage, response);
	}

	private void validationExceptionRequest(String[] errorMessages, DroneMedication droneMedicationDto)
			throws Exception {
		String droneMedicationJSON = mapper.writeValueAsString(droneMedicationDto);
		String response = mockMvc
				.perform(post(URL_DRONE_LOAD).contentType(MediaType.APPLICATION_JSON).content(droneMedicationJSON))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertErrorMessages(response, errorMessages);
	}
}
