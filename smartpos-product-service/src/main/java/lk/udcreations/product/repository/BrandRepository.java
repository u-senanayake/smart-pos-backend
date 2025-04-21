package lk.udcreations.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lk.udcreations.product.entity.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {

	List<Brand> findByDeletedFalse();

	Optional<Brand> findByNameAndDeletedFalse(String name);

	Optional<Brand> findByNameAndDeletedTrue(String name);

}
