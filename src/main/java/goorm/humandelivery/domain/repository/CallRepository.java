package goorm.humandelivery.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.humandelivery.domain.model.entity.Matching;

public interface CallRepository extends JpaRepository<Matching, Long> {
}
