package whz.project.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.entity.Termin;
import whz.project.demo.enums.TerminStatus;
import whz.project.demo.security.BenutzerDetails;
import whz.project.demo.services.TerminService;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Controller
@RequestMapping("/termine")
@RequiredArgsConstructor
public class TerminController {

    private final TerminService terminService;

    @GetMapping
    public String zeigeTermine(Model model,
                               @AuthenticationPrincipal BenutzerDetails benutzerDetails) {
        Benutzer arzt = benutzerDetails.getBenutzer();
        List<Termin> termine = terminService.findeAlleFuerArzt(arzt);

        termine.forEach(termin -> termin.setCssClass(getStatusCssClass(termin)));

        model.addAttribute("alle", termine);
        model.addAttribute("stornierte", filterByStatus(termine, TerminStatus.STORNIERT));
        model.addAttribute("geplante", filterByStatus(termine, TerminStatus.GEBUCHT));
        model.addAttribute("wartend", filterByStatus(termine, TerminStatus.ABGESCHLOSSEN));

        return "profile/arzt_termine";
    }


    @PostMapping("/status")
    public String statusWechsel(@RequestParam Long terminId,
                                @RequestParam TerminStatus status,
                                @AuthenticationPrincipal BenutzerDetails benutzerDetails) throws AccessDeniedException {

        if (benutzerDetails == null) {
            throw new AccessDeniedException("Nicht eingeloggt");
        }

        Benutzer arzt = benutzerDetails.getBenutzer();
        terminService.statusAktualisieren(terminId, status, arzt);
        return "redirect:/termine";
    }

    @GetMapping("/details/{id}")
    public String zeigeDetails(@PathVariable Long id,
                               @AuthenticationPrincipal BenutzerDetails benutzerDetails,
                               Model model) throws AccessDeniedException {

        if (benutzerDetails == null) {
            throw new AccessDeniedException("Nicht eingeloggt");
        }

        Benutzer arzt = benutzerDetails.getBenutzer();

        Termin termin = terminService.findeMitZugriff(id, arzt);
        model.addAttribute("termin", termin);
        return "fragments/terminDetails :: terminDetailsContent";
    }


    private List<Termin> filterByStatus(List<Termin> termine, TerminStatus status) {
        return termine.stream().filter(t -> t.getStatus() == status).toList();
    }

    private String getStatusCssClass(Termin termin) {
        if (termin.getStatus() == null) return "";
        return switch (termin.getStatus()) {
            case FREI -> "bg-success";
            case GEBUCHT -> "bg-primary";
            case ABGESCHLOSSEN -> "bg-secondary";
            case STORNIERT -> "bg-danger";
            default -> "";
        };
    }

}