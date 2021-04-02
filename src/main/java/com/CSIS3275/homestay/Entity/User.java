package com.CSIS3275.homestay.Entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
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
@ToString
@Document(collection = "Users")
public class User {
    @Id
    String id;

    String email;
    String password;
    String password2;
    String name;
    String description;
    String nationality;
    String type;
    String phone;

    String jeez;
}
