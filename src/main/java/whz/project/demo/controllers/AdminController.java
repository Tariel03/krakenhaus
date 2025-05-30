package whz.project.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.entity.Leistungen;
import whz.project.demo.enums.Role;
import whz.project.demo.services.BenutzerService;
import whz.project.demo.services.LeistungenService;

import java.util.List;

@Controller
@RequestMapping("/admin/leistungen")
public class AdminController {

    private final BenutzerService benutzerService;
    private final LeistungenService leistungenService;

    @Autowired
    public AdminController(BenutzerService benutzerService, LeistungenService leistungenService) {
        this.benutzerService = benutzerService;
        this.leistungenService = leistungenService;
    }

    @GetMapping("/form")
    public String showForm(Model model) {
        model.addAttribute("arzte", benutzerService.findAllByRole(Role.ARZT));
        model.addAttribute("leistungen", leistungenService.findAll());
        return "arzt_leistungen_form";
    }

    @PostMapping("/zuweisen")
    public String assignLeistungen(@RequestParam Long arztId, @RequestParam List<Long> leistungIds) throws Exception {
        Benutzer arzt = benutzerService.findById(arztId);

        List<Leistungen> leistungen = leistungenService.findAllByIds(leistungIds);
        arzt.setLeistungenList(leistungen);
        benutzerService.save(arzt);

        return "redirect:/admin/leistungen/form?success";
    }

    @GetMapping("/neu")
    public String showLeistungenForm(Model model) {
        model.addAttribute("leistung", new Leistungen());
        return "leistungen_erstellen";
    }

    @PostMapping("/neu")
    public String saveLeistung(@ModelAttribute Leistungen leistung) {
        leistungenService.save(leistung);
        return "redirect:/admin/leistungen/form?created";
    }

}
