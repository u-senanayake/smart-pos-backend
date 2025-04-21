package lk.udcreations.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lk.udcreations.user.entity.Users;


@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {

	Optional<Users> findByUsernameAndDeletedFalse(String userName);

	Optional<Users> findByUsernameAndDeletedTrue(String userName);

	Optional<Users> findByUserIdAndDeletedFalse(Integer userId);

	Optional<Users> findByUsername(String username);

	List<Users> findByDeletedFalse();

	boolean existsByEmailAndDeletedFalse(String email);

	boolean existsByUsername(String username);


}
