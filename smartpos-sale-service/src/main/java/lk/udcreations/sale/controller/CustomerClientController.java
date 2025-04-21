package lk.udcreations.sale.controller;

import org.springframework.stereotype.Component;

import lk.udcreations.common.dto.customer.CustomerDTO;
import lk.udcreations.sale.config.CustomerServiceClient;

@Component
public class CustomerClientController {

	private final CustomerServiceClient customerServiceClient;

	public CustomerClientController(CustomerServiceClient customerServiceClient) {
		super();
		this.customerServiceClient = customerServiceClient;
	}

	public CustomerDTO getCustomerById(Integer customerId) {
		return customerServiceClient.getCustomerById(customerId);
	}
}
