package whz.project.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import whz.project.demo.dto.ReviewDto;
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

    @GetMapping
    public String index(Model model) {
        System.out.println(benutzerService.findAllByRole(Role.ARZT));
        model.addAttribute("arzts", benutzerService.findAllByRole(Role.ARZT));
        model.addAttribute("benutzerService", benutzerService);
        return "main";
    }

    @PostMapping("/review")
    public void addReview(Long arzt_id, ReviewDto reviewDto, Model model, Principal principal){
        reviewDto.setPatient_id(currentUserService.getCustomerIdFromPrincipal(principal));
        reviewService.save(reviewDto);



    }




}