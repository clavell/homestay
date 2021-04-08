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
@Document(collection = "Status")
public class Status {
    @Id
    String id;

    String status;
    String listingId;
    String listingAddress;
    String adminId;
    String adminEmail;
    String studentId;
    String studentEmail;
    String studentName;
}
