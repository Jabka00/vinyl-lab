package dev.vinyllab.controller;

import dev.vinyllab.exception.ConflictException;
import dev.vinyllab.form.RegisterForm;
import dev.vinyllab.security.CurrentUserService;
import dev.vinyllab.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

  private final UserService users;
  private final CurrentUserService currentUser;

  @GetMapping("/login")
  public String login() {
    return "auth/login";
  }

  @GetMapping("/register")
  public String registerForm(Model model) {
    model.addAttribute("registerForm", new RegisterForm());
    return "auth/register";
  }

  @PostMapping("/register")
  public String register(
      @Valid @ModelAttribute RegisterForm registerForm,
      BindingResult result,
      HttpServletRequest request,
      HttpServletResponse response
  ) {
    if (!registerForm.passwordsMatch()) {
      result.rejectValue("confirmPassword", "match", "Паролі не збігаються");
    }
    if (result.hasErrors()) {
      return "auth/register";
    }
    try {
      var user = users.register(registerForm);
      currentUser.authenticate(user.getUsername(), request, response);
    } catch (ConflictException exception) {
      result.reject("taken", exception.getMessage());
      return "auth/register";
    }
    return "redirect:/collection";
  }
}
