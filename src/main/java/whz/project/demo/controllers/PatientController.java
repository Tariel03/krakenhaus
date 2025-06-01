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
import whz.project.demo.services.BenutzerService;
import whz.project.demo.services.CurrentUserService;

import jakarta.validation.Valid;
import java.io.IOException;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class PatientController {

    private final BenutzerService benutzerService;
    private final CurrentUserService currentUserService;
    private final PasswordEncoder passwordEncoder;
    private final PhotoConfig photoConfig = new PhotoConfig();

    @GetMapping
    public String showProfile(Model model, Authentication authentication) {
        try {
            Long userId = currentUserService.getCustomerIdFromAuthentication(authentication);
            Benutzer benutzer = benutzerService.findById(userId);

            if (benutzer == null) {
                return "redirect:/login?error=user-not-found";
            }

            model.addAttribute("benutzer", benutzer);
            return "profile";

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
    public String updateProfile(
            @Valid @ModelAttribute("benutzer") Benutzer updatedBenutzerForm, // Переименовал для ясности: это данные из формы
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        // Проверяем ошибки валидации, если есть.
        // Обрати внимание: валидация может пройти для null полей, если они не помечены @NotNull
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Bitte überprüfen Sie Ihre Eingaben.");
            // Можно добавить более подробные ошибки, если нужно:
            // redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.benutzer", bindingResult);
            // redirectAttributes.addFlashAttribute("benutzer", updatedBenutzerForm); // Чтобы сохранить введенные данные
            return "redirect:/profile";
        }

        try {
            Long userId = currentUserService.getCustomerIdFromAuthentication(authentication);

            if (!userId.equals(updatedBenutzerForm.getId())) {
                redirectAttributes.addFlashAttribute("error", "Zugriff verweigert.");
                return "redirect:/profile";
            }

            // ШАГ 1: Получаем СУЩЕСТВУЮЩЕГО пользователя из базы данных
            Benutzer existingBenutzer = benutzerService.findById(userId);
            if (existingBenutzer == null) {
                redirectAttributes.addFlashAttribute("error", "Benutzer nicht gefunden.");
                return "redirect:/profile";
            }

            // ШАГ 2: Обновляем только те поля, которые были ЯВНО заполнены или изменены в форме.
            // Для String полей: проверяем на null и на пустую строку.
            // Для LocalDate: проверяем на null.
            // Для Geschlecht (enum): проверяем на null (выпадающий список всегда отправит что-то, если есть опции).

            // Vorname: обычно обязательное поле
            if (updatedBenutzerForm.getVorname() != null && !updatedBenutzerForm.getVorname().trim().isEmpty()) {
                existingBenutzer.setVorname(updatedBenutzerForm.getVorname());
            }

            // Nachname: обычно обязательное поле
            if (updatedBenutzerForm.getNachname() != null && !updatedBenutzerForm.getNachname().trim().isEmpty()) {
                existingBenutzer.setNachname(updatedBenutzerForm.getNachname());
            }

            // Email: обычно обязательное поле, с проверкой уникальности
            // Сначала проверяем, изменился ли email, чтобы избежать ошибки уникальности, если он не менялся.
            if (updatedBenutzerForm.getEmail() != null && !updatedBenutzerForm.getEmail().trim().isEmpty() &&
                    !existingBenutzer.getEmail().equals(updatedBenutzerForm.getEmail())) { // Проверяем, изменился ли email
                if (benutzerService.existsByEmailAndIdNot(updatedBenutzerForm.getEmail(), userId)) {
                    redirectAttributes.addFlashAttribute("error", "Diese E-Mail-Adresse wird bereits verwendet.");
                    return "redirect:/profile";
                }
                existingBenutzer.setEmail(updatedBenutzerForm.getEmail());
            }
            // Если email не изменился, но пришел из формы, оставляем его как есть (он уже был в existingBenutzer)
            else if (updatedBenutzerForm.getEmail() != null && !updatedBenutzerForm.getEmail().trim().isEmpty()) {
                existingBenutzer.setEmail(updatedBenutzerForm.getEmail()); // Если он был пустым, а стал заполненным
            }


            // Telefonnummer: опциональное поле
            // Мы хотим обновить его, только если оно пришло из формы.
            // Если оно пришло как null или пустая строка, это означает, что пользователь не ввел его или очистил.
            // Если хочешь позволить очищать, то просто: existingBenutzer.setTelefonnummer(updatedBenutzerForm.getTelefonnummer());
            // Если не хочешь перезаписывать, если поле пустое в форме, то такая логика:
            if (updatedBenutzerForm.getTelefonnummer() != null) { // Если null, значит, поле не было отправлено (может быть для некоторых типов полей)
                existingBenutzer.setTelefonnummer(updatedBenutzerForm.getTelefonnummer());
            }


            // Geburtsdatum: опциональное поле (LocalDate)
            // Обновляем, только если из формы пришло не null значение.
            if (updatedBenutzerForm.getGeburtsdatum() != null) {
                existingBenutzer.setGeburtsdatum(updatedBenutzerForm.getGeburtsdatum());
            }

            // Geschlecht: Enum, всегда будет какое-то значение из списка, но может быть null, если список пуст.
            // Если ты позволяешь "пустой" выбор или "не указано", это значение может прийти как null.
            if (updatedBenutzerForm.getGeschlecht() != null) {
                existingBenutzer.setGeschlecht(updatedBenutzerForm.getGeschlecht());
            }

            // Address: опциональное поле
            if (updatedBenutzerForm.getAddress() != null) {
                existingBenutzer.setAddress(updatedBenutzerForm.getAddress());
            }


            // Beschreibung: опциональное поле (Lob String)
            if (updatedBenutzerForm.getBeschreibung() != null) {
                existingBenutzer.setBeschreibung(updatedBenutzerForm.getBeschreibung());
            }


            // Поля, которые НЕ должны меняться через эту форму:
            // existingBenutzer.setLogin(updatedBenutzerForm.getLogin()); // Не меняем логин через эту форму
            // existingBenutzer.setPassword(updatedBenutzerForm.getPassword()); // Пароль меняется отдельным методом
            // existingBenutzer.setRole(updatedBenutzerForm.getRole()); // Роль не меняется через эту форму
            // existingBenutzer.setMainImage(updatedBenutzerForm.getMainImage()); // MainImage меняется отдельным методом
            // existingBenutzer.setGallery(updatedBenutzerForm.getGallery()); // Галерея меняется отдельными методами
            // existingBenutzer.setFachrictungList(updatedBenutzerForm.getFachrictungList()); // Эти списки не меняются тут
            // existingBenutzer.setLeistungenList(updatedBenutzerForm.getLeistungenList()); // Эти списки не меняются тут


            // ШАГ 3: Сохраняем ОБНОВЛЕННЫЙ существующий объект
            benutzerService.save(existingBenutzer);
            redirectAttributes.addFlashAttribute("success",
                    "Profil erfolgreich aktualisiert!");

            return "redirect:/profile?updated=true";

        } catch (Exception e) {
            System.err.println("Fehler beim Aktualisieren des Profils: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Fehler beim Aktualisieren des Profils. Bitte versuchen Sie es erneut.");
            return "redirect:/profile";
        }
    }

    // ... (остальные методы контроллера без изменений) ...

    /**
     * Изменение пароля
     */
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
                return "redirect:/profile";
            }

            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error",
                        "Die Passwörter stimmen nicht überein.");
                return "redirect:/profile";
            }

            if (newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("error",
                        "Das Passwort muss mindestens 6 Zeichen lang sein.");
                return "redirect:/profile";
            }

            Benutzer benutzer = benutzerService.findById(userId);
            if (benutzer == null) {
                redirectAttributes.addFlashAttribute("error", "Benutzer nicht gefunden.");
                return "redirect:/profile";
            }

            if (!passwordEncoder.matches(currentPassword, benutzer.getPassword())) {
                redirectAttributes.addFlashAttribute("error",
                        "Das aktuelle Passwort ist falsch.");
                return "redirect:/profile";
            }

            benutzer.setPassword(passwordEncoder.encode(newPassword));
            benutzerService.save(benutzer);

            redirectAttributes.addFlashAttribute("success",
                    "Passwort erfolgreich geändert!");

            return "redirect:/profile?passwordChanged=true";

        } catch (Exception e) {
            System.err.println("Fehler beim Ändern des Passworts: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Fehler beim Ändern des Passworts. Bitte versuchen Sie es erneut.");
            return "redirect:/profile";
        }
    }


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