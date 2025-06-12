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

    private static final String SECRET_KEY = "1234567890123456789012345678901234567890123456789012345678901234"; // (64+ caractères ) pour signer le JWT


    private static final int TOKEN_VALIDITY =3600 * 5; //  Durée de validité du token en secondes

    // Méthode pour extraire le nom d'utilisateur (subject) du token.
    public String getUsernameFromToken(String token)
    {
        return getClaimFromToken(token,Claims::getSubject);//pour lire la donnée "subject" (qui contient le nom d'utilisateur).
    }

    //Méthode générique pour lire un champ spécifique (claim) du token.
    private <T> T getClaimFromToken(String token, Function<Claims,T> claimResolver){
        final Claims claims = getAllClaimsFromToken(token); //recuperer les donnees du token
        return claimResolver.apply(claims);
    }

    // Cette partie vérifie et décode le token à l’aide de la clé de signature, puis retourne le contenu du token.
    private Claims getAllClaimsFromToken (String token){
        return Jwts.parserBuilder() //constructeur d'analyseur pour decoder et verifier token JwT
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token) //Elle vérifie la signature avec la clé donnée
                .getBody(); //la partie payload du token (claims du token)


    }


    public boolean validateToken(String token, UserDetails userDetails){
        String userName = getUsernameFromToken(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


    private boolean isTokenExpired(String token){
        // Compare la date d’expiration du token à la date actuelle.
        final Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }


    private Date getExpirationDateFromToken(String token){
        return getClaimFromToken(token,Claims::getExpiration);
    }


    private Key getSigningKey() {
        // Convertit la chaîne en tableau de bytes et la transforme en clé avec hmacShaKeyFor.
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
                .setClaims(claims) // ajoute les rôles.
                .setSubject(userDetails.getUsername()) //met le nom d’utilisateur.
                .setIssuedAt(new Date()) // date de création.
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY * 1000L)) // date d’expiration.
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)    //on signe le token avec HS512.
                .compact(); //retourne le token final sous forme de chaîne.


    }

}