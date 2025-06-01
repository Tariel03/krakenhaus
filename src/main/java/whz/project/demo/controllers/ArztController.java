package whz.project.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.services.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/arzt/")
public class ArztController {
    private final TerminService terminService;
    private final BenutzerService benutzerService;
    private final ReviewService reviewService;
    private final LeistungenService leistungenService;
    private final FachrictungService fachrictungService;

    @GetMapping("/{id}")
    public String arzt(@PathVariable("id") Long id, Model model ) throws Exception {
        Benutzer arzt = benutzerService.findById(id);
        model.addAttribute("arzt", arzt);
        model.addAttribute("leistungen", leistungenService.findAllByIds(List.of(id)));
        model.addAttribute("fachrictungen", fachrictungService.findAllByIds(List.of(id)));
        model.addAttribute("reviews", reviewService.getLastFiveReviews(arzt.getId()));
        return "arzt";
    }

}
