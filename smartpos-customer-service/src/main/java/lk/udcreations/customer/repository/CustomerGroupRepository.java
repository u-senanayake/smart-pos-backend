package lk.udcreations.customer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lk.udcreations.customer.entity.CustomerGroup;

@Repository
public interface CustomerGroupRepository extends JpaRepository<CustomerGroup, Integer> {

	List<CustomerGroup> findByDeletedFalse();

	Optional<CustomerGroup> findByNameAndDeletedFalse(String name);

	Optional<CustomerGroup> findByNameAndDeletedTrue(String name);

}
