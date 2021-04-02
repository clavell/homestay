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
import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationTests {

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
        user.setName("registrationPerson");
        user.setEmail("registrationtest@test.com");
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

        requestParams.add("name", user.getName());
        requestParams.add("password", user.getPassword());
        requestParams.add("password2", user.getPassword());
        requestParams.add("email", user.getEmail());
        requestParams.add("phone", user.getPhone());
        requestParams.add("type", user.getType());
        requestParams.add("description", user.getDescription());
        requestParams.add("nationality", user.getNationality());

    }

    @Test
    void thereisNoRegistrationTestUserInTheDatabaseAfterDelete(){
        User expectedNullUser = userRepository.findByEmail(user.getEmail());
        assertNull(expectedNullUser);
    }

    @Test
    void registeringUserAddsThemToDatabase() throws Exception{

        mockMvc.perform(post("/register", 42L)
                .params(requestParams)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        assertEquals(user.getEmail(), userRepository.findByEmail(user.getEmail()).getEmail());
    }

    @Test
    void registeringUserWithWrongPasswordDoesNotAddThemToDatabase() throws Exception{
        //replace retyped password with incorrect value
        requestParams.remove("password2");
        requestParams.add("password2", "not the password");

        mockMvc.perform(post("/register", 42L)
                .params(requestParams)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        assertNull(userRepository.findByEmail(user.getEmail()));
    }

    @Test
    void registeringUserBringsToPageWithRegistrationSuccessMessage() throws Exception{

        MvcResult result = mockMvc.perform(post("/register", 42L)
                .params(requestParams)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains(registrationSuccessMessage);
    }

    @Test
    void passwordsMustMatchToRegisterUser()throws Exception{
        requestParams.remove("password2");
        requestParams.add("password2", "not the password");
        MvcResult result = mockMvc.perform(post("/register", 42L)
                .params(requestParams)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains(registrationFailedMessage);
    }

    @Test
    void emailMustExistToRegisterInDB() throws Exception {
        List<User> registrationPersons = userRepository.findByName("registrationPerson");
        if(registrationPersons != null)
            for (User person:registrationPersons
                 ) {

            userRepository.delete(person);
            }
        requestParams.remove("email");
        mockMvc.perform(post("/register", 42L)
                .params(requestParams)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        assertTrue(userRepository.findByName("registrationPerson").isEmpty());

    }

    @Test
    void missingEmailReturnsUserToRegistrationPageWithRegistrationFailedMessage()throws Exception{
        requestParams.remove("email");
        MvcResult result = mockMvc.perform(post("/register", 42L)
                .params(requestParams)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains(registrationFailedMessage);
        assertThat(content).contains("<form action=\"/register\"");
    }

    @Test
    void missingStudentOrHostTypeSendsUserBackToRegistrationPage() throws Exception{

        //remove the parameter (missing type)
        requestParams.remove("type");

        MvcResult result = mockMvc.perform(post("/register", 42L)
                .params(requestParams)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains(registrationFailedMessage);
        assertThat(content).contains("<form action=\"/register\"");

        //add the type parameter but with and incorrect value
        requestParams.add("type","wrong value");

        result = mockMvc.perform(post("/register", 42L)
                .params(requestParams)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();

        content = result.getResponse().getContentAsString();
        assertThat(content).contains(registrationFailedMessage);
        assertThat(content).contains("<form action=\"/register\"");

    }

}
