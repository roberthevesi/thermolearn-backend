package thermolearn.backend.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thermolearn.backend.api.entities.*;
import thermolearn.backend.api.services.SecretsManagerService;
import thermolearn.backend.api.services.UserService;
import thermolearn.backend.api.utils.DeviceShadowService;
import thermolearn.backend.api.utils.MqttPublisher;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private MqttPublisher mqttPublisher;
    @Autowired
    private DeviceShadowService deviceShadowService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest registerRequest
    ) {
        try {
            return ResponseEntity.ok(userService.register(registerRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/send-new-registration-code")
    public ResponseEntity<?> sendNewRegistrationCode(
            @RequestParam String email
    ) {
        try {
            return ResponseEntity.ok(userService.sendRegistrationCode(email));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify-registration-code")
    public ResponseEntity<?> verifyRegistrationCode(
            @RequestBody VerificationCodeRequest verificationCodeRequest
            ) {
        try {
            return ResponseEntity.ok(userService.verifyRegistrationCode(verificationCodeRequest.getEmail(), verificationCodeRequest.getCode()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/send-forgotten-password-code")
    public ResponseEntity<?> sendForgottenPasswordCode(
            @RequestParam String email
    ) {
        try {
            return ResponseEntity.ok(userService.sendForgottenPasswordCode(email));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify-forgotten-password-code")
    public ResponseEntity<?> verifyForgottenPasswordCode(
            @RequestBody VerificationCodeRequest verificationCodeRequest
    ) {
        try {
            return ResponseEntity.ok(userService.verifyForgottenPasswordCode(verificationCodeRequest.getEmail(), verificationCodeRequest.getCode()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-forgotten-password")
    public ResponseEntity<?> resetForgottenPassword(
            @RequestBody ResetForgottenPasswordRequest resetForgottenPasswordRequest
    ) {
        try {
            return ResponseEntity.ok(userService.resetForgottenPassword(resetForgottenPasswordRequest.getEmail(), resetForgottenPasswordRequest.getPassword()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @RequestBody AuthenticationRequest authenticationRequest
    ){
        try {
            return ResponseEntity.ok(userService.authenticate(authenticationRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-user-paired-thermostats")
    public ResponseEntity<?> getUserPairedThermostats(
            @RequestParam Long userId
    ) {
        try {
            return ResponseEntity.ok(userService.getUserPairedThermostats(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update-user-home-location")
    public ResponseEntity<?> updateUserHomeLocation(
            @RequestBody UpdateUserHomeLocationRequest request
            ) {
        try {
            return ResponseEntity.ok(userService.updateUserHomeLocation(request.getUserId(), request.getHomeLatitude(), request.getHomeLongitude()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update-user-distance-from-home")
    public ResponseEntity<?> updateUserDistanceFromHome(
            @RequestBody UpdateUserDistanceFromHomeRequest request
    ) {
        try {
            return ResponseEntity.ok(userService.updateUserDistanceFromHome(request.getUserId(), request.getDistanceFromHome()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-user-distance-from-home")
    public ResponseEntity<?> getUserDistanceFromHome(
            @RequestParam String thermostatId
    ) {
        try {
            return ResponseEntity.ok(userService.getUserDistanceFromHome(thermostatId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
