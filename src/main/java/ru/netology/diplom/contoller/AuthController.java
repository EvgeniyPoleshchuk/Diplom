package ru.netology.diplom.contoller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.diplom.Token.AuthRequest;
import ru.netology.diplom.repository.UserTokenRepository;
import ru.netology.diplom.service.AuthenticationService;

@RestController
@AllArgsConstructor
public class AuthController {

    private AuthenticationService service;
    private UserTokenRepository tokenRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(service.createAuthenticationToken(authRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("auth-token") String authToken) {
        tokenRepository.deleteUserAndToken(authToken);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
