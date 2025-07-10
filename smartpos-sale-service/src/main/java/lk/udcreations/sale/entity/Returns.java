package lk.udcreations.sale.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "returns")
public class Returns {

	@Id
	@Column(name = "return_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer returnId;

	@Column(name = "sale_id", nullable = false)
	private Integer saleId;

	@Column(name = "sales_item_id", nullable = false)
	private Integer salesItemId;

	@Column(name = "quantity", nullable = false)
	private int quantity;

	@Column(name = "reason", length = 255)
	private String reason;

	@Column(name = "refund_amount", precision = 10, scale = 2, nullable = false)
	private BigDecimal refundAmount;

	@Column(name = "return_date", nullable = false)
	private LocalDateTime returnDate;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	public void prePersist() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	public void preUpdate() {
		updatedAt = LocalDateTime.now();
	}

}
