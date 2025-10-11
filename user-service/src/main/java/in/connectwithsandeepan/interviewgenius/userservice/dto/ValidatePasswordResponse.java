package in.connectwithsandeepan.interviewgenius.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidatePasswordResponse {
    private boolean valid;
}
