package in.connectwithsandeepan.interviewgenius.gatewayservice.dto;

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
    private String authProvider;
    private String profileImageUrl;
}
