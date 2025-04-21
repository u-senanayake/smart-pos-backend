package lk.udcreations.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import lk.udcreations.product.entity.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

	Optional<Inventory> findByProductId(Integer id);

	List<Inventory> findByQuantityLessThan(int num);

	@Query(value = "SELECT a.* FROM inventory a INNER JOIN inventory b ON a.inventory_id = b.inventory_id "
			+ "WHERE a.quantity <= b.stock_alert_level", nativeQuery = true)
	List<Inventory> findInventoryBelowStockAlertLevel();

	@Query(value = "SELECT a.* FROM inventory a INNER JOIN inventory b ON a.inventory_id = b.inventory_id "
			+ "WHERE a.quantity <= b.stock_warning_level", nativeQuery = true)
	List<Inventory> findInventoryBelowStockWarningLevel();

}
