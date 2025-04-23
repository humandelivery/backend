package goorm.humandelivery.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.humandelivery.domain.model.entity.Matching;

public interface MatchingRepository extends JpaRepository<Matching, Long> {
}
