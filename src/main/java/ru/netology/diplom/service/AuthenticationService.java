package ru.netology.diplom.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.netology.diplom.Token.AuthRequest;
import ru.netology.diplom.Token.AuthResponse;
import ru.netology.diplom.Token.JWTUtil;
import ru.netology.diplom.repository.UserTokenRepository;


@AllArgsConstructor
@Service
@Slf4j
public class AuthenticationService {

    private JWTUtil jwtUtil;
    private AuthenticationManager authenticationManager;
    private UserTokenRepository userTokenRepository;

    public AuthResponse createAuthenticationToken(AuthRequest authRequest) {
        final String userName = authRequest.login();
        final String password = authRequest.password();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userName,
                        password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateToken(authentication);
        userTokenRepository.saveUserToken(userName, jwt);
        log.info("User {} authentication. Token {}",userName,jwt);
        return new AuthResponse(jwt);
    }
}
