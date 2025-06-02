package whz.project.demo.services;

import whz.project.demo.entity.Benutzer;

public interface ProfileRedirectService {
    String getRedirectPath(Benutzer benutzer);
}
