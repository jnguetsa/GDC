package com.example.demo.GDU.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private String nom;
    private String description;
    private boolean actif = true;

    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Setter(AccessLevel.NONE)
    private LocalDateTime dateModification;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<Role> roles;
}