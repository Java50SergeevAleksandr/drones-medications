delete from event_logs;
delete from drones;
delete from medications;
delete from drone_models;

insert into drone_models (model_name, weight) values
('Lightweight', 100),
('Middleweight', 300),
('Cruiserweight', 400),
('Heavyweight', 500);
insert into drones (drone_number, model_name,  battery_capacity, state) 
	values
		('Drone-1', 'Lightweight', 100, 'IDLE'),
		('Drone-2', 'Lightweight', 100, 'IDLE'),
		('Drone-3', 'Lightweight', 100, 'IDLE'),
		('Drone-4', 'Middleweight', 100, 'IDLE'),
		('Drone-5', 'Middleweight', 100, 'IDLE'),
		('Drone-6', 'Middleweight', 100, 'IDLE'),		
		('Drone-7', 'Cruiserweight', 100, 'IDLE'),
		('Drone-8', 'Cruiserweight', 100, 'IDLE'),
		('Drone-9', 'Heavyweight', 100, 'IDLE'),
		('Drone-10', 'Heavyweight', 100, 'IDLE');
insert into medications (medication_code, medication_name, weight)
	values 
		('MED_1', 'Medication-1', 200),
		('MED_2', 'Medication-2', 350),
		('MED_3', 'Medication-3', 90);