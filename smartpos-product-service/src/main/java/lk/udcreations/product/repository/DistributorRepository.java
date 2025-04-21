package lk.udcreations.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lk.udcreations.product.entity.Distributor;

@Repository
public interface DistributorRepository extends JpaRepository<Distributor, Integer> {

	List<Distributor> findByDeletedFalse();

	Optional<Distributor> findByCompanyNameAndDeletedFalse(String name);

	Optional<Distributor> findByCompanyNameAndDeletedTrue(String name);

	Optional<Distributor> findByEmail(String email);
}
