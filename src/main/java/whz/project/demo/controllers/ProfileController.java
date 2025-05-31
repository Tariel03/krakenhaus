//package whz.project.demo.controllers;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import whz.project.demo.Photos.PhotoConfig;
//import whz.project.demo.entity.Benutzer;
//import whz.project.demo.services.BenutzerService;
//import whz.project.demo.services.CurrentUserService;
//
//import jakarta.validation.Valid;
//
//@Controller
//@RequestMapping("/profile")
//@RequiredArgsConstructor
//public class ProfileController {
//
//    private final BenutzerService benutzerService;
//    private final CurrentUserService currentUserService;
//    private final PasswordEncoder passwordEncoder;
//    PhotoConfig photoConfig = new PhotoConfig();
//    /**
//     * Отображение страницы профиля
//     */
//    @GetMapping
//    public String showProfile(Model model, Authentication authentication) throws Exception {
//        try {
//            Long userId = currentUserService.getCustomerIdFromAuthentication(authentication);
//            Benutzer benutzer = benutzerService.findById(userId);
//
//            if (benutzer == null) {
//                return "redirect:/login?error=user-not-found";
//            }
//
//            model.addAttribute("benutzer", benutzer);
//            return "profile";
//
//        } catch (Exception e) {
//            System.err.println("Fehler beim Laden des Profils: " + e.getMessage());
//            return "redirect:/main?error=profile-load-failed";
//        }
//    }
//
//    /**
//     * Обновление профиля пользователя
//     */
//    @PostMapping("/update")
//    public String updateProfile(
//            @Valid @ModelAttribute Benutzer benutzer,
//            BindingResult bindingResult,
//            Authentication authentication,
//            RedirectAttributes redirectAttributes) {
//
//        if (bindingResult.hasErrors()) {
//            redirectAttributes.addFlashAttribute("error", "Bitte überprüfen Sie Ihre Eingaben.");
//            return "redirect:/profile";
//        }
//
//        try {
//            Long userId = currentUserService.getCustomerIdFromAuthentication(authentication);
//
//            // Проверяем, что пользователь редактирует свой собственный профиль
//            if (!userId.equals(benutzer.getId())) {
//                redirectAttributes.addFlashAttribute("error", "Zugriff verweigert.");
//                return "redirect:/profile";
//            }
//
//            // Получаем текущего пользователя из БД для сохранения критических полей
//            Benutzer existingBenutzer = benutzerService.findById(userId);
//            if (existingBenutzer == null) {
//                redirectAttributes.addFlashAttribute("error", "Benutzer nicht gefunden.");
//                return "redirect:/profile";
//            }
//
//            // Обновляем только разрешенные поля
//            existingBenutzer.setVorname(benutzer.getVorname());
//            existingBenutzer.setNachname(benutzer.getNachname());
//            existingBenutzer.setEmail(benutzer.getEmail());
//            existingBenutzer.setTelefonnummer(benutzer.getTelefonnummer());
//            existingBenutzer.setGeburtsdatum(benutzer.getGeburtsdatum());
//            existingBenutzer.setGeschlecht(benutzer.getGeschlecht());
//            existingBenutzer.setAddress(benutzer.getAddress());
//
//            // Проверяем уникальность email
//            if (benutzerService.existsByEmailAndIdNot(benutzer.getEmail(), userId)) {
//                redirectAttributes.addFlashAttribute("error",
//                        "Diese E-Mail-Adresse wird bereits verwendet.");
//                return "redirect:/profile";
//            }
//
//            benutzerService.save(existingBenutzer);
//            redirectAttributes.addFlashAttribute("success",
//                    "Profil erfolgreich aktualisiert!");
//
//            return "redirect:/profile?updated=true";
//
//        } catch (Exception e) {
//            System.err.println("Fehler beim Aktualisieren des Profils: " + e.getMessage());
//            redirectAttributes.addFlashAttribute("error",
//                    "Fehler beim Aktualisieren des Profils. Bitte versuchen Sie es erneut.");
//            return "redirect:/profile";
//        }
//    }
//
//    /**
//     * Изменение пароля
//     */
//    @PostMapping("/change-password")
//    public String changePassword(
//            @RequestParam("currentPassword") String currentPassword,
//            @RequestParam("newPassword") String newPassword,
//            @RequestParam("confirmPassword") String confirmPassword,
//            @RequestParam("userId") Long userId,
//            Authentication authentication,
//            RedirectAttributes redirectAttributes) {
//
//        try {
//            Long authenticatedUserId = currentUserService.getCustomerIdFromAuthentication(authentication);
//
//            // Проверяем, что пользователь меняет свой собственный пароль
//            if (!authenticatedUserId.equals(userId)) {
//                redirectAttributes.addFlashAttribute("error", "Zugriff verweigert.");
//                return "redirect:/profile";
//            }
//
//            // Проверяем совпадение нового пароля и подтверждения
//            if (!newPassword.equals(confirmPassword)) {
//                redirectAttributes.addFlashAttribute("error",
//                        "Die Passwörter stimmen nicht überein.");
//                return "redirect:/profile";
//            }
//
//            // Проверяем минимальную длину пароля
//            if (newPassword.length() < 6) {
//                redirectAttributes.addFlashAttribute("error",
//                        "Das Passwort muss mindestens 6 Zeichen lang sein.");
//                return "redirect:/profile";
//            }
//
//            Benutzer benutzer = benutzerService.findById(userId);
//            if (benutzer == null) {
//                redirectAttributes.addFlashAttribute("error", "Benutzer nicht gefunden.");
//                return "redirect:/profile";
//            }
//
//            // Проверяем текущий пароль
//            if (!passwordEncoder.matches(currentPassword, benutzer.getPassword())) {
//                redirectAttributes.addFlashAttribute("error",
//                        "Das aktuelle Passwort ist falsch.");
//                return "redirect:/profile";
//            }
//
//            // Устанавливаем новый пароль
//            benutzer.setPassword(passwordEncoder.encode(newPassword));
//            benutzerService.save(benutzer);
//
//            redirectAttributes.addFlashAttribute("success",
//                    "Passwort erfolgreich geändert!");
//
//            return "redirect:/profile?password-changed=true";
//
//        } catch (Exception e) {
//            System.err.println("Fehler beim Ändern des Passworts: " + e.getMessage());
//            redirectAttributes.addFlashAttribute("error",
//                    "Fehler beim Ändern des Passworts. Bitte versuchen Sie es erneut.");
//            return "redirect:/profile";
//        }
//    }
//
//
//
//    @PostMapping("/set/mainImage")
//    public String setMainImage(@RequestParam MultipartFile file, Authentication authentication) throws Exception {
//        Benutzer benutzer = getCurrentBenutzer(authentication);
//        String safeFileName = file.getOriginalFilename().replaceAll("\\s+", "_");
//        photoConfig.savePhoto(file);
//        benutzer.setMainImage("img/" + safeFileName);
//        benutzerService.save(benutzer);
//        return "redirect:/profile";
//
//    }
//    @PostMapping("/add/toGallery")
//    public String addToGallery(@RequestParam MultipartFile file, Authentication authentication) throws Exception {
//        photoConfig.savePhoto(file);
//        Benutzer benutzer = getCurrentBenutzer(authentication);
//        benutzer.getGallery().add("img/"+file.getOriginalFilename());
//        benutzerService.save(benutzer);
//        return "redirect:/profile";
//
//    }
//
//    private Benutzer getCurrentBenutzer(Authentication authentication) throws Exception {
//        return benutzerService.findById(currentUserService.getCustomerIdFromAuthentication(authentication));
//    }
//
//}
////    /**
////     * AJAX endpoint для проверки доступности email
////     */
////    @GetMapping("/check-email")
////    @ResponseBody
////    public boolean checkEmailAvailability(
////            @RequestParam("email") String email,
////            Authentication authentication) {
////
////        try {
////            Long userId = currentUserService.getCustomerIdFromAuthentication(authentication);
////            return !benutzerService.existsByEmailAndIdNot(email, userId);
////        }
