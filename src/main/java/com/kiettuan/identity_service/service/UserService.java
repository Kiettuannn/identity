package com.kiettuan.identity_service.service;

import com.kiettuan.identity_service.dto.request.UserCreationRequest;
import com.kiettuan.identity_service.dto.request.UserUpdateRequest;
import com.kiettuan.identity_service.entity.User;
import com.kiettuan.identity_service.exception.AppException;
import com.kiettuan.identity_service.exception.ErrorCode;
import com.kiettuan.identity_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User createUser(UserCreationRequest request){
        User user = new User();
        if(userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

//        UserCreationRequest request1 = UserCreationRequest.builder()
//                .username("")
//                .firstname("")
//                .build();


        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setDob(request.getDob());

        return userRepository.save(user);
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public  User getUser(String id){
        return  userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public User updateUser(String userId, UserUpdateRequest request){
        User user = getUser(userId);
        user.setPassword(request.getPassword());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setDob(request.getDob());

        return userRepository.save(user);
    }

    public void deleteUser(String userId){
        userRepository.deleteById(userId);
    }
}
