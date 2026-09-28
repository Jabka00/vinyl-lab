package dev.vinyllab.service;

import dev.vinyllab.entity.UserAccount;
import dev.vinyllab.exception.ConflictException;
import dev.vinyllab.exception.NotFoundException;
import dev.vinyllab.form.RegisterForm;
import dev.vinyllab.mapper.UserMapper;
import dev.vinyllab.model.Role;
import dev.vinyllab.repository.AlbumRepository;
import dev.vinyllab.repository.CollectionItemRepository;
import dev.vinyllab.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

  private final UserRepository users;
  private final CollectionItemRepository collectionItems;
  private final AlbumRepository albums;
  private final UserMapper userMapper;
  private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(String username) {
    UserAccount user = users.findByUsernameIgnoreCase(username)
        .orElseThrow(() -> new UsernameNotFoundException("Користувача не знайдено"));
    return User.withUsername(user.getUsername())
        .password(user.getPasswordHash())
        .authorities(user.getRole().authority())
        .build();
  }

  @Transactional
  public UserAccount register(RegisterForm form) {
    String username = form.getUsername().trim();
    String email = form.getEmail().trim();
    if (users.existsByUsernameIgnoreCase(username)) {
      throw new ConflictException("Це ім'я вже зайняте");
    }
    if (users.existsByEmailIgnoreCase(email)) {
      throw new ConflictException("Ця пошта вже зареєстрована");
    }
    return users.save(userMapper.toNewUser(form, passwordEncoder.encode(form.getPassword())));
  }

  public List<dev.vinyllab.dto.UserCard> list() {
    return users.findAll().stream()
        .sorted(java.util.Comparator.comparing(UserAccount::getUsername, String.CASE_INSENSITIVE_ORDER))
        .map(userMapper::toCard)
        .toList();
  }

  @Transactional
  public void changeRole(Long id, Role role, Long actorId) {
    if (id.equals(actorId)) {
      throw new ConflictException("Не можна змінити власну роль");
    }
    UserAccount user = users.findById(id)
        .orElseThrow(() -> new NotFoundException("Користувача не знайдено"));
    if (user.getRole() == Role.ADMIN && role == Role.USER && users.countByRole(Role.ADMIN) <= 1) {
      throw new ConflictException("Має лишитися хоча б один адміністратор");
    }
    user.setRole(role);
  }

  @Transactional
  public void delete(Long id, Long actorId) {
    if (id.equals(actorId)) {
      throw new ConflictException("Не можна видалити власний обліковий запис");
    }
    UserAccount user = users.findById(id)
        .orElseThrow(() -> new NotFoundException("Користувача не знайдено"));
    if (user.getRole() == Role.ADMIN && users.countByRole(Role.ADMIN) <= 1) {
      throw new ConflictException("Має лишитися хоча б один адміністратор");
    }
    albums.deleteRatingsForUser(id);
    collectionItems.deleteForOwner(id);
    users.delete(user);
  }
}
