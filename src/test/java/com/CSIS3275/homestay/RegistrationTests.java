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
        user.setPassword2("asdf");
        user.setName("registrationPerson");
        user.setEmail("registrationtest@test.com");
        if(userRepository.findByEmail(user.getEmail()) != null)
            userRepository.delete(user);
    }

    @Test
    void thereisNoRegistrationTestUserInTheDatabaseAfterDelete(){
        User expectedNullUser = userRepository.findByEmail(user.getEmail());
        assertNull(expectedNullUser);
    }

    @Test
    void registeringUserAddsThemToDatabase() throws Exception{

        mockMvc.perform(post("/register", 42L)
                .param("email", user.getEmail())
                .param("password", user.getPassword())
                .param("password2", user.getPassword2())
                .param("name", user.getName())
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

         assertTrue(userRepository.findByEmail(user.getEmail()).equals(user));
    }

    @Test
    void registeringUserWithWrongPasswordDoesNotAddThemToDatabase() throws Exception{

        mockMvc.perform(post("/register", 42L)
                .param("email", user.getEmail())
                .param("password", user.getPassword())
                .param("password2", "not the password")
                .param("name", user.getName())
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        assertNull(userRepository.findByEmail(user.getEmail()));
    }

    @Test
    void registeringUserBringsToProfilePage() throws Exception{

        MvcResult result = mockMvc.perform(post("/register", 42L)
                .param("email", user.getEmail())
                .param("password", user.getPassword())
                .param("password2", user.getPassword2())
                .param("name", user.getName())
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("registrationPerson");
        assertThat(content).contains("Profile");
    }

    @Test
    void passwordsMustMatchToRegisterUser()throws Exception{

        MvcResult result = mockMvc.perform(post("/register", 42L)
                .param("email", user.getEmail())
                .param("password", user.getPassword())
                .param("password2", "not the password")
                .param("name", user.getName())
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("registrationPerson");
        assertThat(content).contains("<form action=\"/register\"");
    }
}
