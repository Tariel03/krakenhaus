package whz.project.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import whz.project.demo.entity.Arzt;
import whz.project.demo.entity.Termin;
import whz.project.demo.enums.TerminStatus;
import whz.project.demo.security.BenutzerDetails;
import whz.project.demo.services.TerminService;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Controller
@RequestMapping("arzt/termine")
@RequiredArgsConstructor
public class ArztTerminController {

    private final TerminService terminService;

    @GetMapping
    public String zeigeTermine(Model model,
                               @AuthenticationPrincipal BenutzerDetails benutzerDetails) throws AccessDeniedException {

        if (benutzerDetails == null || !(benutzerDetails.getBenutzer() instanceof Arzt arzt)) {
            throw new AccessDeniedException("Nur Ärzte haben Zugriff auf diese Seite.");
        }

        List<Termin> termine = terminService.findeAlleFuerArzt(arzt);

        termine.forEach(termin -> termin.setCssClass(getStatusCssClass(termin)));

        model.addAttribute("alle", termine);
        model.addAttribute("arzt", arzt);
        model.addAttribute("stornierte", filterByStatus(termine, TerminStatus.STORNIERT));
        model.addAttribute("geplante", filterByStatus(termine, TerminStatus.GEBUCHT));
        model.addAttribute("wartend", filterByStatus(termine, TerminStatus.ABGESCHLOSSEN));

        return "profile/arzt_profile/arzt_termine";
    }


    @PostMapping("/status")
    public String statusWechsel(@RequestParam Long terminId,
                                @RequestParam TerminStatus status,
                                @AuthenticationPrincipal BenutzerDetails benutzerDetails) throws AccessDeniedException {

        if (benutzerDetails == null || !(benutzerDetails.getBenutzer() instanceof Arzt)) {
            throw new AccessDeniedException("Nur Ärzte dürfen diesen Bereich sehen.");
        }

        Long arztId = benutzerDetails.getBenutzer().getId();
        terminService.statusAktualisieren(terminId, status, arztId);

        return "redirect:/arzt/termine";
    }


    @GetMapping("/details/{id}")
    public String zeigeDetails(@PathVariable Long id,
                               @AuthenticationPrincipal BenutzerDetails benutzerDetails,
                               Model model) throws AccessDeniedException {

        if (benutzerDetails == null || !(benutzerDetails.getBenutzer() instanceof Arzt)) {
            throw new AccessDeniedException("Nur Ärzte haben Zugriff auf Termin-Details");
        }

        Long arztId = benutzerDetails.getBenutzer().getId();
        Termin termin = terminService.findeMitZugriff(id, arztId);
        model.addAttribute("termin", termin);

        return "layout/fragments/terminDetails :: terminDetailsContent";
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