package dev.vinyllab.controller.admin;

import dev.vinyllab.exception.ConflictException;
import dev.vinyllab.form.ArtistForm;
import dev.vinyllab.service.ArtistService;
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
@RequestMapping("/admin/artists")
@RequiredArgsConstructor
public class AdminArtistController {

  private final ArtistService artists;

  @GetMapping
  public String list(Model model) {
    model.addAttribute("artists", artists.list(null));
    return "admin/artists";
  }

  @GetMapping("/new")
  public String createForm(Model model) {
    return form(model, new ArtistForm(), "Новий виконавець", false);
  }

  @PostMapping
  public String create(
      @Valid @ModelAttribute ArtistForm artistForm,
      BindingResult result,
      Model model,
      RedirectAttributes redirect
  ) {
    if (result.hasErrors()) {
      return form(model, artistForm, "Новий виконавець", false);
    }
    try {
      artists.create(artistForm);
    } catch (ConflictException exception) {
      result.reject("conflict", exception.getMessage());
      return form(model, artistForm, "Новий виконавець", false);
    }
    redirect.addFlashAttribute("success", "Виконавця додано");
    return "redirect:/admin/artists";
  }

  @GetMapping("/{id}/edit")
  public String editForm(@PathVariable Long id, Model model) {
    model.addAttribute("entityId", id);
    return form(model, artists.form(id), "Редагувати виконавця", true);
  }

  @PostMapping("/{id}")
  public String update(
      @PathVariable Long id,
      @Valid @ModelAttribute ArtistForm artistForm,
      BindingResult result,
      Model model,
      RedirectAttributes redirect
  ) {
    model.addAttribute("entityId", id);
    if (result.hasErrors()) {
      return form(model, artistForm, "Редагувати виконавця", true);
    }
    try {
      artists.update(id, artistForm);
    } catch (ConflictException exception) {
      result.reject("conflict", exception.getMessage());
      return form(model, artistForm, "Редагувати виконавця", true);
    }
    redirect.addFlashAttribute("success", "Виконавця оновлено");
    return "redirect:/admin/artists";
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable Long id, RedirectAttributes redirect) {
    try {
      artists.delete(id);
      redirect.addFlashAttribute("success", "Виконавця видалено");
    } catch (ConflictException exception) {
      redirect.addFlashAttribute("error", exception.getMessage());
    }
    return "redirect:/admin/artists";
  }

  private String form(Model model, ArtistForm artistForm, String heading, boolean editing) {
    model.addAttribute("artistForm", artistForm);
    model.addAttribute("heading", heading);
    model.addAttribute("editing", editing);
    return "admin/artist-form";
  }
}
