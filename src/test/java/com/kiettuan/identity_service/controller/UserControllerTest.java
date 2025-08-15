package com.kiettuan.identity_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kiettuan.identity_service.dto.request.UserCreationRequest;
import com.kiettuan.identity_service.dto.response.UserResponse;
import com.kiettuan.identity_service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private UserCreationRequest request;
    private UserResponse userResponse;
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
    }

    @Test
    // Test with correct data
    void createUser_validRequest_success() throws Exception {
        // GIVEN
        ObjectMapper objectmapper = new ObjectMapper();
        objectmapper.registerModule(new JavaTimeModule()); // register for `jackson` package identify LocalDate type

        String content = objectmapper.writeValueAsString(request); // convert object to string

        // Mock method call service filter
        Mockito.when(userService.createUser(ArgumentMatchers.any()))
                .thenReturn(userResponse);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                    .post("/users") // method
                    .contentType(MediaType.APPLICATION_JSON_VALUE) // data type
                    .content(content)) // response
                .andExpect(MockMvcResultMatchers.status().isOk()) // status
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000)); // code


    }

}
