package goorm.humandelivery.domain.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
public class DrivingInfo extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "driving_info_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "matching_id")
	private Matching matching;

	@Embedded
	@AttributeOverride(name = "latitude", column = @Column(name = "origin_latitude"))
	@AttributeOverride(name = "longitude", column = @Column(name = "origin_longitude"))

	private Location origin;

	private LocalDateTime pickupTime;

	@Embedded
	@AttributeOverride(name = "latitude", column = @Column(name = "dest_latitude"))
	@AttributeOverride(name = "longitude", column = @Column(name = "dest_longitude"))
	private Location destination;

	private LocalDateTime arrivingTime;

	@Enumerated(value = EnumType.STRING)
	private DrivingStatus drivingStatus;

	private boolean reported;

}
