package whz.project.demo.controllers;

import lombok.RequiredArgsConstructor;
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
import whz.project.demo.services.ArztService;
import whz.project.demo.services.BenutzerService;
import whz.project.demo.services.PatientService;
import whz.project.demo.services.TerminService;

import java.util.List;

@Controller
@RequestMapping("/admin/termine")
@RequiredArgsConstructor
public class TerminVerwaltungController {

    private final TerminService terminService;
    private final BenutzerService benutzerService;
    private final PatientService patientService;
    private final ArztService arztService;

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


}
