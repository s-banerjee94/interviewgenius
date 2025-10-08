package in.connectwithsandeepan.interviewgenius.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsResponse {
    private Long totalUsers;
    private Long adminUsers;
    private Long normalUsers;
}
