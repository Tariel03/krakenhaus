package whz.project.demo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import whz.project.demo.entity.Benutzer;
import whz.project.demo.repos.BenutzerRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BenutzerDetailsService implements UserDetailsService {
    private final BenutzerRepository benutzerRepository;
    private final String USER_NOT_FOUND = "user with email %s not found";

    public BenutzerDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            Optional<Benutzer> optional = benutzerRepository.findByLogin(username);
            if(optional.isEmpty()){
                throw new UsernameNotFoundException(String.format(USER_NOT_FOUND, username));
            }
            return new BenutzerDetails(optional.get());
    }
}
