package in.connectwithsandeepan.interviewgenius.gatewayservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;

    @Builder.Default
    private String tokenType = "Bearer";

    private Long expiresIn; // Expiration time in seconds
}
