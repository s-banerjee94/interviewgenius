package in.connectwithsandeepan.interviewgenius.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_role", columnList = "role")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = true)
    private String password; // Nullable for OAuth-only users

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_auth_providers",
        joinColumns = @JoinColumn(name = "user_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "auth_provider"})
    )
    @Column(name = "auth_provider")
    @Builder.Default
    private Set<AuthProvider> authProviders = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column()
    private Experience experience;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Boolean isVerified = false;

    private String phoneNumber;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime lastLoginAt;

    @Column(columnDefinition = "TEXT")
    private String profileImageUrl;

    public enum Role {
        ADMIN, USER
    }

    public enum Experience {
        FRESHER,
        INTERMEDIATE,
        EXPERIENCED
    }

    public enum AuthProvider {
        LOCAL, GOOGLE, GITHUB
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean hasPasswordAuth() {
        return authProviders.contains(AuthProvider.LOCAL) && password != null;
    }

    public boolean hasOAuthProvider(AuthProvider provider) {
        return authProviders.contains(provider);
    }
}