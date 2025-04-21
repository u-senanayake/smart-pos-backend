package lk.udcreations.sale.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lk.udcreations.sale.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

	Optional<Payment> findBySaleId(Integer saleId);
}
