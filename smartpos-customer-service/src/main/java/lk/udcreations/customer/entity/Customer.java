package lk.udcreations.customer.entity;

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

@Entity
@Table(name = "customer")
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "customer_id")
	private Integer customerId;

//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "customer_group_id", nullable = false)
//	@NotNull(message = "Customer group is required")
//	private CustomerGroup customerGroup;

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

//	@ManyToOne
//	@JoinColumn(name = "created_user_id")
//	private Users createdUser;

	@Column(name = "created_user_id")
	private Integer createdUserId;

//	@ManyToOne
//	@JoinColumn(name = "updated_user_id")
//	private Users updatedUser;

	@Column(name = "updated_user_id")
	private Integer updatedUserId;

//	@ManyToOne
//	@JoinColumn(name = "deleted_user_id")
//	private Users deletedUser;

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

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

//	public CustomerGroup getCustomerGroup() {
//		return customerGroup;
//	}
//
//	public void setCustomerGroup(CustomerGroup customerGroup) {
//		this.customerGroup = customerGroup;
//	}

	public String getUsername() {
		return username;
	}

	public Integer getCustomerGroupId() {
		return customerGroupId;
	}

	public void setCustomerGroupId(Integer customerGroupId) {
		this.customerGroupId = customerGroupId;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNo1() {
		return phoneNo1;
	}

	public void setPhoneNo1(String phoneNo1) {
		this.phoneNo1 = phoneNo1;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
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

	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(LocalDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}

	public Integer getCreatedUserId() {
		return createdUserId;
	}

	public void setCreatedUserId(Integer createdUserId) {
		this.createdUserId = createdUserId;
	}

	public Integer getUpdatedUserId() {
		return updatedUserId;
	}

	public void setUpdatedUserId(Integer updatedUserId) {
		this.updatedUserId = updatedUserId;
	}

	public Integer getDeletedUserId() {
		return deletedUserId;
	}

	public void setDeletedUserId(Integer deletedUserId) {
		this.deletedUserId = deletedUserId;
	}

	public Customer(Integer customerId, @NotNull(message = "Customer group is required") Integer customerGroupId,
			@NotNull(message = "Username is required") @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters") String username,
			@NotNull(message = "First name is required") @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters") String firstName,
			@NotNull(message = "Last name is required") @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters") String lastName,
			@NotNull(message = "Email is required") @Email(message = "Email should be valid") String email,
			@NotNull(message = "Phone number cannot be blank") @Size(min = 10, max = 10, message = "Phone number should be 10 characters") String phoneNo1,
			@Size(max = 255, message = "Address must be less than 255 characters") String address, boolean enabled,
			boolean locked, boolean deleted, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt,
			Integer createdUserId, Integer updatedUserId, Integer deletedUserId) {
		super();
		this.customerId = customerId;
		this.customerGroupId = customerGroupId;
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNo1 = phoneNo1;
		this.address = address;
		this.enabled = enabled;
		this.locked = locked;
		this.deleted = deleted;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.deletedAt = deletedAt;
		this.createdUserId = createdUserId;
		this.updatedUserId = updatedUserId;
		this.deletedUserId = deletedUserId;
	}

	public Customer() {
		super();
	}

}
