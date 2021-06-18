package com.glivion.backend.domain.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "user_profiles")
@Entity
@Getter
@Setter
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @OneToOne(mappedBy = "userProfile")
    User user;

    @Column(nullable = false)
    String name;
    @Column(unique = true, nullable = false)
    String emailAddress;
    @Column(unique = true)
    String phoneNumber;


    @CreationTimestamp
    @Column(nullable = false)
    LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(nullable = false)
    LocalDateTime updatedAt;
}
