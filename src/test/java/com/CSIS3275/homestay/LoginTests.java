package com.CSIS3275.homestay;

import com.CSIS3275.homestay.Entity.User;
import com.CSIS3275.homestay.Repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(
//        properties = {
//        "spring.jpa.hibernate.ddl-auto=create-drop",
//        "spring.liquibase.enabled=false",
//        "spring.flyway.enabled=false"
//}
)
@AutoConfigureMockMvc
public class LoginTests {

    @Value("${login.failed.message}")
    String loginFailedMessage;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void CreateTheBasicUserAndUploadToDB(){
        user = new User();
        user.setPassword("asdf");
        user.setName("me");
        user.setEmail("me@me.com");
        if(userRepository.findByEmail(user.getEmail()) != null)
            userRepository.delete(user);
        userRepository.insert(user);
    }

    @Test
    void LoginWorks() throws Exception {
        MvcResult result = mockMvc.perform(get("/logging_in", 42L)
//                .contentType("application/json")
                .param("email", "me@me.com")
                .param("password", "asdf")
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();

        assertThat(content).contains("me@me.com");

    }

    @Test
    void failedLoginShowsLoginFailedMessage() throws Exception {
        user.setEmail("NeverEnterThisEmailIntoTheDB@Ever.com");
        MvcResult result = mockMvc.perform(get("/logging_in", 42L)
//                .contentType("application/json")
                .param("email", user.getEmail())
                .param("password", user.getPassword())
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();

        assertThat(content).contains(loginFailedMessage);

        user.setEmail("me@me.com");
        user.setPassword("wrong password");
         result = mockMvc.perform(get("/logging_in", 42L)
//                .contentType("application/json")
                .param("email", user.getEmail())
                .param("password", user.getPassword())
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();
         content = result.getResponse().getContentAsString();

        assertThat(content).contains(loginFailedMessage);

    }

    @Test
    void ifFieldMissingLoginStillWorks() throws Exception {
        MvcResult result = mockMvc.perform(get("/logging_in", 42L)
//                .contentType("application/json")
                .param("email", user.getEmail())
                .param("password", user.getPassword())
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
//just make sure that the page loads
        assertThat(content).contains("Student Profile");

    }

    @Test
    void nonexistentPasswordInDBDoesNotCrashProgram() throws Exception {
        //remove password from the user
        user.setPassword(null);
        userRepository.save(user);

        //set password to something again
        user.setPassword("asdf");
        MvcResult result = mockMvc.perform(get("/logging_in", 42L)
//                .contentType("application/json")
                .param("email", user.getEmail())
                .param("password", user.getPassword())
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
//just make sure that the page loads
        assertThat(content).contains(loginFailedMessage);

    }


}

