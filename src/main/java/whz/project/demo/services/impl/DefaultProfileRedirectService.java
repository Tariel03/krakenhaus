package whz.project.demo.services.impl;

import org.springframework.stereotype.Service;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.enums.Role;
import whz.project.demo.services.ProfileRedirectService;

@Service
public class DefaultProfileRedirectService implements ProfileRedirectService {

    @Override
    public String getRedirectPath(Benutzer benutzer) {
        if (benutzer == null || benutzer.getRole() == null) {
            return "/profile";
        }

        return switch (benutzer.getRole()) {
            case ARZT -> "/arzt/profil-bearbeiten";
            case PATIENT -> "/profile/patient";
            case ADMIN -> "/profile/admin";
            default -> "/profile";
        };
    }
}
