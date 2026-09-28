package dev.vinyllab.controller.admin;

import dev.vinyllab.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

  private final StatsService stats;

  @GetMapping
  public String index(Model model) {
    model.addAttribute("counts", stats.overview());
    return "admin/index";
  }
}
