package in.connectwithsandeepan.interviewgenius.useranswerservice.client;

import in.connectwithsandeepan.interviewgenius.useranswerservice.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/api/v1/users")
public interface UserServiceClient {

    @GetMapping("/{id}")
    UserResponse getUserById(@PathVariable Long id);

    @GetMapping("/email/{email}")
    UserResponse getUserByEmail(@PathVariable String email);

    @GetMapping("/exists")
    Boolean existsByEmail(@PathVariable String email);
}