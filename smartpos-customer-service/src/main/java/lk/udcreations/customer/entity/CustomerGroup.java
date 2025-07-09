package lk.udcreations.customer.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "customergroup")
public class CustomerGroup {

	@Id
	@Column(name = "customer_group_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer customerGroupId;

	@NotBlank(message = "Category name cannot be blank")
	@Size(max = 20, message = "Category name must be less than 20 characters")
	@Column(name = "name")
	private String name;

	@Size(max = 250, message = "Description must be less than 250 characters")
	@Column(name = "description")
	private String description;

	@NotNull(message = "Enabled flag cannot be null")
	@Column(name = "enabled")
	private boolean enabled;

	@Column(name = "deleted")
	private boolean deleted;

	@Column(name = "created_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at", nullable = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime deletedAt;

	@Column(name = "created_user_id")
	private Integer createdUser;

	@Column(name = "updated_user_id")
	private Integer updatedUser;

	@Column(name = "deleted_user_id")
	private Integer deletedUser;

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
