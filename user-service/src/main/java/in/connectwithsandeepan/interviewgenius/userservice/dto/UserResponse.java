package in.connectwithsandeepan.interviewgenius.userservice.dto;

import in.connectwithsandeepan.interviewgenius.userservice.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private User.Experience experience;
    private User.Role role;
    private Boolean isActive;
    private Boolean isVerified;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
    private String profileImageUrl;

    public static UserResponse fromUser(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setFullName(user.getFullName());
        response.setExperience(user.getExperience());
        response.setRole(user.getRole());
        response.setIsActive(user.getIsActive());
        response.setIsVerified(user.getIsVerified());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        response.setLastLoginAt(user.getLastLoginAt());
        response.setProfileImageUrl(user.getProfileImageUrl());
        return response;
    }
}