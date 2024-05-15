package thermolearn.backend.api.services;

import jakarta.transaction.Transactional;
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
import thermolearn.backend.api.entities.AuthenticationRequest;
import thermolearn.backend.api.entities.AuthenticationResponse;
import thermolearn.backend.api.entities.RegisterRequest;
import thermolearn.backend.api.entities.VerificationCodeType;
import thermolearn.backend.api.models.*;
import thermolearn.backend.api.repositories.UserRepository;
import thermolearn.backend.api.repositories.VerificationCodeRepository;
import thermolearn.backend.api.utils.JwtService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@NoArgsConstructor(force = true)
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final VerificationCodeRepository verificationCodeRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final AuthenticationManager authenticationManager;
    private final SESService sesService;

    @Autowired
    public UserService(UserRepository userRepository, VerificationCodeRepository verificationCodeRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, SESService sesService) {
        this.userRepository = userRepository;
        this.verificationCodeRepository = verificationCodeRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.sesService = sesService;
    }

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
                .isAccountVerified(false)
                .build();

        sendRegistrationCode(user.getEmail());

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

    public VerificationCode sendRegistrationCode(String email) {
        return sendVerificationCode(email, VerificationCodeType.REGISTER);
    }

    @Transactional
    public boolean verifyRegistrationCode(String email, String code){
        assert userRepository != null;
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        boolean verified = verifyCode(email, code, VerificationCodeType.REGISTER);

        if(verified){
            user.setAccountVerified(true);
        }
        else{
            throw new RuntimeException("Verification code is invalid");
        }

        return verified;
    }

    public boolean verifyCode(String email, String code, VerificationCodeType type){
        assert verificationCodeRepository != null;
        VerificationCode verificationCode = verificationCodeRepository.findValidCode(code, email, LocalDateTime.now(), type);

        if(verificationCode == null)
            return false;

        if(verificationCode.isUsed())
            return false;

        verificationCode.setUsed(true);
        verificationCodeRepository.save(verificationCode);

        return true;
    }

    @Transactional
    public VerificationCode sendVerificationCode(String email, VerificationCodeType type) {
        String code = VerificationCode.generateCode();

        var verificationCode = VerificationCode
                .builder()
                .code(code)
                .userEmail(email)
                .type(type)
                .generationTime(LocalDateTime.now())
                .expirationTime(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();

        assert verificationCodeRepository != null;
        verificationCodeRepository.save(verificationCode);

        assert sesService != null;
        sesService.sendEmail(email, code + " - Your Thermolearn Verification Code", "Your Thermolearn verification code is " + verificationCode.getCode() + ".\nIf you did not make this request, you can simply ignore this message.");

        return verificationCode;
    }
}
