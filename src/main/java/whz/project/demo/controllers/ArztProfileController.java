package whz.project.demo.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import whz.project.demo.entity.Arzt;
import whz.project.demo.security.BenutzerDetails;
import whz.project.demo.services.ArztService;
import whz.project.demo.services.FachrichtungService;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@Controller
@RequestMapping("/arzt/profil-bearbeiten")
@RequiredArgsConstructor
public class ArztProfileController {

    private final ArztService arztService;
    private final FachrichtungService fachrichtungService;


    @GetMapping
    public String zeigeProfil(Model model,
                              @AuthenticationPrincipal BenutzerDetails benutzerDetails) throws AccessDeniedException {
        if (benutzerDetails == null || !(benutzerDetails.getBenutzer() instanceof Arzt arzt)) {
            throw new AccessDeniedException("Nur Ärzte haben Zugriff auf diese Seite.");
        }

        Arzt aktuell = arztService.findById(arzt.getId());
        model.addAttribute("alleFachrichtungen", fachrichtungService.findAll());
        model.addAttribute("arzt", aktuell);
        return "profile/arzt_profile/arzt_profile";
    }

    @PatchMapping
    public String aktualisiereProfil(@ModelAttribute("arzt") Arzt aktualisiert,
                                     @RequestParam(value = "profilbild", required = false) MultipartFile profilbild,
                                     @AuthenticationPrincipal BenutzerDetails benutzerDetails,
                                     RedirectAttributes redirectAttributes) throws IOException {

        if (benutzerDetails == null || !(benutzerDetails.getBenutzer() instanceof Arzt arzt)) {
            throw new AccessDeniedException("Nur Ärzte haben Zugriff auf diese Seite.");
        }

        arztService.aktualisiereProfil(arzt.getId(), aktualisiert, profilbild);
        redirectAttributes.addFlashAttribute("success", "Profil erfolgreich aktualisiert.");
        return "redirect:/arzt/profil-bearbeiten";
    }
}
