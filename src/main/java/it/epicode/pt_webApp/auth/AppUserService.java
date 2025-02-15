package it.epicode.pt_webApp.auth;

import it.epicode.pt_webApp.cliente.Cliente;
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

    public AppUser register(RegisterRequest request) {
        if (appUserRepository.existsByUsername(request.getUsername())) {
            throw new EntityExistsException("Username già in uso");
        }

        AppUser user;

        if (request.getRoles().contains(Role.ROLE_USER)) {

            Cliente cliente = new Cliente();
            cliente.setUsername(request.getUsername());
            cliente.setPassword(passwordEncoder.encode(request.getPassword()));
            cliente.setEmail(request.getEmail());

            cliente.setNome(request.getNome());
            cliente.setCognome(request.getCognome());
            cliente.setDataDiNascita(request.getDataDiNascita());
            cliente.setRoles(request.getRoles());
            user = cliente;
        } else if (request.getRoles().contains(Role.ROLE_PERSONAL_TRAINER)) {

            PersonalTrainer pt = new PersonalTrainer();
            pt.setUsername(request.getUsername());
            pt.setPassword(passwordEncoder.encode(request.getPassword()));
            pt.setEmail(request.getEmail());

            pt.setNome(request.getNome());
            pt.setCognome(request.getCognome());
            pt.setDataDiNascita(request.getDataDiNascita());
            pt.setRoles(request.getRoles());
            user = pt;
        } else {
            throw new IllegalArgumentException("Ruolo non valido. Usa ROLE_USER o ROLE_PERSONAL_TRAINER");
        }

        return appUserRepository.save(user);
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
