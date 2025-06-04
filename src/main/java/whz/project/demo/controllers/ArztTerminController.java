package whz.project.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import whz.project.demo.dto.MedikamentDto;
import whz.project.demo.entity.Arzt;
import whz.project.demo.entity.Medikament;
import whz.project.demo.entity.Rezept;
import whz.project.demo.entity.Termin;
import whz.project.demo.enums.TerminStatus;
import whz.project.demo.repos.MedikamentRepository;
import whz.project.demo.security.BenutzerDetails;
import whz.project.demo.services.MedikamentService;
import whz.project.demo.services.RezeptService;
import whz.project.demo.services.TerminService;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("arzt/termine")
@RequiredArgsConstructor
public class ArztTerminController {

    private final TerminService terminService;
    private final RezeptService rezeptService;
    private final MedikamentService medikamentService;

    @GetMapping
    public String zeigeTermine(Model model,
                               @AuthenticationPrincipal BenutzerDetails benutzerDetails) throws AccessDeniedException {

        if (benutzerDetails == null || !(benutzerDetails.getBenutzer() instanceof Arzt arzt)) {
            throw new AccessDeniedException("Nur Ärzte haben Zugriff auf diese Seite.");
        }

        List<Termin> termine = terminService.findeAlleFuerArzt(arzt);

        // CSS-Klassen für alle Termine setzen
        termine.forEach(termin -> termin.setCssClass(getStatusCssClass(termin)));

        // Alle Listen für die verschiedenen Tabs erstellen
        model.addAttribute("alle", termine);
        model.addAttribute("arzt", arzt);

        // Aktive Termine (GEBUCHT + BESTAETIGT)
        List<Termin> aktive = new ArrayList<>();
        aktive.addAll(filterByStatus(termine, TerminStatus.GEBUCHT));
        aktive.addAll(filterByStatus(termine, TerminStatus.BESTAETIGT));
        model.addAttribute("aktive", aktive);

        // Einzelne Statuslisten
        model.addAttribute("abgeschlossene", filterByStatus(termine, TerminStatus.ABGESCHLOSSEN));
        model.addAttribute("stornierte", filterByStatus(termine, TerminStatus.STORNIERT));
        model.addAttribute("abgesagte", filterByStatus(termine, TerminStatus.ABGESAGT));
        model.addAttribute("nichtErschienen", filterByStatus(termine, TerminStatus.NICHT_ERSCHIENEN));
        model.addAttribute("freie", filterByStatus(termine, TerminStatus.FREI));

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




    private String getStatusCssClass(Termin termin) {
        return switch (termin.getStatus()) {
            case FREI -> "bg-light text-dark";
            case GEBUCHT -> "bg-primary";
            case BESTAETIGT -> "bg-info";
            case ABGESCHLOSSEN -> "bg-success";
            case ABGESAGT -> "bg-warning text-dark";
            case STORNIERT -> "bg-danger";
            case NICHT_ERSCHIENEN -> "bg-dark";
        };
    }

    private List<Termin> filterByStatus(List<Termin> termine, TerminStatus status) {
        return termine.stream()
                .filter(termin -> termin.getStatus() == status)
                .collect(Collectors.toList());
    }


    @GetMapping("/{terminId}/rezept")
    public String rezeptViewAndEdit(@PathVariable Long terminId,
                                    Model model,
                                    @AuthenticationPrincipal BenutzerDetails benutzerDetails) throws Exception {

        if (benutzerDetails == null || !(benutzerDetails.getBenutzer() instanceof Arzt arzt)) {
            throw new AccessDeniedException("Nur Ärzte haben Zugriff auf diese Seite.");
        }

        Termin termin = terminService.findById(terminId);
        Rezept rezept = rezeptService.findOrCreateByTermin(termin);
        List<Medikament> alleMedikamente = medikamentService.findAll();

        model.addAttribute("arzt", arzt);
        model.addAttribute("termin", termin);
        model.addAttribute("rezept", rezept);
        model.addAttribute("alleMedikamente", alleMedikamente);

        return "profile/arzt_profile/rezept_form";
    }
    @PostMapping("/rezept/speichern")
    public String rezeptSpeichern(@ModelAttribute Rezept rezept,
                                  @RequestParam(name="notizen" , required = false ) String notizen,
                                  @RequestParam(name="diagnose", required = false) String diagnose) throws Exception {

        rezeptService.speichereRezeptMitTerminInfos(rezept, notizen, diagnose);
        return "redirect:/arzt/termine/" + rezept.getTermin().getId() + "/rezept";
    }

    @PostMapping("/arzt/termine/rezept/medikament/loeschen")
    public String deleteMedikament(@RequestParam Long id) {
        Long rezeptId = rezeptService.deleteMedikament(id);
        return "redirect:/arzt/termine/" + rezeptId + "/rezept";
    }

    @PostMapping(value = "/arzt/termine/rezept/medikament/hinzufuegen-ajax", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<MedikamentDto> rezeptAjax(@RequestBody MedikamentDto dto) {
        MedikamentDto gespeichert = rezeptService.addMedikamentAjax(dto);
        return ResponseEntity.ok(gespeichert);
    }

    @PostMapping(value = "/arzt/termine/rezept/medikament/update-ajax", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<Void> updateMedikamentAjax(@RequestBody MedikamentDto dto) {
        rezeptService.updateMedikament(dto);
        return ResponseEntity.ok().build();
    }




}