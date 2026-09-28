package dev.vinyllab.controller;

import dev.vinyllab.security.CurrentUserService;
import dev.vinyllab.service.StatsService;
import dev.vinyllab.util.Ukrainian;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class StatsController {

  private final StatsService stats;
  private final CurrentUserService currentUser;

  @GetMapping("/stats")
  public String stats(Model model) {
    var collection = stats.forUser(currentUser.requireId());
    model.addAttribute("stats", collection);
    model.addAttribute("summary", Ukrainian.records(collection.total()));
    return "stats/index";
  }
}
