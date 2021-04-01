package com.CSIS3275.homestay;

import com.CSIS3275.homestay.Entity.User;
import com.CSIS3275.homestay.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.thymeleaf.spring5.expression.Mvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    private User user;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void prepareDB(){
        user = new User();
        user.setPassword("asdf");
        user.setName("registrationPerson");
        user.setEmail("registrationtest@test.com");
        if(userRepository.findByEmail(user.getEmail()) != null)
            userRepository.delete(user);
    }

    @Test
    void registeringUserAddsThemToDatabase() throws Exception{

        mockMvc.perform(post("/register", 42L)
                .param("email", user.getEmail())
                .param("password", user.getPassword())
                .param("name", user.getName())
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

         assertTrue(userRepository.findByEmail(user.getEmail()).equals(user));
    }

    @Test
    void registeringUserBringsToProfilePage() throws Exception{

        MvcResult result = mockMvc.perform(post("/register", 42L)
                .param("email", user.getEmail())
                .param("password", user.getPassword())
                .param("name", user.getName())
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("registrationPerson");
    }
}
