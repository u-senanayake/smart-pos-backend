package lk.udcreations.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class Users {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Integer userId;

	@NotNull(message = "Username is required")
	@Size(min = 5, max = 50, message = "Username must be between 5 and 50 characters")
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

	@Size(max = 255, message = "Address must be less than 255 characters")
	@Column(name = "address")
	private String address;

	@NotNull(message = "Phone number cannot be blank")
	@Size(min = 10, max = 10, message = "Phone number should be 10 characters")
	@Column(name = "phone_no_1", nullable = false)
	private String phoneNo1;

	@Column(name = "phone_no_2")
	private String phoneNo2;

//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "role_id", nullable = false)
//	@NotNull(message = "Role is required")
//	private Role role;

	@Column(name = "role_id", nullable = false)
	@NotNull(message = "Role is required")
	private Integer roleId;

	@Column(name = "password", nullable = false)
	// @JsonIgnore //TODO remove comment
	private String password;

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
