package lk.udcreations.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lk.udcreations.product.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

	List<Product> findByDeletedFalse();

	Optional<Product> findByProductNameAndDeletedFalse(String name);

	Optional<Product> findByProductNameAndDeletedTrue(String name);

	Optional<Product> findByIdAndDeletedTrue(Integer id);

	Optional<Product> findByProductIdAndDeletedTrue(String productId);

	Optional<Product> findByIdAndEnabledTrue(Integer id);

	Optional<Product> findByProductIdAndEnabledTrue(String productId);
}
