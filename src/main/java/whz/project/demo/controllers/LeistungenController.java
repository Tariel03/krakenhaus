package whz.project.demo.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import whz.project.demo.dto.LeistungDTO;
import whz.project.demo.entity.Arzt;
import whz.project.demo.entity.Leistungen;
import whz.project.demo.security.BenutzerDetails;
import whz.project.demo.services.LeistungenService;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Controller
@RequestMapping("arzt/leistungen")
@RequiredArgsConstructor
public class LeistungenController {

    private final LeistungenService leistungenService;

    @GetMapping
    public String showLeistungen(Model model,
                                 @AuthenticationPrincipal BenutzerDetails benutzerDetails) throws AccessDeniedException {

        if (benutzerDetails == null || !(benutzerDetails.getBenutzer() instanceof Arzt arzt)) {
            throw new AccessDeniedException("Nur Ärzte haben Zugriff auf diese Seite.");
        }
        List<Leistungen> leistungens = leistungenService.findByArzt(arzt);
        model.addAttribute("leistungens", leistungens);
        model.addAttribute("arzt", arzt);
        model.addAttribute("neueLeistung", new Leistungen());

        return "profile/arzt_profile/arzt_leistungen";
    }
    @PostMapping
    public String fuegeLeistungHinzu(@ModelAttribute("leistungen") Leistungen leistung,
                                     @AuthenticationPrincipal BenutzerDetails benutzerDetails) throws AccessDeniedException {

        if (benutzerDetails == null || !(benutzerDetails.getBenutzer() instanceof Arzt arzt)) {
            throw new AccessDeniedException("Nur Ärzte dürfen Leistungen hinzufügen.");
        }

        leistung.setArzt(arzt);
        leistungenService.save(leistung);
        return "redirect:/arzt/leistungen";
    }

    @GetMapping("/{id}/bearbeiten")
    @ResponseBody
    public LeistungDTO bearbeiteLeistung(@PathVariable Long id,
                                         @AuthenticationPrincipal BenutzerDetails benutzerDetails) throws AccessDeniedException {
        if (benutzerDetails == null || !(benutzerDetails.getBenutzer() instanceof Arzt arzt)) {
            throw new AccessDeniedException("Kein Zugriff");
        }

        Leistungen leistung = leistungenService.findeByIdUndArzt(id, arzt.getId());

        return new LeistungDTO(
                leistung.getId(),
                leistung.getName(),
                leistung.getBeschreibung()
        );
    }


    @PostMapping("/{id}/aktualisieren")
    public String aktualisiereLeistung(@PathVariable Long id,
                                       @ModelAttribute Leistungen leistung,
                                       @AuthenticationPrincipal BenutzerDetails benutzerDetails) throws AccessDeniedException {

        if (benutzerDetails == null || !(benutzerDetails.getBenutzer() instanceof Arzt arzt)) {
            throw new AccessDeniedException("Kein Zugriff");
        }

        Leistungen original = leistungenService.findeByIdUndArzt(id, arzt.getId());
        original.setName(leistung.getName());
        original.setBeschreibung(leistung.getBeschreibung());
        leistungenService.save(original);

        return "redirect:/arzt/leistungen";
    }

    @PostMapping("/{id}/loeschen")
    public String loescheLeistung(@PathVariable Long id,
                                  @AuthenticationPrincipal BenutzerDetails benutzerDetails) throws AccessDeniedException {

        if (benutzerDetails == null || !(benutzerDetails.getBenutzer() instanceof Arzt arzt)) {
            throw new AccessDeniedException("Kein Zugriff");
        }

        leistungenService.loescheByIdUndArzt(id, arzt.getId());
        return "redirect:/arzt/leistungen";
    }

}
