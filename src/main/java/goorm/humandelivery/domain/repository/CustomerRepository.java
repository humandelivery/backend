package goorm.humandelivery.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.humandelivery.domain.model.entity.Customer;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	Optional<Customer> findByLoginId(String loginId);

	boolean existsByLoginId(String loginId);

	boolean existsByPhoneNumber(String phoneNumber);
}
