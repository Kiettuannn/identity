package com.kiettuan.identity_service.controller;

import com.kiettuan.identity_service.dto.request.UserCreationRequest;
import com.kiettuan.identity_service.dto.request.UserUpdateRequest;
import com.kiettuan.identity_service.entity.User;
import com.kiettuan.identity_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequestMapping("/users")
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    User createUser(@RequestBody UserCreationRequest request){
        return userService.createUser(request);
    }

    // Get all users
    @GetMapping
    List<User> getUsers(){
        return userService.getUsers();
    }

    // Get a user
    @GetMapping("/{userId}")
    User getUser(@PathVariable String userId){
        return userService.getUser(userId);
    }

    @PutMapping("/{userId}")
    User updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request){
        return  userService.updateUser(userId,request);
    }
}
