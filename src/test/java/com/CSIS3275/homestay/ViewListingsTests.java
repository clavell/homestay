package com.CSIS3275.homestay;

import com.CSIS3275.homestay.Entity.Listings;
import com.CSIS3275.homestay.Entity.User;
import com.CSIS3275.homestay.Repository.ListingRepository;
import com.CSIS3275.homestay.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ViewListingsTests {
    @Value("${login.success.message}")
    String loginSuccessMessage;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;


    private User admin;

    private LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();

    @Value("${listings.test.description}")
    String listingsTestDescription;

    private Listings listing;

    private int NO_OF_LISTINGS = 2;

    @BeforeEach
    void CreateTheBasicUserAndUploadToDB(){

        //add administrator to db
        admin = new User();
        admin.setPassword("asdf");
        admin.setName("AdminPerson");
        admin.setEmail("admintest@test.com");
        admin.setPhone("12345678");
        admin.setType("Admin");
        admin.setDescription("Have a place to rent");
        admin.setNationality("American");

//        remove all test admins from db
        List<User> dbUsers = userRepository.findByName(admin.getName());
        if(dbUsers != null)
            for (User dbUser: dbUsers
            ) {
                userRepository.delete(dbUser);
            }
        userRepository.insert(admin);

        //create a new listing
        listing = new Listings();
        listing.setAddress("123 fake st.");
        listing.setDuration("1");
        listing.setPrice("22");
        listing.setDescription(listingsTestDescription);
        listing.setStart_from("today");
        //make sure the admin's id is entered correctly so make sure to get from db
        listing.setAdminEmailId(userRepository.findByEmail(admin.getEmail()).getEmail());

        //remove test listings
        List<Listings> dbListings = listingRepository.findByDescription(listing.getDescription());
        if(dbUsers != null)
            for (Listings dbListing: dbListings
            ) {
                listingRepository.delete(dbListing);
            }
//        for(int i=0;i<NO_OF_LISTINGS;i++)
            listingRepository.insert(listing);

        //add the listing to the parameters
//        requestParams.add("address", listing.getAddress());
//        requestParams.add("duration", listing.getDuration());
//        requestParams.add("price", listing.getPrice());
//        requestParams.add("description", listing.getDescription());
//        requestParams.add("start_from", listing.getStart_from());
//        requestParams.add("adminEmailId", listing.getAdminEmailId());


    }

    @Test
    void goingToTheAdminHomePageShowsListingsThatAreAssociatedWithTheLoggedInAdminUser() throws Exception{
        MvcResult result = mockMvc.perform(get("/admin_home", 42L)
                .flashAttr("user",admin)
                .content(objectMapper.writeValueAsString(admin)))
                .andExpect(status().isOk()).andReturn();

        List<Listings> listings = (List<Listings>) result.getModelAndView().getModel().get("listings");
//        assertEquals(listings.size(),NO_OF_LISTINGS);
//        assertEmpty(listings);
    }

}
