package lk.udcreations.user.security;

import org.springframework.stereotype.Component;

import lk.udcreations.user.entity.Users;
import lk.udcreations.user.exception.NotFoundException;
import lk.udcreations.user.repository.UserRepository;

@Component
public class AuthUtils {

	private final UserRepository userRepository;

	public AuthUtils(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}

	public String getLoggedInUsername() {
//		String user = SecurityContextHolder.getContext().getAuthentication().getName();
		String user = "admin_user";
		return user;
	}

	public Users getLoggedInUser() {
		String username = getLoggedInUsername();
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new NotFoundException("Logged-in user not found"));
	}

}
