package telran.drones.configuration;

import java.util.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import telran.drones.dto.State;

@Configuration
public class DronesConfiguration {
	@SuppressWarnings("serial")
	@Bean
	Map<State, State> getStatesMachine() {
		return new HashMap<>() {
			{
				put(State.LOADING, State.LOADED);
				put(State.LOADED, State.DELIVERING);
				put(State.DELIVERING, State.DELIVERING1);
				put(State.DELIVERING1, State.DELIVERING2);
				put(State.DELIVERING2, State.DELIVERING3);
				put(State.DELIVERING3, State.DELIVERED);
				put(State.DELIVERED, State.RETURNING);
				put(State.RETURNING, State.RETURNING1);
				put(State.RETURNING1, State.RETURNING2);
				put(State.RETURNING2, State.RETURNING3);
				put(State.RETURNING3, State.IDLE);

			}
		};
	}
}
