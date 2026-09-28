package dev.vinyllab.controller;

import dev.vinyllab.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

  private final AlbumService albums;

  @GetMapping("/")
  public String home(Model model) {
    model.addAttribute("top", albums.topRated(5));
    return "index";
  }

  @GetMapping("/about")
  public String about() {
    return "about";
  }
}
