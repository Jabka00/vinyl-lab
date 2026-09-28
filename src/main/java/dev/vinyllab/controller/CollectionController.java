package dev.vinyllab.controller;

import dev.vinyllab.form.CollectionForm;
import dev.vinyllab.model.RecordCondition;
import dev.vinyllab.security.CurrentUserService;
import dev.vinyllab.service.AlbumService;
import dev.vinyllab.service.CollectionService;
import dev.vinyllab.util.Ukrainian;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/collection")
@RequiredArgsConstructor
public class CollectionController {

  private final CollectionService collection;
  private final AlbumService albums;
  private final CurrentUserService currentUser;

  @GetMapping
  public String list(Model model) {
    var rows = collection.list(currentUser.requireId());
    model.addAttribute("items", rows);
    model.addAttribute("summary", Ukrainian.records(rows.size()));
    return "collection/list";
  }

  @GetMapping("/new")
  public String createForm(@RequestParam(required = false) Long albumId, Model model) {
    CollectionForm form = new CollectionForm();
    form.setAlbumId(albumId);
    form.setCondition(RecordCondition.VERY_GOOD);
    return formView(model, form, "Додати платівку", false);
  }

  @PostMapping
  public String create(
      @Valid @ModelAttribute CollectionForm collectionForm,
      BindingResult result,
      Model model,
      RedirectAttributes redirect
  ) {
    if (result.hasErrors()) {
      return formView(model, collectionForm, "Додати платівку", false);
    }
    collection.create(currentUser.requireId(), collectionForm);
    redirect.addFlashAttribute("success", "Платівку додано до колекції");
    return "redirect:/collection";
  }

  @GetMapping("/{id}/edit")
  public String editForm(@PathVariable Long id, Model model) {
    model.addAttribute("itemId", id);
    return formView(model, collection.form(id, currentUser.requireId()), "Редагувати запис", true);
  }

  @PostMapping("/{id}")
  public String update(
      @PathVariable Long id,
      @Valid @ModelAttribute CollectionForm collectionForm,
      BindingResult result,
      Model model,
      RedirectAttributes redirect
  ) {
    if (result.hasErrors()) {
      model.addAttribute("itemId", id);
      return formView(model, collectionForm, "Редагувати запис", true);
    }
    collection.update(id, currentUser.requireId(), collectionForm);
    redirect.addFlashAttribute("success", "Запис оновлено");
    return "redirect:/collection";
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable Long id, RedirectAttributes redirect) {
    collection.delete(id, currentUser.requireId());
    redirect.addFlashAttribute("success", "Платівку прибрано з колекції");
    return "redirect:/collection";
  }

  private String formView(Model model, CollectionForm form, String heading, boolean editing) {
    model.addAttribute("collectionForm", form);
    model.addAttribute("albums", albums.choices());
    model.addAttribute("conditions", List.of(RecordCondition.values()));
    model.addAttribute("heading", heading);
    model.addAttribute("editing", editing);
    return "collection/form";
  }
}
