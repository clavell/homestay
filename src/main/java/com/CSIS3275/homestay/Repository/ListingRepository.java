package com.CSIS3275.homestay.Repository;

import com.CSIS3275.homestay.Entity.Listings;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingRepository extends MongoRepository<Listings, String> {

    List<Listings> AdminEmailId(String email);
}
