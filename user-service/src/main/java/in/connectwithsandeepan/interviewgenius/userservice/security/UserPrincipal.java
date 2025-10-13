package in.connectwithsandeepan.interviewgenius.userservice.security;

import in.connectwithsandeepan.interviewgenius.userservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Principal;

/**
 * Custom Principal class that holds authenticated user information.
 * This is populated from JWT claims and used by Spring Security for authorization.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements Principal {

    private Long userId;
    private String email;
    private User.Role role;

    @Override
    public String getName() {
        return email;
    }

    public boolean isAdmin() {
        return role == User.Role.ADMIN;
    }

    public boolean isUser() {
        return role == User.Role.USER;
    }
}
