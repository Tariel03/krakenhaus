package whz.project.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import whz.project.demo.entity.Arzt;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.entity.Patient;
import whz.project.demo.enums.Role;
import whz.project.demo.services.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final BenutzerService benutzerService;
    private final RegistrationService registrationService;
    private final CurrentUserService currentUserService;
    private final  ProfileRedirectService profileRedirectService;;
    private final  PasswordService passwordService;

    @GetMapping("/register/patient")
    public String showPatientRegisterForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "auth/register-patient";
    }

    @PostMapping("/register/patient")
    public String registerPatient(@ModelAttribute("patient") Patient patient, RedirectAttributes redirectAttributes) {
        registrationService.registrierePatient(patient);

        redirectAttributes.addFlashAttribute("success",
                "Registrierung erfolgreich! Du kannst dich jetzt einloggen.");
        return "redirect:/login";
    }

    @GetMapping("/register/arzt")
    public String showArztRegisterForm(Model model) {
        model.addAttribute("arzt", new Arzt());
        return "auth/register-arzt";
    }

    @PostMapping("/register/arzt")
    public String registerArzt(@ModelAttribute("arzt") Arzt arzt, RedirectAttributes redirectAttributes) {
        registrationService.registriereArzt(arzt);
        redirectAttributes.addFlashAttribute("success",
                "Registrierung abgeschlossen. Dein Konto wird nach Prüfung durch einen Admin freigeschaltet.");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }


    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("userId") Long userId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            Long authenticatedUserId = currentUserService.getCustomerIdFromAuthentication(authentication);

            if (!authenticatedUserId.equals(userId)) {
                redirectAttributes.addFlashAttribute("error", "Zugriff verweigert.");
                return "redirect:/";
            }

            Benutzer benutzer = benutzerService.findById(userId);

            String redirect = "redirect:" + profileRedirectService.getRedirectPath(benutzer);

            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Die Passwörter stimmen nicht überein.");
                return redirect;
            }

            if (newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Das Passwort muss mindestens 6 Zeichen lang sein.");
                return redirect;
            }


            passwordService.changePassword(userId, currentPassword, newPassword);
            redirectAttributes.addFlashAttribute("success", "Passwort erfolgreich geändert!");
            return redirect + "?passwordChanged=true";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Fehler beim Ändern des Passworts.");
            return "redirect:/";
        }
    }
}

