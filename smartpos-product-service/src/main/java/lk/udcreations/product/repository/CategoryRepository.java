package lk.udcreations.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lk.udcreations.product.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

	List<Category> findByDeletedFalse();

	Optional<Category> findByNameAndDeletedFalse(String name);

	Optional<Category> findByNameAndDeletedTrue(String name);
}
