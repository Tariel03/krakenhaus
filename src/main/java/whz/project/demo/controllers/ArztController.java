package whz.project.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import whz.project.demo.dto.ReviewDto;
import whz.project.demo.entity.Arzt;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.exceptions.NotFoundByIdException;
import whz.project.demo.services.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/arzt/")
public class ArztController {
    private final TerminService terminService;
    private final ArztService arztService;
    private final ReviewService reviewService;
    private final LeistungenService leistungenService;
    private final FachrichtungService fachrichtungService;
    private final CurrentUserService currentUserService;
    private final PatientService patientService;

    @GetMapping("/{id}")
    public String arzt(@PathVariable("id") Long id, Model model ) throws Exception {
        Arzt arzt = arztService.findById(id);

        model.addAttribute("arzt", arzt);
        model.addAttribute("leistungen", leistungenService.findAllByIds(List.of(id)));
        model.addAttribute("fachrichtungen", fachrichtungService.findAllByIds(List.of(id)));
        model.addAttribute("reviews", reviewService.getLastFiveReviews(arzt.getId()));
        return "arzt/arzt";
    }

    @PostMapping("/review")
    public String addReview(@ModelAttribute ReviewDto reviewDto, Authentication authentication) {
        Long userId = currentUserService.getCustomerIdFromAuthentication(authentication);

        if (!patientService.existsById(userId)) {
            return "redirect:/access-denied"; 
        }

        reviewDto.setPatient_id(userId);

        try {
            reviewService.save(reviewDto);
        } catch (NotFoundByIdException e) {
            return "redirect:/error"; 
        }

        return "redirect:/arzt/" + reviewDto.getArzt_id();
    }

}
