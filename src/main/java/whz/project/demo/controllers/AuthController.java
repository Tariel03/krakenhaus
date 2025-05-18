package whz.project.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.enums.Role;
import whz.project.demo.repos.BenutzerRepository;
import whz.project.demo.services.RegistrationService;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final RegistrationService registrationService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("benutzer", new Benutzer());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute Benutzer benutzer) {
        registrationService.save(benutzer);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}

