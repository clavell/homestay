package com.CSIS3275.homestay.Repository;

import com.CSIS3275.homestay.Entity.Listings;
import com.CSIS3275.homestay.Entity.Status;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StatusRepository extends MongoRepository<Status, String> {
    List<Status> findByStudentEmail(String email);

    Status StudentEmailAndListingId(String studentEmail, String listingid);

    List<Status> StudentEmail(String email);

    List<Status> AdminEmailAndListingId(String adminEmail, String listingid);

    Status AdminEmailAndAndStudentEmailAndListingId(String adminEmail, String studentEmail, String listingid);
}
