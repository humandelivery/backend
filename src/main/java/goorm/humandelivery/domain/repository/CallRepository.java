package goorm.humandelivery.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.humandelivery.domain.model.entity.CallInfo;

public interface CallRepository extends JpaRepository<CallInfo, Long> {
}
