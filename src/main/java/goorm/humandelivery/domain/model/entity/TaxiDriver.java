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
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TaxiDriver extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "taxi_driver_id")
	private int id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "taxi_id")
	private Taxi taxi;

	@Column(unique = true)
	private String loginId;

	private String password;

	private String name;

	private String licenseCode;

	private String phoneNumber;

	@Enumerated(value = EnumType.STRING)
	private TaxiDriverStatus status;
}
