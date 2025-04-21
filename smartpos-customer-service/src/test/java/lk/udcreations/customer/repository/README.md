# Repository Tests

This document provides guidelines for testing the repository layer in the SmartPOS Customer Service.

## CustomerGroupRepository Tests

The `CustomerGroupRepository` interface should be tested to verify the following methods:

1. `findByDeletedFalse()` - Should return all customer groups that are not deleted
2. `findByNameAndDeletedFalse(String name)` - Should find a customer group by name that is not deleted
3. `findByNameAndDeletedTrue(String name)` - Should find a customer group by name that is deleted

### Test Cases

#### Test findByDeletedFalse()
```java
@Test
void testFindByDeletedFalse() {
    // Create test data
    CustomerGroup vipGroup = createCustomerGroup("VIP Customers", "High-value customers", false);
    CustomerGroup regularGroup = createCustomerGroup("Regular Customers", "Normal customers", false);
    CustomerGroup deletedGroup = createCustomerGroup("Deleted Group", "This group is deleted", true);
    
    // Execute the query
    List<CustomerGroup> activeGroups = customerGroupRepository.findByDeletedFalse();
    
    // Verify results
    assertEquals(2, activeGroups.size());
    assertTrue(activeGroups.stream().anyMatch(group -> group.getName().equals("VIP Customers")));
    assertTrue(activeGroups.stream().anyMatch(group -> group.getName().equals("Regular Customers")));
    assertFalse(activeGroups.stream().anyMatch(group -> group.getName().equals("Deleted Group")));
}
```

#### Test findByNameAndDeletedFalse()
```java
@Test
void testFindByNameAndDeletedFalse() {
    // Create test data
    CustomerGroup vipGroup = createCustomerGroup("VIP Customers", "High-value customers", false);
    
    // Execute the query
    Optional<CustomerGroup> group = customerGroupRepository.findByNameAndDeletedFalse("VIP Customers");
    
    // Verify results
    assertTrue(group.isPresent());
    assertEquals("VIP Customers", group.get().getName());
    assertEquals("High-value customers", group.get().getDescription());
}

@Test
void testFindByNameAndDeletedFalse_NotFound() {
    // Create test data
    CustomerGroup vipGroup = createCustomerGroup("VIP Customers", "High-value customers", false);
    
    // Execute the query
    Optional<CustomerGroup> group = customerGroupRepository.findByNameAndDeletedFalse("NonExistentGroup");
    
    // Verify results
    assertFalse(group.isPresent());
}
```

#### Test findByNameAndDeletedTrue()
```java
@Test
void testFindByNameAndDeletedTrue() {
    // Create test data
    CustomerGroup vipGroup = createCustomerGroup("VIP Customers", "High-value customers", false);
    CustomerGroup deletedGroup = createCustomerGroup("Deleted Group", "This group is deleted", true);
    
    // Execute the query
    Optional<CustomerGroup> group = customerGroupRepository.findByNameAndDeletedTrue("Deleted Group");
    
    // Verify results
    assertTrue(group.isPresent());
    assertEquals("Deleted Group", group.get().getName());
    assertTrue(group.get().isDeleted());
}

@Test
void testFindByNameAndDeletedTrue_NotFound() {
    // Create test data
    CustomerGroup vipGroup = createCustomerGroup("VIP Customers", "High-value customers", false);
    CustomerGroup deletedGroup = createCustomerGroup("Deleted Group", "This group is deleted", true);
    
    // Execute the query
    Optional<CustomerGroup> group = customerGroupRepository.findByNameAndDeletedTrue("VIP Customers");
    
    // Verify results
    assertFalse(group.isPresent());
}
```

## CustomerRepository Tests

The `CustomerRepository` interface should be tested to verify the following methods:

1. `findByUsernameAndDeletedFalse(String userName)` - Should find a customer by username that is not deleted
2. `findByUsernameAndDeletedTrue(String userName)` - Should find a customer by username that is deleted
3. `findByFirstName(String fistName)` - Should find a customer by first name
4. `findByLastName(String lastName)` - Should find a customer by last name
5. `findByFirstNameAndLastName(String firstName, String lastName)` - Should find a customer by first and last name
6. `findByEmail(String email)` - Should find a customer by email
7. `findByDeletedFalse()` - Should return all customers that are not deleted
8. `findByCustomerIdAndDeletedFalse(Integer userId)` - Should find a customer by ID that is not deleted
9. `existsByEmail(String email)` - Should check if a customer with the given email exists
10. `existsByUsername(String username)` - Should check if a customer with the given username exists
11. `findByUsername(String username)` - Should find a customer by username

### Test Cases

#### Test findByUsernameAndDeletedFalse()
```java
@Test
void testFindByUsernameAndDeletedFalse() {
    // Create test data
    Customer johnDoe = createCustomer("john_doe", "John", "Doe", "john.doe@example.com", false);
    
    // Execute the query
    Optional<Customer> customer = customerRepository.findByUsernameAndDeletedFalse("john_doe");
    
    // Verify results
    assertTrue(customer.isPresent());
    assertEquals("john_doe", customer.get().getUsername());
    assertEquals("John", customer.get().getFirstName());
    assertEquals("Doe", customer.get().getLastName());
}
```

And similar test cases for the other methods.

## Helper Methods

```java
private CustomerGroup createCustomerGroup(String name, String description, boolean deleted) {
    CustomerGroup group = new CustomerGroup();
    group.setName(name);
    group.setDescription(description);
    group.setEnabled(true);
    group.setDeleted(deleted);
    group.setCreatedAt(LocalDateTime.now());
    group.setUpdatedAt(LocalDateTime.now());
    if (deleted) {
        group.setDeletedAt(LocalDateTime.now());
        group.setDeletedUser(1);
    }
    group.setCreatedUser(1);
    group.setUpdatedUser(1);
    
    return customerGroupRepository.save(group);
}

private Customer createCustomer(String username, String firstName, String lastName, String email, boolean deleted) {
    Customer customer = new Customer();
    customer.setCustomerGroupId(1);
    customer.setUsername(username);
    customer.setFirstName(firstName);
    customer.setLastName(lastName);
    customer.setEmail(email);
    customer.setPhoneNo1("1234567890");
    customer.setEnabled(true);
    customer.setDeleted(deleted);
    customer.setCreatedUserId(1);
    customer.setUpdatedUserId(1);
    if (deleted) {
        customer.setDeletedAt(LocalDateTime.now());
        customer.setDeletedUserId(1);
    }
    
    return customerRepository.save(customer);
}
```