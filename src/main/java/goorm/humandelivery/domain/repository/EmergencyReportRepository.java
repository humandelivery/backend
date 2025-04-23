package goorm.humandelivery.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.humandelivery.domain.model.entity.EmergencyReport;

public interface EmergencyReportRepository extends JpaRepository<EmergencyReport, Long> {
}
