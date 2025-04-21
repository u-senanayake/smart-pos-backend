package lk.udcreations.product.security;

import org.springframework.stereotype.Component;

import lk.udcreations.common.dto.user.UsersDTO;
import lk.udcreations.product.config.UserServiceClient;

@Component
public class AuthUtils {

	
	private final UserServiceClient userServiceClient;

	public AuthUtils(UserServiceClient userServiceClient) {
		super();
		this.userServiceClient = userServiceClient;
	}

	public String getLoggedInUsername() {
		// return SecurityContextHolder.getContext().getAuthentication().getName();
		return "admin_user";
	}

	public UsersDTO getLoggedInUser() {
		String username = getLoggedInUsername();
		
		return userServiceClient.getUserDetails(username);
		
//		return userRepository.findByUsername(username)
//				.orElseThrow(() -> new NotFoundException("Logged-in user not found"));
	}

	public UsersDTO getUserById(Integer userId) {
		return userServiceClient.getUserById(userId);
	}


}
