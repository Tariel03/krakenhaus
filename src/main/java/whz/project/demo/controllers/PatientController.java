package whz.project.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import whz.project.demo.Photos.PhotoConfig;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.entity.Patient;
import whz.project.demo.services.BenutzerService;
import whz.project.demo.services.CurrentUserService;

import jakarta.validation.Valid;
import whz.project.demo.services.PatientService;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final BenutzerService benutzerService;
    private final CurrentUserService currentUserService;
    private final PhotoConfig photoConfig = new PhotoConfig();

    @GetMapping
    public String showProfile(Model model, Authentication authentication) {
        try {
            Long userId = currentUserService.getCustomerIdFromAuthentication(authentication);
            Benutzer benutzer = patientService.findById(userId);

            if (benutzer == null) {
                return "redirect:/login?error=user-not-found";
            }

            model.addAttribute("benutzer", benutzer);
            return "profile/benutzer_profile/profile";

        } catch (Exception e) {
            System.err.println("Fehler beim Laden des Profils: " + e.getMessage());
            return "redirect:/main?error=profile-load-failed";
        }
    }

    /**
     * Обновление профиля пользователя
     *
     * Мы будем обновлять поля, только если они не null и не пустые строки
     * (для строковых полей), что указывает на то, что они были отправлены из формы.
     */
    @PostMapping("/update")
    public String updatePatientProfile(
            @Valid @ModelAttribute("patient") Patient updatedForm,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Bitte überprüfen Sie Ihre Eingaben.");
            return "redirect:/profile";
        }

        try {
            Long userId = currentUserService.getCustomerIdFromAuthentication(authentication);

            if (!userId.equals(updatedForm.getId())) {
                redirectAttributes.addFlashAttribute("error", "Zugriff verweigert.");
                return "redirect:/profile";
            }

            // 💡 Получаем ТОЛЬКО пациента
            Patient existing = patientService.findById(userId);

            // Обновляем поля (общие поля, специфичные для пациента — если есть)
            if (updatedForm.getVorname() != null && !updatedForm.getVorname().trim().isEmpty()) {
                existing.setVorname(updatedForm.getVorname());
            }

            if (updatedForm.getNachname() != null && !updatedForm.getNachname().trim().isEmpty()) {
                existing.setNachname(updatedForm.getNachname());
            }

            if (updatedForm.getEmail() != null && !updatedForm.getEmail().trim().isEmpty()
                    && !existing.getEmail().equals(updatedForm.getEmail())) {

                if (patientService.existsByEmailAndIdNot(updatedForm.getEmail(), userId)) {
                    redirectAttributes.addFlashAttribute("error", "Diese E-Mail-Adresse wird bereits verwendet.");
                    return "redirect:/profile";
                }

                existing.setEmail(updatedForm.getEmail());
            }

            if (updatedForm.getTelefonnummer() != null) {
                existing.setTelefonnummer(updatedForm.getTelefonnummer());
            }

            if (updatedForm.getGeburtsdatum() != null) {
                existing.setGeburtsdatum(updatedForm.getGeburtsdatum());
            }

            if (updatedForm.getGeschlecht() != null) {
                existing.setGeschlecht(updatedForm.getGeschlecht());
            }

            if (updatedForm.getAddress() != null) {
                existing.setAddress(updatedForm.getAddress());
            }

            patientService.save(existing);

            redirectAttributes.addFlashAttribute("success", "Profil erfolgreich aktualisiert!");
            return "redirect:/profile?updated=true";

        } catch (Exception e) {
            System.err.println("Fehler beim Aktualisieren des Patient-Profils: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Fehler beim Aktualisieren des Profils. Bitte versuchen Sie es erneut.");
            return "redirect:/profile";
        }
    }

    // ... (остальные методы контроллера без изменений) ...

    /**
     * Изменение пароля
     */



    @PostMapping("/set/mainImage")
    public String setMainImage(@RequestParam("file") MultipartFile file, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Benutzer benutzer = getCurrentBenutzer(authentication);
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Bitte wählen Sie eine Datei aus.");
                return "redirect:/profile";
            }

            String fileName = photoConfig.savePhoto(file);
            benutzer.setMainImage("/img/" + fileName);
            benutzerService.save(benutzer);
            redirectAttributes.addFlashAttribute("success", "Hauptbild erfolgreich aktualisiert!");
        } catch (Exception e) {
            System.err.println("Fehler beim Hochladen des Hauptbildes: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Fehler beim Hochladen des Hauptbildes.");
        }
        return "redirect:/profile";
    }

    @PostMapping("/add/toGallery")
    public String addToGallery(@RequestParam("file") MultipartFile file, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Benutzer benutzer = getCurrentBenutzer(authentication);
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Bitte wählen Sie eine Datei aus.");
                return "redirect:/profile";
            }
            String fileName = photoConfig.savePhoto(file);
            if (benutzer.getGallery() == null) {
                benutzer.setGallery(new java.util.ArrayList<>());
            }
            benutzer.getGallery().add("/img/" + fileName);
            benutzerService.save(benutzer);
            redirectAttributes.addFlashAttribute("success", "Bild zur Galerie hinzugefügt!");
        } catch (Exception e) {
            System.err.println("Fehler beim Hinzufügen zur Galerie: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Fehler beim Hinzufügen zur Galerie.");
        }
        return "redirect:/profile";
    }

    private Benutzer getCurrentBenutzer(Authentication authentication) throws Exception {
        Long userId = currentUserService.getCustomerIdFromAuthentication(authentication);
        if (userId == null) {
            throw new Exception("Benutzer-ID konnte nicht aus Authentifizierung abgerufen werden.");
        }
        return benutzerService.findById(userId);
    }
}