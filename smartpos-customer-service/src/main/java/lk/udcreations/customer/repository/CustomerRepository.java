package lk.udcreations.customer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import lk.udcreations.customer.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

	Optional<Customer> findByUsernameAndDeletedFalse(String userName);

	Optional<Customer> findByUsernameAndDeletedTrue(String userName);

	Optional<Customer> findByFirstName(String fistName);

	Optional<Customer> findByLastName(String lastName);

	Optional<Customer> findByFirstNameAndLastName(String firstName, String lastName);

	Optional<Customer> findByEmail(String email);

	List<Customer> findByDeletedFalse();

	Optional<Customer> findByCustomerIdAndDeletedFalse(Integer userId);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

	Optional<Customer> findByUsername(String username);

}
