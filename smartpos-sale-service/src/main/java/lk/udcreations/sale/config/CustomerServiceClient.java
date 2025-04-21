package lk.udcreations.sale.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lk.udcreations.common.dto.customer.CustomerDTO;

@FeignClient(name = "customer-service")
public interface CustomerServiceClient {

	@GetMapping("/api/v1/customers/{id}")
	public CustomerDTO getCustomerById(@PathVariable Integer id);

}
