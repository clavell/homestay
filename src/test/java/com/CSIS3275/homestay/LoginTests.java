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
import org.springframework.util.LinkedMultiValueMap;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class LoginTests {

    @Value("${login.failed.message}")
    String loginFailedMessage;

    @Value("${login.success.message}")
    String loginSuccessMessage;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private User user2;

    private LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();

    @BeforeEach
    void CreateTheBasicUserAndUploadToDB(){
        user = new User();
        user.setPassword("asdf");
        user.setName("me");
        user.setEmail("me@me.com");
        user.setType("Student");
        User dbUser = userRepository.findByEmail(user.getEmail());
        if(dbUser != null)
            userRepository.delete(dbUser);
        userRepository.insert(user);

        requestParams.add("name", user.getName());
        requestParams.add("password", user.getPassword());
        requestParams.add("email", user.getEmail());
        requestParams.add("type", user.getType());


//        user2 = new User();
//        user2.setPassword("Sabby");
//        user2.setName("Sarbjeet");
//        user2.setEmail("kaursarb@gmail.com");
//        user2.setType("Admin");
//        user2.setDescription("Testing for Admin");
//        User dbUser2 = userRepository.findByEmail(user2.getEmail());
//        if(dbUser != null)
//            userRepository.delete(dbUser);
//        userRepository.insert(user);
//
//        requestParams.add("name", user.getName());
//        requestParams.add("password", user.getPassword());
//        requestParams.add("email", user.getEmail());
//        requestParams.add("type", user.getType());
//        requestParams.add("description", user.getDescription());

//        Creating a test for the admin, might be good, like where is the page going to redirect if we have saved
//        the value as "admin" or "ADMIN" instead of "Admin"

    }

    @Test
    void LoginWorksAndShowsLoginSuccesMessage() throws Exception {
        MvcResult result = mockMvc.perform(get("/logging_in", 42L)
                .params(requestParams)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();

        assertThat(content).contains(loginSuccessMessage);

    }

    @Test
    void failedLoginShowsLoginFailedMessage() throws Exception {
        user.setEmail("NeverEnterThisEmailIntoTheDB@Ever.com");
        requestParams.remove("email");
        requestParams.add("email",user.getEmail());
        MvcResult result = mockMvc.perform(get("/logging_in", 42L)
                .params(requestParams)
//                .contentType("application/json")
//                .param("email", user.getEmail())
//                .param("password", user.getPassword())
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
    void ifFieldMissingLoginStillWorksAndShowsLoginMessage() throws Exception {
        MvcResult result = mockMvc.perform(get("/logging_in", 42L)
//                .contentType("application/json")
                .params(requestParams)
//                .param("email", user.getEmail())
//                .param("password", user.getPassword())
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
//      just make sure that the page loads
        assertThat(content).contains(loginSuccessMessage);

    }

    @Test
    void nonexistentPasswordInDBDoesNotCrashProgram() throws Exception {
//        remove password from the user
        User dbUser = userRepository.findByEmail(user.getEmail());
        dbUser.setPassword(null);
        userRepository.save(dbUser);


        MvcResult result = mockMvc.perform(get("/logging_in", 42L)
//                .contentType("application/json")
                .params(requestParams)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
//        just make sure that the page loads
        assertThat(content).contains(loginFailedMessage);

    }


}

