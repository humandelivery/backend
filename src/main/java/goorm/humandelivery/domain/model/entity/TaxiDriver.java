package goorm.humandelivery.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaxiDriver extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "taxi_driver_id")
	private int id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "taxi_id")
	private Taxi taxi;

	private String loginId;

	private String password;

	private String name;

	private String licenseCode;

	private String phoneNumber;

	@Enumerated(value = EnumType.STRING)
	private TaxiDriverStatus status;
}
