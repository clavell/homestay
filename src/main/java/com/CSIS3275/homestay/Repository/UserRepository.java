package com.CSIS3275.homestay.Repository;

import com.CSIS3275.homestay.Entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    List<User> findByName(String name);
    User findByEmail(String email);

}
