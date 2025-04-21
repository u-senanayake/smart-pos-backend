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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "distributor")
public class Distributor {

	@Id
	@Column(name = "distributor_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer distributorId;

	@NotBlank(message = "Company name cannot be blank")
	@Size(max = 60, message = "Company name must be less than 60 characters")
	@Column(name = "company_name")
	private String companyName;

	@NotNull(message = "Email is required")
	@Email(message = "Email should be valid")
	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@NotNull(message = "Phone number cannot be blank")
	@Size(min = 10, max = 12, message = "Phone number should be 12 characters")
	@Column(name = "phone_no_1", nullable = false)
	private String phoneNo1;

	@Size(min = 10, max = 12, message = "Phone number should be 12 characters")
	@Column(name = "phone_no_2")
	private String phoneNo2;

	@Size(max = 250, message = "Address must be less than 250 characters")
	@Column(name = "address")
	private String address;

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
