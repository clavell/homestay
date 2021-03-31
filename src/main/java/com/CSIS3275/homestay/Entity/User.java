package com.CSIS3275.homestay.Entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Component
@EqualsAndHashCode
@Document(collection = "User")
public class User {

    @Id
    long id;
    String password;
    String name;
    String email;
    String description;
    String nationality;
    String type;
    String phone;

}
