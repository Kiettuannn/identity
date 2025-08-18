package com.kiettuan.identity_service.controller;

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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class UserControllerIntegrationTest {
    @Container
    static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:8.0.43");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
        registry.add("spring.datasource.driverClassName", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    private MockMvc mockMvc;

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

    }

    // Test with correct data
    @Test
    void createUser_validRequest_success() throws Exception {
        // GIVEN
        ObjectMapper objectmapper = new ObjectMapper();
        objectmapper.registerModule(new JavaTimeModule()); // register for `jackson` package identify LocalDate type

        String content = objectmapper.writeValueAsString(request); // convert object to string


        // WHEN, THEN
        var response = mockMvc.perform(MockMvcRequestBuilders
                        .post("/users") // method
                        .contentType(MediaType.APPLICATION_JSON_VALUE) // data type
                        .content(content)) // response
                .andExpect(MockMvcResultMatchers.status().isOk()) // status
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000)) // code
                .andExpect(MockMvcResultMatchers.jsonPath("result.username").value("Kiettuan"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.firstname").value("Kiet"))
                .andExpect(MockMvcResultMatchers.jsonPath("result.lastname").value("Tuan"));

        log.info("Result: {}",response.andReturn().getResponse().getContentAsString());

    }


}
