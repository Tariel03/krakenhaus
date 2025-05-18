package whz.project.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import whz.project.demo.enums.Role;
import whz.project.demo.services.BenutzerService;
import whz.project.demo.services.ReviewService;
import whz.project.demo.services.TerminService;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final BenutzerService benutzerService;
    private final TerminService terminService;
    private final ReviewService reviewService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("arzts", benutzerService.getAllArztOverviews());
        return "main";
    }
}
