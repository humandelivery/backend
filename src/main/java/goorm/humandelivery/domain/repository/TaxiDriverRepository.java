package goorm.humandelivery.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.humandelivery.domain.model.entity.Taxi;
import goorm.humandelivery.domain.model.entity.TaxiDriver;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public interface TaxiDriverRepository extends JpaRepository<TaxiDriver, Long> {

	Optional<TaxiDriver> findByLoginId(String loginId);

	TaxiDriver taxi(Taxi taxi);

	boolean getTaxiDriverByLoginId(String loginId);

	boolean existsByLoginId(@Email @NotBlank String loginId);
}
