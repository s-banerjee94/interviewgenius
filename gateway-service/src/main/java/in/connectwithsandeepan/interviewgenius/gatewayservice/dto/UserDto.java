package in.connectwithsandeepan.interviewgenius.gatewayservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Set<String> authProviders;
    private Map<String, String> oauthProviderIds;
    private String role;
    private Boolean isActive;
    private Boolean isVerified;
}
