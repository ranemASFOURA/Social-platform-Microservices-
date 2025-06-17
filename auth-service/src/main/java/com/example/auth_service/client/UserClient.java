package com.example.auth_service.client;

import com.example.auth_service.dto.UserAuthDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8080")
public interface UserClient {

    @GetMapping("/api/users/email/{email}/auth")
    UserAuthDTO getUserAuthByEmail(@PathVariable String email);
}
