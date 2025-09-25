package in.connectwithsandeepan.interviewgenius.useranswerservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private Role role;
    private Boolean isActive;
    private Boolean isVerified;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
    private String profileImageUrl;

    public enum Role {
        ADMIN, NORMAL_USER
    }
}