package thermolearn.backend.api.services;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import thermolearn.backend.api.models.AuthenticationRequest;
import thermolearn.backend.api.models.AuthenticationResponse;
import thermolearn.backend.api.models.RegisterRequest;
import thermolearn.backend.api.models.User;
import thermolearn.backend.api.repositories.UserRepository;
import thermolearn.backend.api.utils.JwtService;

@Service
@NoArgsConstructor(force = true)
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) throws Exception {
        assert passwordEncoder != null;
        assert userRepository != null;

        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new Exception("Email already exists");
        }

        var user = User
                .builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        var jwtToken = JwtService.generateToken(user);

        return AuthenticationResponse
                .builder()
                .user(user)
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Authentication authentication;
        try {
            assert authenticationManager != null;
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid email or password", e);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        var jwtToken = JwtService.generateToken(userDetails);

        return AuthenticationResponse.builder()
                .user((User) userDetails)
                .token(jwtToken)
                .build();
    }
}
