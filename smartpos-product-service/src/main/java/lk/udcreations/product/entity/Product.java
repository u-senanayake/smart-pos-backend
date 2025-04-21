package lk.udcreations.product.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lk.udcreations.product.config.ProductListener;
import lombok.Data;

@Data
@Entity
@Table(name = "product")
@EntityListeners(ProductListener.class)
public class Product {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "product_id", unique = true, length = 10, nullable = false)
	private String productId;

	@Column(name = "sku", unique = true, length = 20, nullable = false)
	private String sku;

	@NotBlank(message = "Product name cannot be blank")
	@Size(max = 50, message = "Product name must be less than 50 characters")
	@Column(name = "product_name", nullable = false, length = 100)
	private String productName;

	@NotBlank(message = "Description cannot be blank")
	@Size(max = 100, message = "Description must be less than 100 characters")
	@Column(name = "description")
	private String description;

	@Column(name = "category_id")
	private Integer categoryId;
	
	@JoinColumn(name = "distributor_id")
	private Integer distributorId;

	@Column(name = "price", precision = 10, scale = 2, nullable = false)
	private BigDecimal price;

	@Column(name = "cost_price", precision = 10, scale = 2, nullable = false)
	private BigDecimal costPrice;

	@Column(name = "min_price", precision = 10, scale = 2)
	private BigDecimal minPrice;

	@Column(name = "manufacture_date")
	private LocalDate manufactureDate;

	@Column(name = "expire_date")
	private LocalDate expireDate;

	@NotNull(message = "Enabled flag cannot be null")
	@Column(name = "enabled", columnDefinition = "boolean default true")
	private boolean enabled;

	@Column(name = "deleted", columnDefinition = "boolean default false")
	private boolean deleted;

	@Column(name = "created_at", columnDefinition = "timestamp default CURRENT_TIMESTAMP", updatable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;

	@Column(name = "updated_at", columnDefinition = "timestamp default CURRENT_TIMESTAMP")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at", nullable = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime deletedAt;

	@Column(name = "created_user_id")
	private Integer createdUserId;

	@Column(name = "updated_user_id")
	private Integer updatedUserId;

	@Column(name = "deleted_user_id")
	private Integer deletedUserId;
	
	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}
