package lk.udcreations.sale.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sales")
public class Sales {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sale_id")
	private Integer saleId;

	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "customer_id")
	private Integer customerId;

	@Column(name = "total_amount", nullable = false)
	private BigDecimal totalAmount;

	@Column(name = "total_item_count", nullable = false)
	private int totalItemCount;

	@Column(name = "sale_date_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime saleDateTime;

	@Column(name = "payment_status", nullable = false, length = 50)
	private String paymentStatus;

	@Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
		if ("PAID".equalsIgnoreCase(this.paymentStatus)) {
			this.saleDateTime = LocalDateTime.now();
		}
	}
}
