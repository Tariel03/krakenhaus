package whz.project.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import whz.project.demo.dto.TerminFormDto;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.entity.Termin;
import whz.project.demo.enums.Role;
import whz.project.demo.enums.TerminStatus;
import whz.project.demo.services.BenutzerService;
import whz.project.demo.services.TerminService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/admin/termine")
@RequiredArgsConstructor
public class TerminVerwaltungController {

    private final TerminService terminService;
    private final BenutzerService benutzerService;

    @GetMapping("/neu")
    public String neuerTerminFormular(Model model) {
        model.addAttribute("terminDto", new TerminFormDto());

        List<Benutzer> aerzte = benutzerService.findAllByRole(Role.ARZT);
        List<Benutzer> patienten = benutzerService.findAllByRole(Role.PATIENT);

        model.addAttribute("aerzte", aerzte);
        model.addAttribute("patienten", patienten);
        model.addAttribute("statuses", TerminStatus.values());

        return "testTemplate/termin_formular";
    }

    @PostMapping("/neu")
    public String terminSpeichern(@ModelAttribute("terminDto") TerminFormDto dto) throws Exception {
        if (dto.getArztId() == null) {
            throw new IllegalArgumentException("Arzt darf nicht null sein");
        }

        Benutzer arzt = benutzerService.findById(dto.getArztId());
        Benutzer patient = dto.getPatientId() != null ? benutzerService.findById(dto.getPatientId()) : null;

        Termin termin = Termin.builder()
                .datum(dto.getDatum())
                .uhrzeit(dto.getUhrzeit())
                .arzt(arzt)
                .patient(patient)
                .status(dto.getStatus())
                .diagnose(dto.getDiagnose())
                .notizen(dto.getNotizen())
                .build();

        terminService.speichern(termin);
        return "redirect:/termine";
    }


}
