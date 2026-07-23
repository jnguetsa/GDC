package com.example.demo.GDU.services.serviceImpl;

import com.example.demo.GDU.Config.security.JwtService;
import com.example.demo.GDU.dto.auth.*;
import com.example.demo.GDU.dto.role.RoleResponse;
import com.example.demo.GDU.entity.RefreshToken;
import com.example.demo.GDU.entity.Role;
import com.example.demo.GDU.entity.Utilisateur;
import com.example.demo.GDU.exeption.AuthException;
import com.example.demo.GDU.exeption.RoleNotFoundException;
import com.example.demo.GDU.mappers.RoleMapper;
import com.example.demo.GDU.repository.RefreshTokenRepository;
import com.example.demo.GDU.repository.RoleRepository;
import com.example.demo.GDU.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpiration;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        Utilisateur utilisateur = utilisateurRepository.findByEmailWithRolesAndPermissions(request.getEmail())
                .orElseThrow(() -> new AuthException("Utilisateur introuvable"));

        utilisateur.setDerniereConnexion(LocalDateTime.now());

        refreshTokenRepository.deleteByUtilisateur(utilisateur);
        String rawRefresh = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(rawRefresh)
                .utilisateur(utilisateur)
                .dateExpiration(Instant.now().plusMillis(refreshExpiration))
                .build();
        refreshTokenRepository.save(refreshToken);

        log.info("Connexion réussie pour : {}", utilisateur.getEmail());
        return AuthResponse.builder()
                .accessToken(jwtService.genererToken(utilisateur))
                .refreshToken(rawRefresh)
                .tokenType("Bearer")
                .expiresIn(jwtExpiration)
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AuthException("Les mots de passe ne correspondent pas");
        }
        if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AuthException("Un compte avec cet email existe déjà : " + request.getEmail());
        }
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(request.getEmail());
        utilisateur.setPassword(passwordEncoder.encode(request.getPassword()));
        utilisateurRepository.save(utilisateur);

        log.info("Nouveau compte créé : {}", utilisateur.getEmail());
        return AuthResponse.builder()
                .accessToken(jwtService.genererToken(utilisateur))
                .refreshToken(genererEtSauvegarderRefreshToken(utilisateur))
                .tokenType("Bearer")
                .expiresIn(jwtExpiration)
                .build();
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new AuthException("Refresh token invalide"));
        if (!refreshToken.estValide()) {
            refreshTokenRepository.delete(refreshToken);
            throw new AuthException("Refresh token expiré, veuillez vous reconnecter");
        }
        Utilisateur utilisateur = utilisateurRepository
                .findByEmailWithRolesAndPermissions(refreshToken.getUtilisateur().getEmail())
                .orElseThrow(() -> new AuthException("Utilisateur introuvable"));

        return AuthResponse.builder()
                .accessToken(jwtService.genererToken(utilisateur))
                .refreshToken(request.getRefreshToken())
                .tokenType("Bearer")
                .expiresIn(jwtExpiration)
                .build();
    }

    public void logout(LogoutRequest request) {
        refreshTokenRepository.findByToken(request.getRefreshToken())
                .ifPresent(refreshTokenRepository::delete);
        log.info("Déconnexion effectuée");
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfil(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmailWithRolesAndPermissions(email)
                .orElseThrow(() -> new AuthException("Utilisateur introuvable"));
        return ProfileResponse.builder()
                .id(utilisateur.getId())
                .email(utilisateur.getEmail())
                .compteActif(utilisateur.isCompteActif())
                .derniereConnexion(utilisateur.getDerniereConnexion())
                .dateCreation(utilisateur.getDateCreation())
                .roles(roleMapper.rolesToRoleResponseList(utilisateur.getRoles().stream().toList()))
                .build();
    }

    public ProfileResponse activerCompte(ActivationRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findById(request.getUtilisateurId())
                .orElseThrow(() -> new AuthException("Utilisateur introuvable avec l'id : " + request.getUtilisateurId()));
        utilisateur.setCompteActif(request.getCompteActif());
        log.info("Compte {} {} pour l'utilisateur {}", request.getCompteActif() ? "activé" : "désactivé", "", utilisateur.getEmail());
        return getProfil(utilisateur.getEmail());
    }

    public ProfileResponse assignerRoles(AssignRoleRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findByEmailWithRolesAndPermissions(
                utilisateurRepository.findById(request.getUtilisateurId())
                        .orElseThrow(() -> new AuthException("Utilisateur introuvable"))
                        .getEmail()
        ).orElseThrow(() -> new AuthException("Utilisateur introuvable"));

        Set<Role> roles = request.getRoleIds().stream()
                .map(id -> roleRepository.findById(id)
                        .orElseThrow(() -> new RoleNotFoundException("Rôle introuvable avec l'id : " + id)))
                .collect(Collectors.toSet());

        utilisateur.setRoles(roles);
        log.info("{} rôle(s) assigné(s) à {}", roles.size(), utilisateur.getEmail());
        return getProfil(utilisateur.getEmail());
    }

    private String genererEtSauvegarderRefreshToken(Utilisateur utilisateur) {
        String raw = UUID.randomUUID().toString();
        refreshTokenRepository.save(RefreshToken.builder()
                .token(raw)
                .utilisateur(utilisateur)
                .dateExpiration(Instant.now().plusMillis(refreshExpiration))
                .build());
        return raw;
    }
}