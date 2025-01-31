package it.epicode.pt_webApp.auth;

import it.epicode.pt_webApp.personal_trainer.PersonalTrainer;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public PersonalTrainer register(String username, String password, String email, String nome, String cognome, LocalDate dataDiNascita) {
        if (appUserRepository.existsByUsername(username)) {
            throw new EntityExistsException("Username già in uso");
        }

        PersonalTrainer personalTrainer = new PersonalTrainer();
        personalTrainer.setUsername(username);
        personalTrainer.setPassword(passwordEncoder.encode(password));
        personalTrainer.setEmail(email);
        personalTrainer.setNome(nome);
        personalTrainer.setCognome(cognome);
        personalTrainer.setDataDiNascita(dataDiNascita);
        personalTrainer.setRoles(Set.of(Role.ROLE_PERSONAL_TRAINER));

        return appUserRepository.save(personalTrainer);
    }

    public Optional<AppUser> findByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    public AuthResponse authenticateUser(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenUtil.generateToken(userDetails);


            Optional<AppUser> optionalUser = findByUsername(username);
            AppUser user = optionalUser.orElseThrow(() ->
                    new EntityNotFoundException("Utente non trovato con username: " + username)
            );

            return new AuthResponse(token, user);
        } catch (AuthenticationException e) {
            throw new SecurityException("Credenziali non valide", e);
        }
    }


    public AppUser loadUserByUsername(String username) {
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username: " + username));


        return appUser;
    }
}
