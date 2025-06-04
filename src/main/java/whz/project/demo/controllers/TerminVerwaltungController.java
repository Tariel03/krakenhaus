package whz.project.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import whz.project.demo.dto.TerminFormDto;
import whz.project.demo.entity.Arzt;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.entity.Patient;
import whz.project.demo.entity.Termin;
import whz.project.demo.enums.Role;
import whz.project.demo.enums.TerminStatus;
import whz.project.demo.services.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/termine")
@RequiredArgsConstructor
public class TerminVerwaltungController {

    private final TerminService terminService;
    private final BenutzerService benutzerService;
    private final PatientService patientService;
    private final ArztService arztService;
    private final CurrentUserService currentUserService;

    @GetMapping("/neu")
    public String neuerTerminFormular(Model model) {
        model.addAttribute("terminDto", new TerminFormDto());

        List<Arzt> aerzte = arztService.findAll();
        List<Patient> patienten = patientService.findAll();

        model.addAttribute("aerzte", aerzte);
        model.addAttribute("patienten", patienten);
        model.addAttribute("statuses", TerminStatus.values());

        return "admin/termin_formular";
    }

    @PostMapping("/neu")
    public String terminSpeichern(@ModelAttribute("terminDto") TerminFormDto dto) throws Exception {
        if (dto.getArztId() == null) {
            throw new IllegalArgumentException("Arzt darf nicht null sein");
        }

        Arzt arzt = arztService.findById(dto.getArztId());
        Patient patient = dto.getPatientId() != null ? patientService.findById(dto.getPatientId()) : null;

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

    /**
     * Создание стандартных слотов для врача (админская функция)
     */
    @PostMapping("/{id}/create-slots")
    @ResponseBody
    public String createSlotsForDate(
            @PathVariable("id") Long arztId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {

        Long userId = currentUserService.getCustomerIdFromAuthentication(authentication);
        Benutzer user = benutzerService.findById(userId);
        System.out.println("User ID: " + userId + ", Role: " + user.getRole() + ", Authorities: " + authentication.getAuthorities());

        if (!user.getRole().equals(Role.ADMIN)) {
            if (!user.getRole().equals(Role.ARZT)) {
                return "error: Access denied. You can only create slots for yourself.";
            }
        }

        try {
            terminService.createStandardSlotsForDate(arztId, date);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }


}
