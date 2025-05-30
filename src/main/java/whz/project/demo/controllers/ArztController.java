package whz.project.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.services.BenutzerService;
import whz.project.demo.services.CurrentUserService;
import whz.project.demo.services.ReviewService;
import whz.project.demo.services.TerminService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/arzt")
public class ArztController {
    private final TerminService terminService;
    private final BenutzerService benutzerService;
    private final ReviewService reviewService;
    private final CurrentUserService currentUserService;
    public String arzt(Model model, Authentication auth) throws Exception {
        Benutzer arzt = benutzerService.findById(currentUserService.getCustomerIdFromAuthentication(auth));
        model.addAttribute("arzt", arzt);
        model.addAttribute("reviews", reviewService.findByArztId(arzt.getId()));
        model.addAttribute("termins", terminService.findAllByArzt(arzt));
        return "arzt";
    }

}
