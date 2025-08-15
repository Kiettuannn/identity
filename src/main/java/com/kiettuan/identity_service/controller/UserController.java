package com.kiettuan.identity_service.controller;

import com.kiettuan.identity_service.dto.request.ApiResponse;
import com.kiettuan.identity_service.dto.request.UserCreationRequest;
import com.kiettuan.identity_service.dto.request.UserUpdateRequest;
import com.kiettuan.identity_service.dto.response.UserResponse;
import com.kiettuan.identity_service.entity.User;
import com.kiettuan.identity_service.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j

public class UserController {
    UserService userService;

    @PostMapping
    ApiResponse<UserResponse> createUser (@RequestBody @Valid UserCreationRequest request){
        log.info("UserController:createUser");
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    // Get all users
    @GetMapping
    List<User> getUsers(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(
                grantedAuthority -> log.info(grantedAuthority.getAuthority())
        );
        return userService.getUsers();
    }

    // Get a user
    @GetMapping("/{userId}")
    UserResponse getUser(@PathVariable String userId){
        return userService.getUser(userId);
    }

    // Get my info
    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo(){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        return "User has been deleted";
    }
}
