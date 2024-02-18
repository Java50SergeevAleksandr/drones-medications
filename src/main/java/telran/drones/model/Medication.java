package telran.drones.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "medication")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class Medication {

	@Column(name = "medication_name", nullable = false)
	String name;

	@Column(nullable = false)
	int weight;

	@Id
	@Column(name = "medication_code")
	String code;
}