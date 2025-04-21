package lk.udcreations.sale.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import lk.udcreations.sale.entity.Returns;

public interface ReturnsRepository extends JpaRepository<Returns, Integer> {

	List<Returns> findBySaleId(Integer saleId);

	List<Returns> findBySalesItemId(Integer saleItemId);
}
