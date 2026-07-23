package com.example.demo.GDU.Config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private static final ObjectMapper MAPPER =
            new ObjectMapper().registerModule(new JavaTimeModule());
    @Override
    public void handle(
            HttpServletRequest request,       // la requête HTTP entrante (contient l'URL, les headers, etc.)
            HttpServletResponse response,     // la réponse HTTP à renvoyer au client
            AccessDeniedException accessDeniedException) // l'exception levée par Spring Security quand l'accès est refusé
            throws IOException {              // IOException peut être levée lors de l'écriture dans la réponse

        // fixe le code HTTP de la réponse à 403 (Forbidden)
        response.setStatus(HttpStatus.FORBIDDEN.value());

        // indique au client que la réponse est au format JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // force l'encodage UTF-8 pour les caractères spéciaux (accents, etc.)
        response.setCharacterEncoding("UTF-8");

        // crée un map ordonné qui contiendra les champs du corps JSON
        Map<String, Object> body = new LinkedHashMap<>();

        // champ "status" : le code HTTP numérique
        body.put("status", 403);

        // champ "error" : le libellé standard HTTP associé au 403
        body.put("error", "FORBIDDEN");

        // champ "message" : message lisible expliquant le refus d'accès
        body.put("message", "Vous n'avez pas les droits nécessaires pour cette action.");

        // champ "path" : l'URL exacte qui a déclenché l'erreur
        body.put("path", request.getRequestURI());

        // champ "timestamp" : date et heure de l'erreur au format ISO (ex: 2026-07-17T10:30:00)
        body.put("timestamp", LocalDateTime.now().toString());

        // sérialise le map en JSON et l'écrit directement dans le flux de sortie de la réponse
        MAPPER.writeValue(response.getOutputStream(), body);
    }
}

/*
 * ============================================================
 * RÔLE DE CE FICHIER
 * ============================================================
 *
 * JwtAccessDeniedHandler est un gestionnaire d'erreur Spring Security
 * déclenché automatiquement lorsqu'un utilisateur AUTHENTIFIÉ tente
 * d'accéder à une ressource pour laquelle il n'a PAS les droits
 * (ex : un USER qui appelle un endpoint réservé aux ADMIN).
 *
 * Il intercepte l'exception AccessDeniedException et, au lieu de
 * renvoyer la page d'erreur HTML par défaut de Spring, il retourne
 * une réponse JSON structurée avec :
 *   - status    : 403
 *   - error     : "FORBIDDEN"
 *   - message   : explication lisible du refus
 *   - path      : l'URL qui a provoqué l'erreur
 *   - timestamp : l'instant exact de l'erreur
 *
 * Ce handler est enregistré dans la configuration Spring Security
 * via : http.exceptionHandling(e -> e.accessDeniedHandler(...))
 *
 * À NE PAS CONFONDRE avec JwtAuthenticationEntryPoint qui, lui,
 * gère les utilisateurs NON AUTHENTIFIÉS (erreur 401 Unauthorized).
 * ============================================================
 */
