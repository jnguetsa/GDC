package com.example.demo.GDU.Config.security;

import com.example.demo.GDU.entity.Role;
import com.example.demo.GDU.entity.Utilisateur;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Génère un token JWT pour l'utilisateur donné.
     * Inclut dans les claims : l'id, les rôles, l'email (subject),
     * la date d'émission et la date d'expiration.
     */
    public  String genererToken(Utilisateur utilisateur){
        Map<String,Object> claims=new HashMap<>();
        claims.put("id", utilisateur.getId());
        claims.put("roles", utilisateur.getRoles()
                .stream()
                .map(Role::getNom)
                .toList());
        return Jwts.builder()
                .claims(claims)
                .subject(utilisateur.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Extrait l'email (subject) encodé dans le token JWT.
     */
    public String extractEmail(String token){
        return extraireClaim(token, Claims::getSubject);
    }
    /**
     * Extrait un claim spécifique du token JWT via un resolver fonctionnel.
     * Exemple : extraireClaim(token, Claims::getSubject) retourne le subject.
     */
    public <T> T extraireClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extraireTousClaims(token));
    }



    /**
     * Parse et vérifie la signature du token JWT, puis retourne l'ensemble de ses claims.
     * Lève une exception si le token est invalide ou expiré.
     */
    private Claims extraireTousClaims(String token) {
        return  Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Construit la clé HMAC-SHA256 à partir du secret configuré dans application.properties (jwt.secret).
     */
    private SecretKey getSigningKey() {
        return  Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }


}
