package com.CSIS3275.homestay;

import com.CSIS3275.homestay.Entity.User;
import com.CSIS3275.homestay.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.liquibase.enabled=false",
        "spring.flyway.enabled=false"
})
@AutoConfigureMockMvc
public class LoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void LoginWorks() throws Exception {
        User user = new User();
        user.setId(99);
        user.setPassword("asdf");
        user.setName("me");
        user.setEmail("me@me.com");

        MvcResult result = mockMvc.perform(get("/logging_in", 42L)
//                .contentType("application/json")
                .param("email", "me@me.com")
                .param("password", "asdf")
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();

        assertThat(content).contains("me@me.com");
        User userEntity = userRepository.findByEmail(user.getEmail());
        assertThat(userEntity.getEmail()).isEqualTo(user.getEmail());
    }

}

