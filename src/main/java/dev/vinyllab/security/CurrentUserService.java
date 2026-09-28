package dev.vinyllab.security;

import dev.vinyllab.entity.UserAccount;
import dev.vinyllab.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurrentUserService {

  private final UserRepository users;
  private final UserDetailsService userDetailsService;
  private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

  public Optional<Long> id() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!known(authentication)) {
      return Optional.empty();
    }
    return users.findByUsernameIgnoreCase(authentication.getName()).map(UserAccount::getId);
  }

  public Long requireId() {
    return id().orElseThrow(() -> new IllegalStateException("Потрібна автентифікація"));
  }

  public void authenticate(String username, HttpServletRequest request, HttpServletResponse response) {
    UserDetails details = userDetailsService.loadUserByUsername(username);
    UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.authenticated(
        details,
        details.getPassword(),
        details.getAuthorities()
    );
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(token);
    SecurityContextHolder.setContext(context);
    securityContextRepository.saveContext(context, request, response);
  }

  private boolean known(Authentication authentication) {
    return authentication != null
        && authentication.isAuthenticated()
        && !(authentication instanceof AnonymousAuthenticationToken);
  }
}
