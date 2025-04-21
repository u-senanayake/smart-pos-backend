package lk.udcreations.customer.entity;

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

	public Integer getCustomerGroupId() {
		return customerGroupId;
	}

	public void setCustomerGroupId(Integer customerGroupId) {
		this.customerGroupId = customerGroupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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

	public Integer getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(Integer createdUser) {
		this.createdUser = createdUser;
	}

	public Integer getUpdatedUser() {
		return updatedUser;
	}

	public void setUpdatedUser(Integer updatedUser) {
		this.updatedUser = updatedUser;
	}

	public Integer getDeletedUser() {
		return deletedUser;
	}

	public void setDeletedUser(Integer deletedUser) {
		this.deletedUser = deletedUser;
	}

	public CustomerGroup(Integer customerGroupId,
			@NotBlank(message = "Category name cannot be blank") @Size(max = 20, message = "Category name must be less than 20 characters") String name,
			@Size(max = 250, message = "Description must be less than 250 characters") String description,
			@NotNull(message = "Enabled flag cannot be null") boolean enabled, boolean deleted, LocalDateTime createdAt,
			LocalDateTime updatedAt, LocalDateTime deletedAt, Integer createdUser, Integer updatedUser,
			Integer deletedUser) {
		super();
		this.customerGroupId = customerGroupId;
		this.name = name;
		this.description = description;
		this.enabled = enabled;
		this.deleted = deleted;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.deletedAt = deletedAt;
		this.createdUser = createdUser;
		this.updatedUser = updatedUser;
		this.deletedUser = deletedUser;
	}

	public CustomerGroup() {
		super();
	}

	public CustomerGroup(Integer customerGroupId) {
		super();
		this.customerGroupId = customerGroupId;
	}

}
