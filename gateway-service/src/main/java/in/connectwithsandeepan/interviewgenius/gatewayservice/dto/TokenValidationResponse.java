package in.connectwithsandeepan.interviewgenius.gatewayservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenValidationResponse {

    private boolean valid;
    private String message;
    private String email;
    private Long userId;
    private String role;
    private Date expiresAt;
}
