package lk.udcreations.product.config;

import lk.udcreations.common.dto.user.UsersDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {
	
	@GetMapping("/api/v1/users/username/{username}")
    UsersDTO getUserDetails(@PathVariable String username);

	@GetMapping("/api/v1/users/{userId}")
	UsersDTO getUserById(@PathVariable Integer userId);
}
