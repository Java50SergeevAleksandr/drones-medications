package telran.drones.model;

import jakarta.persistence.*;
import telran.drones.dto.ModelType;
import lombok.*;

@Entity
@Table(name = "drone_models")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DroneModel {
	@Id
	@Enumerated(EnumType.STRING)
	@Column(name="model_name")
	ModelType modelName;
	
	int weight;
}