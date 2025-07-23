package lk.udcreations.user.util.relationcheck;


import lk.udcreations.user.entity.Role;
import lk.udcreations.user.repository.RoleRepository;
import org.springframework.stereotype.Component;


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

	// check role name
	// check enabled
	// check deleted
}
