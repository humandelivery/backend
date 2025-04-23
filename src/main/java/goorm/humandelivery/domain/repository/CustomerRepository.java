package goorm.humandelivery.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.humandelivery.domain.model.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
