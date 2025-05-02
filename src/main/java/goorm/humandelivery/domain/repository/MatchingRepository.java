package goorm.humandelivery.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import goorm.humandelivery.domain.model.entity.Matching;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

	@Query("select m from Matching m where m.callInfo.id = :callId")
	Optional<Matching> findMatchingByCallInfoId(Long callId);

	@Query("select m.id from Matching m where m.callInfo.id = :callId")
	Optional<Long> findMatchingIdByCallInfoId(Long callId);
}
