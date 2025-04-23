package goorm.humandelivery.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.humandelivery.domain.model.entity.DrivingInfo;

public interface DrivingInfoRepository extends JpaRepository<DrivingInfo, Long> {
}
