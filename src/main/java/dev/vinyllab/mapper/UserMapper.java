package dev.vinyllab.mapper;

import dev.vinyllab.dto.UserCard;
import dev.vinyllab.entity.UserAccount;
import dev.vinyllab.form.RegisterForm;
import dev.vinyllab.model.Role;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public UserAccount toNewUser(RegisterForm form, String passwordHash) {
    UserAccount user = new UserAccount();
    user.setUsername(form.getUsername().trim());
    user.setEmail(form.getEmail().trim().toLowerCase(Locale.ROOT));
    user.setPasswordHash(passwordHash);
    user.setRole(Role.USER);
    return user;
  }

  public UserCard toCard(UserAccount user) {
    return new UserCard(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.getCreatedAt());
  }
}
