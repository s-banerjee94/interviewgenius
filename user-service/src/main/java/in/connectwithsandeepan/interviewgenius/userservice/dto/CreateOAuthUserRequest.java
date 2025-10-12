package in.connectwithsandeepan.interviewgenius.userservice.dto;

import in.connectwithsandeepan.interviewgenius.userservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOAuthUserRequest {
    private String email;
    private String firstName;
    private String lastName;
    private User.AuthProvider authProvider;
    private String profileImageUrl;
}
