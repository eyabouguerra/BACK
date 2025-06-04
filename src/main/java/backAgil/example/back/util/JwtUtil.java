package backAgil.example.back.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "1234567890123456789012345678901234567890123456789012345678901234"; // 64+ caractères



    private static final int TOKEN_VALIDITY =3600 * 5;

    public String getUsernameFromToken(String token)
    {
    return getClaimFromToken(token,Claims::getSubject);
    }
    private <T> T getClaimFromToken(String token, Function<Claims,T> claimResolver){
    final Claims claims = getAllClaimsFromToken(token);
    return claimResolver.apply(claims);
    }
    private Claims getAllClaimsFromToken (String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();


    }
    public boolean validateToken(String token, UserDetails userDetails){
        String userName = getUsernameFromToken(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    private boolean isTokenExpired(String token){
      final Date expirationDate = getExpirationDateFromToken(token);
      return expirationDate.before(new Date());
    }
    private Date getExpirationDateFromToken(String token){
        return getClaimFromToken(token,Claims::getExpiration);
    }
    private Key getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails) {
        // Ajouter des claims personnalisés (comme les rôles)
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities());

        // Vérifiez que votre clé est suffisamment longue pour HS512 (au moins 64 caractères)
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        // Crée et retourne le JWT
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY * 1000L))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)

                .compact();
    }

}

