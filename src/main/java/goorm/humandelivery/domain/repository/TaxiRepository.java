package goorm.humandelivery.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.humandelivery.domain.model.entity.Taxi;

public interface TaxiRepository extends JpaRepository<Taxi, Long> {
}
