package lk.udcreations.user.security;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lk.udcreations.common.dto.user.UsersDTO;

public class CustomUserDetails implements UserDetails {

	@Serial
	private static final long serialVersionUID = 1L;
	private final UsersDTO user;

	public CustomUserDetails(UsersDTO user) {
		super();
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		String roleName = "ROLE_" + user.getRole().getRoleName().toUpperCase();
		return Collections.singletonList(new SimpleGrantedAuthority(roleName));
	}

	@Override
	public String getPassword() {
		// return user.getPassword();
		return null;
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

    @Override
	public boolean isAccountNonLocked() {
		return !user.isLocked();
	}

    @Override
	public boolean isEnabled() {
		return user.isEnabled();
	}

}
