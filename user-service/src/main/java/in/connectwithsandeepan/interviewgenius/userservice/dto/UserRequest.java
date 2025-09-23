package in.connectwithsandeepan.interviewgenius.userservice.dto;

import in.connectwithsandeepan.interviewgenius.userservice.entity.User;
import lombok.Data;

@Data
public class UserRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private User.Role role;
    private String phoneNumber;
}