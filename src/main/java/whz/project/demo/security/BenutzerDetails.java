package whz.project.demo.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import whz.project.demo.entity.Benutzer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class BenutzerDetails implements UserDetails {
    private final Benutzer benutzer;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(benutzer.getRole().toString()));
    }

    public Benutzer getBenutzer() {
        return benutzer;
    }
    @Override
    public String getPassword() {
        return this.benutzer.getPassword();
    }

    @Override
    public String getUsername() {
        return this.benutzer.getLogin();
    }

    public String getVorname() {
        return this.benutzer.getVorname();
    }

    public String getNachname() {
        return benutzer.getNachname();
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
