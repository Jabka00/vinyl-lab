package dev.vinyllab.controller.admin;

import dev.vinyllab.exception.ConflictException;
import dev.vinyllab.form.GenreForm;
import dev.vinyllab.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/genres")
@RequiredArgsConstructor
public class AdminGenreController {

  private final GenreService genres;

  @GetMapping
  public String list(Model model) {
    model.addAttribute("genres", genres.list());
    return "admin/genres";
  }

  @GetMapping("/new")
  public String createForm(Model model) {
    return form(model, new GenreForm(), "Новий жанр", false);
  }

  @PostMapping
  public String create(
      @Valid @ModelAttribute GenreForm genreForm,
      BindingResult result,
      Model model,
      RedirectAttributes redirect
  ) {
    if (result.hasErrors()) {
      return form(model, genreForm, "Новий жанр", false);
    }
    try {
      genres.create(genreForm);
    } catch (ConflictException exception) {
      result.reject("conflict", exception.getMessage());
      return form(model, genreForm, "Новий жанр", false);
    }
    redirect.addFlashAttribute("success", "Жанр додано");
    return "redirect:/admin/genres";
  }

  @GetMapping("/{id}/edit")
  public String editForm(@PathVariable Long id, Model model) {
    model.addAttribute("entityId", id);
    return form(model, genres.form(id), "Редагувати жанр", true);
  }

  @PostMapping("/{id}")
  public String update(
      @PathVariable Long id,
      @Valid @ModelAttribute GenreForm genreForm,
      BindingResult result,
      Model model,
      RedirectAttributes redirect
  ) {
    model.addAttribute("entityId", id);
    if (result.hasErrors()) {
      return form(model, genreForm, "Редагувати жанр", true);
    }
    try {
      genres.update(id, genreForm);
    } catch (ConflictException exception) {
      result.reject("conflict", exception.getMessage());
      return form(model, genreForm, "Редагувати жанр", true);
    }
    redirect.addFlashAttribute("success", "Жанр оновлено");
    return "redirect:/admin/genres";
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable Long id, RedirectAttributes redirect) {
    try {
      genres.delete(id);
      redirect.addFlashAttribute("success", "Жанр видалено");
    } catch (ConflictException exception) {
      redirect.addFlashAttribute("error", exception.getMessage());
    }
    return "redirect:/admin/genres";
  }

  private String form(Model model, GenreForm genreForm, String heading, boolean editing) {
    model.addAttribute("genreForm", genreForm);
    model.addAttribute("heading", heading);
    model.addAttribute("editing", editing);
    return "admin/genre-form";
  }
}
