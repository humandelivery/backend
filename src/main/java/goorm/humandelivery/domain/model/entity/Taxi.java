package goorm.humandelivery.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Taxi extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "taxi_id")
	private Long id;

	private String model;

	@Enumerated(value = EnumType.STRING)
	private TaxiType taxiType;

	private String plateNumber;

	@Enumerated(value = EnumType.STRING)
	private FuelType fuelType;

}
