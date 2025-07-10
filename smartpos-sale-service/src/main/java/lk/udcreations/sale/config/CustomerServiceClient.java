package lk.udcreations.sale.config;

import lk.udcreations.common.dto.customer.CustomerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service")
public interface CustomerServiceClient {

	@GetMapping("/api/v1/customers/{id}")
	CustomerDTO getCustomerById(@PathVariable Integer id);

}
