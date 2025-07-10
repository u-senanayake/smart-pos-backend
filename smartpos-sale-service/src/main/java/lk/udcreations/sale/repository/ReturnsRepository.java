package lk.udcreations.sale.repository;

import lk.udcreations.sale.entity.Returns;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnsRepository extends JpaRepository<Returns, Integer> {

	List<Returns> findBySaleId(Integer saleId);
}
