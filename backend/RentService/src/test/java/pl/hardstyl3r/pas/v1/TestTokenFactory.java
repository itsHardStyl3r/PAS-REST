package pl.hardstyl3r.pas.v1;

import io.jsonwebtoken.Jwts;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

public final class TestTokenFactory {

    private static final PrivateKey PRIVATE_KEY = loadPrivateKey();

    private TestTokenFactory() {
    }

    public static String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86_400_000))
                .signWith(PRIVATE_KEY)
                .compact();
    }

    private static PrivateKey loadPrivateKey() {
        try {
            byte[] bytes = new ClassPathResource("keys/private_key.pem").getInputStream().readAllBytes();
            String base64 = new String(bytes, StandardCharsets.UTF_8)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] der = Base64.getDecoder().decode(base64);
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(der));
        } catch (Exception e) {
            throw new IllegalStateException("Nie udalo sie wczytac klucza prywatnego do testow", e);
        }
    }
}
