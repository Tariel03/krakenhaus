package whz.project.demo.services;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.security.BenutzerDetails;

import java.security.Principal;

@Service
public class CurrentUserService {
    private  final RegistrationService registrationService;

    public CurrentUserService(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    public Long getCustomerIdFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Authentication is null or not authenticated.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof BenutzerDetails benutzerDetails) {
            String loginName = benutzerDetails.getUsername();
            Benutzer benutzer = registrationService.findByLogin(loginName);

            if (benutzer == null) {
                throw new IllegalStateException("Kein Benutzer mit Login " + loginName + " gefunden.");
            }

            return benutzer.getId();
        }

        throw new IllegalArgumentException("Unsupported principal type: " + principal.getClass());
    }





}
