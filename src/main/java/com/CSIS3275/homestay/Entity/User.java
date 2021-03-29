package com.CSIS3275.homestay.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Table(name = "USER_TABLE")
public class User {

    @Id
    @GeneratedValue
    private long id;

    private String name;
    private String email;
}
