package com.glivion.backend.domain.model;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Table(name = "users")
@Entity
@Getter
@Setter
@ToString
public class User {

    @Column(nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;
    @Column(unique = true)
    String username;
    @Column(nullable = false)
    String password;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    Role role;

    @OneToOne(fetch = FetchType.EAGER)
    UserProfile userProfile;

    @CreationTimestamp
    @Column(nullable = false)
    LocalDateTime joinedAt;
    @UpdateTimestamp
    @Column(nullable = false)
    LocalDateTime updatedAt;

}
