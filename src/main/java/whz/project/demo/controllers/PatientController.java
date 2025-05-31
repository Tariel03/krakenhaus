package whz.project.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import whz.project.demo.Photos.PhotoConfig;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.exceptions.NotFoundByIdException;
import whz.project.demo.services.BenutzerService;
import whz.project.demo.services.CurrentUserService;
import whz.project.demo.services.TerminService;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class PatientController {
    PhotoConfig photoConfig = new PhotoConfig();
    private final BenutzerService benutzerService;
    private final TerminService terminService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public String profile(Model model, Authentication authentication) throws Exception {
        model.addAttribute("benutzer", benutzerService.findById(currentUserService.getCustomerIdFromAuthentication(authentication)));
        return "profile";
    }
    @PostMapping("/set/mainImage")
    public String setMainImage(@RequestParam MultipartFile file, Authentication authentication) throws Exception {
        Benutzer benutzer = getCurrentBenutzer(authentication);
        String safeFileName = file.getOriginalFilename().replaceAll("\\s+", "_");
        photoConfig.savePhoto(file);
        benutzer.setMainImage("img/" + safeFileName);
        benutzerService.save(benutzer);
        return "redirect:/profile";

    }
    @PostMapping("/add/toGallery")
    public String addToGallery(@RequestParam MultipartFile file, Authentication authentication) throws Exception {
        photoConfig.savePhoto(file);
        Benutzer benutzer = getCurrentBenutzer(authentication);
        benutzer.getGallery().add("img/"+file.getOriginalFilename());
        benutzerService.save(benutzer);
        return "redirect:/profile";

    }

    private Benutzer getCurrentBenutzer(Authentication authentication) throws Exception {
        return benutzerService.findById(currentUserService.getCustomerIdFromAuthentication(authentication));
    }

}
