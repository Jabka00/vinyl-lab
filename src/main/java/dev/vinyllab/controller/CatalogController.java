package dev.vinyllab.controller;

import dev.vinyllab.form.RatingForm;
import dev.vinyllab.security.CurrentUserService;
import dev.vinyllab.service.AlbumService;
import dev.vinyllab.service.ArtistService;
import dev.vinyllab.service.GenreService;
import dev.vinyllab.service.RatingService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {

  private final AlbumService albums;
  private final ArtistService artists;
  private final GenreService genres;
  private final RatingService ratings;
  private final CurrentUserService currentUser;

  @GetMapping("/albums")
  public String albums(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String genre,
      @RequestParam(defaultValue = "title") String sort,
      Model model
  ) {
    Long genreId = parseId(genre);
    String activeSort = switch (sort) {
      case "rating", "year" -> sort;
      default -> "title";
    };
    model.addAttribute("albums", albums.catalog(q, genreId, activeSort));
    model.addAttribute("genres", genres.list());
    model.addAttribute("q", q == null ? "" : q);
    model.addAttribute("genre", genreId);
    model.addAttribute("sort", activeSort);
    return "catalog/albums";
  }

  @GetMapping("/albums/{id}")
  public String album(@PathVariable Long id, Model model) {
    var details = albums.details(id, currentUser.id().orElse(null));
    RatingForm ratingForm = new RatingForm();
    ratingForm.setScore(details.myScore());
    model.addAttribute("album", details);
    model.addAttribute("ratingForm", ratingForm);
    return "catalog/album";
  }

  @PostMapping("/albums/{id}/rating")
  public String rate(
      @PathVariable Long id,
      @Valid @ModelAttribute RatingForm ratingForm,
      BindingResult result,
      RedirectAttributes redirect
  ) {
    if (result.hasErrors()) {
      redirect.addFlashAttribute("error", "Оцінка має бути від 1 до 5");
      return "redirect:/catalog/albums/" + id;
    }
    ratings.rate(currentUser.requireId(), id, ratingForm.getScore());
    redirect.addFlashAttribute("success", "Оцінку збережено");
    return "redirect:/catalog/albums/" + id;
  }

  @GetMapping("/artists")
  public String artists(@RequestParam(required = false) String q, Model model) {
    model.addAttribute("artists", artists.list(q));
    model.addAttribute("q", q == null ? "" : q);
    return "catalog/artists";
  }

  @GetMapping("/artists/{id}")
  public String artist(@PathVariable Long id, Model model) {
    model.addAttribute("artist", artists.details(id));
    return "catalog/artist";
  }

  private Long parseId(String raw) {
    if (raw == null || raw.isBlank()) {
      return null;
    }
    try {
      return Long.valueOf(raw);
    } catch (NumberFormatException exception) {
      return null;
    }
  }
}
