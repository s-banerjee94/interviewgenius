package in.connectwithsandeepan.interviewgenius.gatewayservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinkOAuthProviderRequest {
    private Long userId;
    private String authProvider;
}
