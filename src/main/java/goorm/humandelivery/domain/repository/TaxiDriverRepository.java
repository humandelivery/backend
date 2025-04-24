package goorm.humandelivery.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.humandelivery.domain.model.entity.TaxiDriver;

public interface TaxiDriverRepository extends JpaRepository<TaxiDriver, Long> {

	Optional<TaxiDriver> findByLoginId(String loginId);
}
