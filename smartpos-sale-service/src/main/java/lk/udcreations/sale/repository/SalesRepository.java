package lk.udcreations.sale.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lk.udcreations.sale.entity.Sales;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Integer> {

	Optional<Sales> findBySaleId(Integer saleId);

	List<Sales> findByCustomerId(Integer customerId);

	List<Sales> findByPaymentStatus(String status);
	
	List<Sales> findByPaymentStatusNot(String status);
}
