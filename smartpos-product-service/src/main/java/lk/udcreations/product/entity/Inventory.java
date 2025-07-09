package lk.udcreations.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inventory")
public class Inventory {

	@Id
	@Column(name = "inventory_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer inventoryId;

	@Column(name = "product_id", nullable = false)
	@NotNull(message = "Product is required")
	private Integer productId;

	@Column(name = "quantity", nullable = false, columnDefinition = "int default 0")
	private Integer quantity;

	@Column(name = "stock_alert_level", nullable = false, columnDefinition = "int default 0")
	private Integer stockAlertLevel;

	@Column(name = "stock_warning_level", nullable = false, columnDefinition = "int default 0")
	private Integer stockWarningLevel;

	@Column(name = "last_updated", columnDefinition = "timestamp default CURRENT_TIMESTAMP")
	private LocalDateTime lastUpdated;

	@PrePersist
	protected void onCreate() {
		this.lastUpdated = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.lastUpdated = LocalDateTime.now();
	}
}
