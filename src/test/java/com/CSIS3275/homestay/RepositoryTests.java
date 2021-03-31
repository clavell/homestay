package com.CSIS3275.homestay;

//import com.CSIS3275.homestay.Configuration.MongoConfig;
import com.CSIS3275.homestay.Entity.User;
import com.CSIS3275.homestay.Repository.UserRepository;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
//@ContextConfiguration(classes = {MongoConfig.class})
public class RepositoryTests {
    @Autowired
    UserRepository userRepository;

    @Test
    void insertInsertsAUser(){
        User user = new User();
        user.setEmail("me2@me2.com");
        user.setName("me2");

        if(userRepository.findByEmail(user.getEmail()) != null)
            userRepository.delete(user);
        userRepository.insert(user);
        User DBUser = userRepository.findByEmail(user.getEmail());


        assertTrue(user.equals(DBUser));
        userRepository.delete(user);
    }

    @Test
    void nullifyingAPasswordFieldRemovesItFromTheRepository(){
        //go and check this in the database yourself..
        User user = new User();
        user.setPassword("asdf");
        user.setEmail("me2@me2.com");
        user.setName("me2");

        if(userRepository.findByEmail(user.getEmail()) != null)
            userRepository.delete(user);
        userRepository.insert(user);
        user.setPassword(null);
        userRepository.save(user);

    }

}
