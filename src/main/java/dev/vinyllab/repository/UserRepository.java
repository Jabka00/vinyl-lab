package dev.vinyllab.repository;

import dev.vinyllab.entity.UserAccount;
import dev.vinyllab.model.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserAccount, Long> {

  Optional<UserAccount> findByUsernameIgnoreCase(String username);

  boolean existsByUsernameIgnoreCase(String username);

  boolean existsByEmailIgnoreCase(String email);

  long countByRole(Role role);
}
