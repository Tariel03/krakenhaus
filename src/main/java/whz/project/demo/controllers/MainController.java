package whz.project.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import whz.project.demo.dto.ReviewDto;
import whz.project.demo.dto.TerminDto;
import whz.project.demo.enums.Role;
import whz.project.demo.services.BenutzerService;
import whz.project.demo.services.CurrentUserService;
import whz.project.demo.services.ReviewService;
import whz.project.demo.services.TerminService;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final BenutzerService benutzerService;
    private final TerminService terminService;
    private final ReviewService reviewService;
    private final CurrentUserService currentUserService;

    @GetMapping("/main")
    public String index(Model model) {
        System.out.println(benutzerService.findAllByRole(Role.ARZT));
        model.addAttribute("arzts", benutzerService.findAllByRole(Role.ARZT));
        model.addAttribute("benutzerService", benutzerService);
        model.addAttribute("terminService", terminService);
        return "main";
    }

    @PostMapping("/review")
    public String addReview(@ModelAttribute ReviewDto reviewDto, Authentication authentication) {
        Long patientId = currentUserService.getCustomerIdFromAuthentication(authentication);
        reviewDto.setPatient_id(patientId);
        reviewService.save(reviewDto);
        return "redirect:/main";
    }
    @PostMapping("/termin")
    public String addTermin(@ModelAttribute TerminDto terminDto, Authentication authentication) {
        Long patientId = currentUserService.getCustomerIdFromAuthentication(authentication);
        terminService.bookTermin(terminDto.getTerminId(), patientId); // Implement this method
        return "redirect:/main";
    }








}