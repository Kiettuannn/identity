package com.kiettuan.identity_service.service;

import com.kiettuan.identity_service.dto.request.UserCreationRequest;
import com.kiettuan.identity_service.dto.response.UserResponse;
import com.kiettuan.identity_service.entity.User;
import com.kiettuan.identity_service.exception.AppException;
import com.kiettuan.identity_service.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
@SpringBootTest
@TestPropertySource("/test.properties")

public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    private UserCreationRequest request;
    private UserResponse userResponse;
    private User user;
    private LocalDate dob;

    @BeforeEach
    void initData(){
        dob = LocalDate.of(2005,1,25);
        request = UserCreationRequest.builder()
                .username("Kiettuan")
                .password("123456789")
                .firstname("Kiet")
                .lastname("Tuan")
                .dob(dob)
                .build();

        userResponse = UserResponse.builder()
                .id("hahahaha")
                .username("Kiettuan")
                .firstname("Kiet")
                .lastname("Tuan")
                .dob(dob)
                .build();

        user = User.builder()
                .id("hahahaha")
                .username("Kiettuan")
                .firstname("Kiet")
                .lastname("Tuan")
                .dob(dob)
                .build();
    }

    @Test
    void createUser_validRequest_success(){
        // GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        // WHEN
        var response = userService.createUser(request);

        // THEN
        assertThat(response.getId()).isEqualTo("hahahaha");
        assertThat(response.getUsername()).isEqualTo("Kiettuan");
    }

    @Test
    void createUser_userExisted_fail(){
        // GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // WHEN
        var exception = assertThrows(AppException.class,
                () -> userService.createUser(request));

        // THEN
        assertThat(exception.getErrorCode().getCode()).isEqualTo(1001);
    }

    @Test
    @WithMockUser(username = "Kiettuan")
    void getMyInfo_valid_success(){
        // GIVEN
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        // WHEN
        var response = userService.getMyInfo();

        // THEN
        assertThat(response.getUsername()).isEqualTo("Kiettuan");
        assertThat(response.getId()).isEqualTo("hahahaha");
    }

    @Test
    @WithMockUser(username = "Kiettuan")
    void getMyInfo_userNotFound_fail(){
        // GIVEN
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(null));

        // WHEN
        var exception = assertThrows(AppException.class,
                () -> userService.getMyInfo());

        // THEN
        assertThat(exception.getErrorCode().getCode()).isEqualTo(1002);
    }

}
