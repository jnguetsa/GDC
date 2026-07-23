package com.example.demo.GDU.Config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    // ObjectMapper partagé et configuré une seule fois pour sérialiser LocalDateTime en JSON.
    private static final ObjectMapper MAPPER =
            new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * Appelé automatiquement par Spring Security dès qu'une requête non authentifiée
     * tente d'accéder à une ressource protégée.
     *
     * Comportement :
     * 1. Lit l'attribut "jwt_error" posé par le filtre JwtAuthFilter si le token
     *    était présent mais invalide/expiré. Sinon utilise un message générique.
     * 2. Définit le statut HTTP 401 et le content-type JSON.
     * 3. Écrit un corps JSON avec : status, error, message, path et timestamp.
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {

        // Récupère le message d'erreur JWT précis s'il a été stocké par le filtre,
        // sinon tombe sur un message générique.
        String message = request.getAttribute("jwt_error") != null
                ? (String) request.getAttribute("jwt_error")
                : "Token absent ou invalide. Veuillez vous authentifier.";

        // Configure la réponse HTTP : 401 Unauthorized, corps en JSON UTF-8.
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // Construit le corps JSON de la réponse d'erreur.
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 401);
        body.put("error", "Unauthorized");
        body.put("message", message);
        body.put("path", request.getRequestURI());       // URL appelée par le client
        body.put("timestamp", LocalDateTime.now().toString());

        // Sérialise la map en JSON et l'écrit directement dans le flux de réponse.
        MAPPER.writeValue(response.getOutputStream(), body);
    }
}
