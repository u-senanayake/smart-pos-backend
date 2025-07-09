package lk.udcreations.customer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "customer")
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "customer_id")
	private Integer customerId;

	@Column(name = "customer_group_id", nullable = false)
	@NotNull(message = "Customer group is required")
	private Integer customerGroupId;

	@NotNull(message = "Username is required")
	@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
	@Column(name = "username", nullable = false, unique = true)
	private String username;

	@NotNull(message = "First name is required")
	@Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
	@Column(name = "first_name", nullable = false)
	private String firstName;

	@NotNull(message = "Last name is required")
	@Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
	@Column(name = "last_name", nullable = false)
	private String lastName;

	@NotNull(message = "Email is required")
	@Email(message = "Email should be valid")
	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@NotNull(message = "Phone number cannot be blank")
	@Size(min = 10, max = 10, message = "Phone number should be 10 characters")
	@Column(name = "phone_no_1", nullable = false)
	private String phoneNo1;

	@Size(max = 255, message = "Address must be less than 255 characters")
	@Column(name = "address")
	private String address;

	@Column(name = "enabled", nullable = false)
	private boolean enabled = true;

	@Column(name = "locked", nullable = false)
	private boolean locked = false;

	@Column(name = "deleted", nullable = false)
	private boolean deleted = false;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
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
