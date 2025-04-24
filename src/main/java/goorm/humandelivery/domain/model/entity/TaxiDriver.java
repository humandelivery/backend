package goorm.humandelivery.domain.model.entity;

import org.springframework.security.crypto.password.PasswordEncoder;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TaxiDriver extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "taxi_driver_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "taxi_id")
	private Taxi taxi;

	@Column(unique = true)
	private String loginId;

	private String password;

	private String name;

	private String licenseCode;

	@NotBlank
	@Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식은 010-XXXX-XXXX 이어야 합니다.")
	private String phoneNumber;

	@Enumerated(value = EnumType.STRING)
	private TaxiDriverStatus status;

	@Builder
	private TaxiDriver(Taxi taxi, String loginId, String password, String name, String licenseCode, String phoneNumber, TaxiDriverStatus status) {
		this.taxi = taxi;
		this.loginId = loginId;
		this.password = password;
		this.name = name;
		this.licenseCode = licenseCode;
		this.phoneNumber = phoneNumber;
		this.status = status;
	}

	public boolean isSamePassword(String rawPassword, PasswordEncoder passwordEncoder) {
		return passwordEncoder.matches(rawPassword, password);
	}
}
