package com.example.demo.GDU.Config.security;

import com.example.demo.GDU.entity.Utilisateur;
import com.example.demo.GDU.repository.UtilisateurRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UtilisateurRepository utilisateurRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        try {
            final String email = jwtService.extractEmail(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Charge l'utilisateur avec roles + permissions en une seule requête
                Utilisateur utilisateur = utilisateurRepository
                        .findByEmailWithRolesAndPermissions(email)
                        .orElse(null);

                if (utilisateur != null && utilisateur.isEnabled()) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            utilisateur,
                            null,
                            utilisateur.getAuthorities() // roles/permissions déjà chargés — pas de lazy
                    );
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (ExpiredJwtException ex) {
            request.setAttribute("jwt_error", "Token expiré. Veuillez vous reconnecter.");
        } catch (SignatureException ex) {
            request.setAttribute("jwt_error", "Signature du token invalide.");
        } catch (MalformedJwtException ex) {
            request.setAttribute("jwt_error", "Token malformé.");
        } catch (Exception ex) {
            request.setAttribute("jwt_error", "Token invalide.");
        }

        filterChain.doFilter(request, response);
    }
}
