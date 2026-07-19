package hexlet.code.controller;

import hexlet.code.component.JwtTokenUtils;
import hexlet.code.dto.LoginDTO;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtils jwtTokenUtils;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenUtils jwtTokenUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @PostMapping("/api/login")
    public String login(@Valid @RequestBody LoginDTO data) {
        var authentication = new UsernamePasswordAuthenticationToken(
                data.getUsername(),
                data.getPassword()
        );

        authenticationManager.authenticate(authentication);

        return jwtTokenUtils.generateToken(data.getUsername());
    }
}
