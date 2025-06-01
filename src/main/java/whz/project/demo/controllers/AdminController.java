package whz.project.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import whz.project.demo.dto.FachrictungDto;
import whz.project.demo.dto.MedikamentDto;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.entity.Fachrictung;
import whz.project.demo.entity.Leistungen;
import whz.project.demo.entity.Medikament;
import whz.project.demo.enums.Role;
import whz.project.demo.services.BenutzerService;
import whz.project.demo.services.FachrictungService;
import whz.project.demo.services.LeistungenService;
import whz.project.demo.services.MedikamentService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final FachrictungService fachrictungService;
    private final BenutzerService benutzerService;
    private final LeistungenService leistungenService;
    private final MedikamentService medikamentService;



    @GetMapping("/leistungen/form")
    public String showForm(Model model) {
        model.addAttribute("arzte", benutzerService.findAllByRole(Role.ARZT));
        model.addAttribute("leistungen", leistungenService.findAll());
        return "testTemplate/arzt_leistungen_form";
    }

    @PostMapping("/leistungen/zuweisen")
    public String assignLeistungen(@RequestParam Long arztId, @RequestParam List<Long> leistungIds) throws Exception {
        Benutzer arzt = benutzerService.findById(arztId);

        List<Leistungen> leistungen = leistungenService.findAllByIds(leistungIds);
        arzt.setLeistungenList(leistungen);
        benutzerService.save(arzt);

        return "redirect:/admin/leistungen/form?success";
    }

    @GetMapping("/leistungen/neu")
    public String showLeistungenForm(Model model) {
        model.addAttribute("leistung", new Leistungen());
        return "testTemplate/leistungen_erstellen";
    }

    @PostMapping("/leistungen/neu")
    public String saveLeistung(@ModelAttribute Leistungen leistung) {
        leistungenService.save(leistung);
        return "redirect:/admin/leistungen/form?created";
    }

    @GetMapping("/medikament")
    public String listMedikament(Model model){
        model.addAttribute("medikament", medikamentService.findAll());
        return "testTemplate/medikament";
    }

    @PostMapping("/medikament/erstellen")
    public String saveMedikament(@ModelAttribute MedikamentDto medikament) {
        medikamentService.save(medikament);
        return "redirect:/admin/medikament";

    }

    @GetMapping("/fachrictung")
    public String listFachrictungs(Model model) {
        model.addAttribute("arzts", benutzerService.findAllByRole(Role.ARZT));
        model.addAttribute("fachrictungs", fachrictungService.findAll());
        return "testTemplate/fachrichtung";
    }

    @PostMapping("/fachrictung/erstellen")
    public String saveFachrichtung(@ModelAttribute FachrictungDto fachrichtung){
        fachrictungService.save(fachrichtung);
        return "redirect:/admin/fachrictung";

    }
    @PostMapping("/fachrichtung/zuweisen")
    public String fachrictungZuweisen(@RequestParam List<Long> fach_ids,Long arzt_id ) throws Exception {
        Benutzer arzt = benutzerService.findById(arzt_id);
        List<Fachrictung> fachrictungs = fachrictungService.findAllByIds(fach_ids);
        arzt.setFachrictungList(fachrictungs);
        benutzerService.save(arzt);
        return "redirect:/admin/fachrictung";
    }




}
