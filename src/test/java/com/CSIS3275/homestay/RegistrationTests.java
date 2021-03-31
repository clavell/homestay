package com.CSIS3275.homestay;

import com.CSIS3275.homestay.Entity.User;
import org.junit.jupiter.api.Test;

public class RegistrationTests {

    @Test
    void registeringUserAddsThemToDatabase() {
        User user = new User();
        user.setId(100);
        user.setPassword("asdf");
        user.setName("me");
        user.setEmail("me@me.com");
        if(userRepository.findById(user.getId()) != null)
            userRepository.delete(user);
        userRepository.insert(user);
    }
}
