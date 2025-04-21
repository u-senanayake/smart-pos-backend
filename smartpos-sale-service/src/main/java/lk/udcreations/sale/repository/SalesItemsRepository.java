package lk.udcreations.sale.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lk.udcreations.sale.entity.SalesItems;

@Repository
public interface SalesItemsRepository extends JpaRepository<SalesItems, Integer> {

	Optional<SalesItems> findByProductIdAndSaleId(Integer productId, Integer saleId);

	List<SalesItems> findBySaleId(Integer saleId);
}
