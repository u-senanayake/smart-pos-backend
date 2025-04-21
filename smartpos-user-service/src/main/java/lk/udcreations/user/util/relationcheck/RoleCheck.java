package lk.udcreations.user.util.relationcheck;


import org.springframework.stereotype.Component;

import lk.udcreations.user.entity.Role;
import lk.udcreations.user.repository.RoleRepository;


@Component
public class RoleCheck {

	private final RoleRepository repository;

	public RoleCheck(RoleRepository repository) {
		super();
		this.repository = repository;
	}

	public boolean isRoleNameExists(Role role) {
		return repository.findByRoleName(role.getRoleName()).isPresent();
	}

	// role name check
	// check enabled
	// check deleted
}
