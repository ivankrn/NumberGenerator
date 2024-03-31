package ru.ivankrn.numbergenerator.api.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.ivankrn.numbergenerator.api.exception.NotFoundException;
import ru.ivankrn.numbergenerator.domain.repository.UserRepository;
import ru.ivankrn.numbergenerator.infrastructure.security.JwtService;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private static final String USER_NOT_FOUND_MESSAGE = "User not found";

    public AuthenticationService(UserRepository userRepository,
                                 JwtService jwtService,
                                 AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.username(),
                        authenticationRequest.password()
                )
        );
        UserDetails userDetails = userRepository.findByUsername(authenticationRequest.username())
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE));
        String jwtToken = jwtService.generateToken(userDetails);
        return new AuthenticationResponse(jwtToken);
    }

}
