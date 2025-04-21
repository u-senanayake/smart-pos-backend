package lk.udcreations.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lk.udcreations.user.entity.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

	Optional<Role> findByRoleName(String roleName);

	Optional<Role> findByRoleId(Integer roleId);

	Optional<Role> findByRoleNameAndDeletedTrue(String roleName);

	List<Role> findByDeletedFalse();

}
