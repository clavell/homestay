package com.CSIS3275.homestay;

import com.CSIS3275.homestay.Entity.User;
import com.CSIS3275.homestay.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProfileTests {

    @Value("${registration.failed.message}")
    String registrationFailedMessage;

    @Value("${registration.success.message}")
    String registrationSuccessMessage;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    private User user;

    @Autowired
    private ObjectMapper objectMapper;

    private LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();

    @BeforeEach
    void prepareDB(){
        user = new User();
        user.setPassword("asdf");
        user.setName("profilePerson");
        user.setEmail("profiletest@test.com");
        user.setPhone("12345678");
        user.setType("Student");
        user.setDescription("Looking for a place to stay");
        user.setNationality("American");

        List<User> dbUsers = userRepository.findByName(user.getName());
        if(dbUsers != null)
            for (User dbUser: dbUsers
            ) {
                userRepository.delete(dbUser);
            }

        userRepository.insert(user);
        user = userRepository.findByEmail(user.getEmail());

        requestParams.add("id",user.getId());
        requestParams.add("name", user.getName());
        requestParams.add("password", user.getPassword());
        requestParams.add("email", user.getEmail());
        requestParams.add("phone", user.getPhone());
        requestParams.add("type", user.getType());
        requestParams.add("description", user.getDescription());
        requestParams.add("nationality", user.getNationality());

    }


    @Test
    void updatingProfileForUserUpdatesTheUserInTheModel() throws Exception{
        requestParams.remove("phone");
        String new_phone = "91827398217398127";
        requestParams.add("phone", new_phone);

        //test for student
        MvcResult result = mockMvc.perform(post("/student_profile", 42L)
                .params(requestParams)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andReturn();

        User user = (User) result.getModelAndView().getModel().get("user");

        assertTrue(user.getPhone() == new_phone);

    }

    @Test
    void updatingProfileForUserWithIncorrectPWDoesNotUpdateTheUserInTheModel() throws Exception{
        requestParams.remove("phone");
        String new_phone = "91827398217398127";
        requestParams.add("phone", new_phone);

        requestParams.remove("password");
        requestParams.add("password","23432");

        //test for student
        MvcResult result = mockMvc.perform(post("/student_profile", 42L)
                .params(requestParams)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andReturn();

        User user = (User) result.getModelAndView().getModel().get("user");

        assertTrue(user.getPhone() == user.getPhone());

    }


}
