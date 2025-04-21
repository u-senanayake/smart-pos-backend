package lk.udcreations.sale.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

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

	public Integer getReturnId() {
		return returnId;
	}

	public void setReturnId(Integer returnId) {
		this.returnId = returnId;
	}

	public Integer getSaleId() {
		return saleId;
	}

	public void setSaleId(Integer saleId) {
		this.saleId = saleId;
	}

	public int getQuantity() {
		return quantity;
	}

	public Integer getSalesItemId() {
		return salesItemId;
	}

	public void setSalesItemId(Integer salesItemId) {
		this.salesItemId = salesItemId;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public BigDecimal getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}

	public LocalDateTime getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(LocalDateTime returnDate) {
		this.returnDate = returnDate;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

}
