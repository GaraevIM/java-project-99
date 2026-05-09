package hexlet.code.component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtils {

    private final JwtEncoder jwtEncoder;

    public JwtTokenUtils(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateToken(String username) {
        var now = Instant.now();

        var claims = JwtClaimsSet.builder()
                .issuer("java-project-99")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.DAYS))
                .subject(username)
                .build();

        var header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(org.springframework.security.oauth2.jwt.JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }
}
