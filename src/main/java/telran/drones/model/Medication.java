package telran.drones.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medication")
@NoArgsConstructor
@AllArgsConstructor
public class Medication {
	@Id
	@Column(name = "medication_name")
	String name;

	int weight;

	@Column(name = "medication_code")
	String code;
}