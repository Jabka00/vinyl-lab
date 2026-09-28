package dev.vinyllab.controller.admin;

import dev.vinyllab.exception.ConflictException;
import dev.vinyllab.model.Role;
import dev.vinyllab.security.CurrentUserService;
import dev.vinyllab.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

  private final UserService users;
  private final CurrentUserService currentUser;

  @GetMapping
  public String list(Model model) {
    model.addAttribute("users", users.list());
    model.addAttribute("currentId", currentUser.requireId());
    return "admin/users";
  }

  @PostMapping("/{id}/role")
  public String role(@PathVariable Long id, @RequestParam Role role, RedirectAttributes redirect) {
    try {
      users.changeRole(id, role, currentUser.requireId());
      redirect.addFlashAttribute("success", "Роль оновлено");
    } catch (ConflictException exception) {
      redirect.addFlashAttribute("error", exception.getMessage());
    }
    return "redirect:/admin/users";
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable Long id, RedirectAttributes redirect) {
    try {
      users.delete(id, currentUser.requireId());
      redirect.addFlashAttribute("success", "Користувача видалено");
    } catch (ConflictException exception) {
      redirect.addFlashAttribute("error", exception.getMessage());
    }
    return "redirect:/admin/users";
  }
}
