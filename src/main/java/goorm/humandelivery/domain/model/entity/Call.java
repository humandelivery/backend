package goorm.humandelivery.domain.model.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
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
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Call extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "call_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@Embedded
	@AttributeOverride(name = "latitude", column = @Column(name = "ex_origin_latitude"))
	@AttributeOverride(name = "longitude", column = @Column(name = "ex_origin_longitude"))
	private Location expectedOrigin;


	@Embedded
	@AttributeOverride(name = "latitude", column = @Column(name = "ex_dest_latitude"))
	@AttributeOverride(name = "longitude", column = @Column(name = "ex_dest_longitude"))
	private Location expectedDestination;

	@Enumerated(value = EnumType.STRING)
	private TaxiType taxiType;





}
