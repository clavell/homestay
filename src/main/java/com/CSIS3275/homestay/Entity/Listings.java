package com.CSIS3275.homestay.Entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Component
@EqualsAndHashCode
@ToString
@Document(collection = "Listings")
public class Listings {
    @Id
    String id;

    String address;
    String duration;
    String price;
    String description;
    String start_from;
    String adminEmailId;
}
