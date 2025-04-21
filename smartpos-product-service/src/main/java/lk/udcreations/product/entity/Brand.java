package lk.udcreations.product.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "brand")
public class Brand {

	@Id
	@Column(name = "brand_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer brandId;

	@NotBlank(message = "Brand name cannot be blank")
	@Size(max = 20, message = "Brand name must be less than 20 characters")
	@Column(name = "name")
	private String name;

	@Size(max = 250, message = "Description must be less than 250 characters")
	@Column(name = "description")
	private String description;

	@NotNull(message = "Enabled flag cannot be null")
	@Column(name = "enabled")
	private boolean enabled;

	@Column(name = "deleted")
	private boolean deleted = false;

	@Column(name = "created_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at", nullable = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime deletedAt;
	
	@Column(name = "created_user_id", nullable = true)
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
