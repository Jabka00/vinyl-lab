package dev.vinyllab.controller.admin;

import dev.vinyllab.exception.ConflictException;
import dev.vinyllab.form.AlbumForm;
import dev.vinyllab.service.AlbumService;
import dev.vinyllab.service.ArtistService;
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
@RequestMapping("/admin/albums")
@RequiredArgsConstructor
public class AdminAlbumController {

  private final AlbumService albums;
  private final ArtistService artists;
  private final GenreService genres;

  @GetMapping
  public String list(Model model) {
    model.addAttribute("albums", albums.catalog(null, null, "title"));
    return "admin/albums";
  }

  @GetMapping("/new")
  public String createForm(Model model) {
    return form(model, new AlbumForm(), "Новий альбом", false);
  }

  @PostMapping
  public String create(
      @Valid @ModelAttribute AlbumForm albumForm,
      BindingResult result,
      Model model,
      RedirectAttributes redirect
  ) {
    if (result.hasErrors()) {
      return form(model, albumForm, "Новий альбом", false);
    }
    try {
      albums.create(albumForm);
    } catch (ConflictException exception) {
      result.reject("conflict", exception.getMessage());
      return form(model, albumForm, "Новий альбом", false);
    }
    redirect.addFlashAttribute("success", "Альбом додано");
    return "redirect:/admin/albums";
  }

  @GetMapping("/{id}/edit")
  public String editForm(@PathVariable Long id, Model model) {
    model.addAttribute("entityId", id);
    return form(model, albums.form(id), "Редагувати альбом", true);
  }

  @PostMapping("/{id}")
  public String update(
      @PathVariable Long id,
      @Valid @ModelAttribute AlbumForm albumForm,
      BindingResult result,
      Model model,
      RedirectAttributes redirect
  ) {
    model.addAttribute("entityId", id);
    if (result.hasErrors()) {
      return form(model, albumForm, "Редагувати альбом", true);
    }
    try {
      albums.update(id, albumForm);
    } catch (ConflictException exception) {
      result.reject("conflict", exception.getMessage());
      return form(model, albumForm, "Редагувати альбом", true);
    }
    redirect.addFlashAttribute("success", "Альбом оновлено");
    return "redirect:/admin/albums";
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable Long id, RedirectAttributes redirect) {
    try {
      albums.delete(id);
      redirect.addFlashAttribute("success", "Альбом видалено");
    } catch (ConflictException exception) {
      redirect.addFlashAttribute("error", exception.getMessage());
    }
    return "redirect:/admin/albums";
  }

  private String form(Model model, AlbumForm albumForm, String heading, boolean editing) {
    model.addAttribute("albumForm", albumForm);
    model.addAttribute("artists", artists.list(null));
    model.addAttribute("genres", genres.list());
    model.addAttribute("heading", heading);
    model.addAttribute("editing", editing);
    return "admin/album-form";
  }
}
