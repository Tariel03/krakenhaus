package whz.project.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import whz.project.demo.dto.FachrichtungDto;
import whz.project.demo.dto.MedikamentDto;
import whz.project.demo.entity.Arzt;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.entity.Fachrichtung;
import whz.project.demo.entity.Leistungen;
import whz.project.demo.enums.Role;
import whz.project.demo.services.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final FachrichtungService fachrichtungService;
    private final ArztService arztService;
    private final LeistungenService leistungenService;
    private final MedikamentService medikamentService;



    @GetMapping("/leistungen/form")
    public String showForm(Model model) {
        model.addAttribute("arzte", arztService.findAll());
        model.addAttribute("leistungen", leistungenService.findAll());
        return "admin/arzt_leistungen_form";
    }

    @PostMapping("/leistungen/zuweisen")
    public String assignLeistungen(@RequestParam Long arztId, @RequestParam List<Long> leistungIds) throws Exception {
        Arzt arzt = arztService.findById(arztId);

        List<Leistungen> leistungen = leistungenService.findAllByIds(leistungIds);
        arzt.setLeistungen(leistungen);
        arztService.save(arzt);

        return "redirect:/admin/leistungen/form?success";
    }

    @GetMapping("/leistungen/neu")
    public String showLeistungenForm(Model model) {
        model.addAttribute("leistung", new Leistungen());
        return "admin/leistungen_erstellen";
    }

    @PostMapping("/leistungen/neu")
    public String saveLeistung(@ModelAttribute Leistungen leistung) {
        leistungenService.save(leistung);
        return "redirect:/admin/leistungen/form?created";
    }

    @GetMapping("/medikament")
    public String listMedikament(Model model){
        model.addAttribute("medikament", medikamentService.findAll());
        return "admin/medikament";
    }

    @PostMapping("/medikament/erstellen")
    public String saveMedikament(@ModelAttribute MedikamentDto medikament) {
        medikamentService.save(medikament);
        return "redirect:/admin/medikament";

    }

    @GetMapping("/fachrictung")
    public String listFachrictungs(Model model) {
        model.addAttribute("arzts", arztService.findAll());
        model.addAttribute("fachrictungs", fachrichtungService.findAll());
        return "admin/fachrichtung";
    }

    @PostMapping("/fachrictung/erstellen")
    public String saveFachrichtung(@ModelAttribute FachrichtungDto fachrichtung){
        fachrichtungService.save(fachrichtung);
        return "redirect:/admin/fachrictung";

    }
    @PostMapping("/fachrichtung/zuweisen")
    public String fachrictungZuweisen(@RequestParam List<Long> fach_ids,Long arzt_id ) throws Exception {
        Arzt arzt = arztService.findById(arzt_id);
        List<Fachrichtung> fachrictungs = fachrichtungService.findAllByIds(fach_ids);
        arzt.setFachrichtungList(fachrictungs);
        arztService.save(arzt);
        return "redirect:/admin/fachrictung";
    }




}
