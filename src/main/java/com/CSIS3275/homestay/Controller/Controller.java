package com.CSIS3275.homestay.Controller;

import com.CSIS3275.homestay.Entity.Listings;
import com.CSIS3275.homestay.Entity.Status;
import com.CSIS3275.homestay.Entity.User;
import com.CSIS3275.homestay.Repository.ListingRepository;
import com.CSIS3275.homestay.Repository.StatusRepository;
import com.CSIS3275.homestay.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@org.springframework.stereotype.Controller
public class Controller {

    private User user;

    @Value("${login.failed.message}")
    String loginFailedMessage;

    @Value("${registration.failed.message}")
    String registrationFailedMessage;

    @Value("${registration.success.message}")
    String registrationSuccessMessage;

    @Value("${login.success.message}")
    String loginSuccessMessage;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ListingRepository listingRepository;

    @Autowired
    StatusRepository statusRepository;

    //Creating a New User Getting and Posting

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("toRegister", new User());
        model.addAttribute("password2", new String());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(Model model, @ModelAttribute("toRegister") User toRegister, @ModelAttribute("password2") String password2) {
        //    public String registerUser(Model model, @ModelAttribute("toRegister") User toRegister,@RequestParam String password, @RequestParam String email, @RequestParam String name ){
        String email = toRegister.getEmail();
        User existingUser = userRepository.findByEmail(email);

        boolean canRegister = password2.equals(toRegister.getPassword()) && email != null &&
                (toRegister.getType() == "Admin" || toRegister.getType() == "Student") &&
                existingUser == null;

        if (canRegister) {
            userRepository.insert(toRegister);
            model.addAttribute("message",registrationSuccessMessage);
            model.addAttribute("toLogin",toRegister);
            return "newlogin";
        }
        model.addAttribute("message", registrationFailedMessage);
        return "register";
    }

    @PostMapping("/adduser")
    public String addUser(User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-user";
        }

        userRepository.save(user);
        return "redirect:/index";
    }

    // Logging in Screen
    @GetMapping("/newlogin")
    public String showSignInForm(Model model) {
        model.addAttribute("toLogin", new User());
        return "newlogin";
    }

    //Once the user has logged in the Get Mapping to decide which kind of user it is

    @GetMapping("/logging_in")
    public String loggingIn(Model model, @ModelAttribute("toLogin") User toLogin) {
        User userFromDB = userRepository.findByEmail(toLogin.getEmail());
        if (userFromDB != null && userFromDB.getPassword() != null && userFromDB.getPassword().equals(toLogin.getPassword())) {
            System.out.println(userFromDB.getType());
            model.addAttribute("user", userFromDB);
            model.addAttribute("message", loginSuccessMessage);
            if (userFromDB.getType().equals("Student")) {
                model.addAttribute("listings", listingRepository.findAll());
                return "student_home";
            } else if (userFromDB.getType().equals("Admin")) {
                model.addAttribute("listings", listingRepository.AdminEmailId(userFromDB.getEmail()));
                return "admin_home";
            }
        }
        //If the toLogin doesn't exist it won't log in
        model.addAttribute("message", loginFailedMessage);
        return "newlogin";
    }


    //Get Mapping for Student Home which is called when a Student Logs in
    //Adding the listing element too which will display all the listings.
    //We will limit the view of the list using HTML CSS don't worry
    @GetMapping("/student_home")
    public String student_home(Model model, @ModelAttribute("user") User user) {
        model.addAttribute("user", userRepository.findByEmail(user.getEmail()));
        model.addAttribute("listings", listingRepository.findAll());
        System.out.println(listingRepository.findAll());
        System.out.println(user);
        return "student_home";
    }

    // If a student wants to make changes to his/her profile this mapping is called
    @GetMapping("/student_profile")
    public String student_profile(Model model, @ModelAttribute("user") User user) {
        model.addAttribute("user", user);
        return "student_profile";
    }

    //Whenever he/she posts an update for there details an update is called (aka post mapping)
    @PostMapping("/student_profile")
    public String student_profile_update(Model model, @ModelAttribute("user") User user) {
        User userFromDB = userRepository.findById(user.getId()).get();


        if (user.getPassword().equals(userFromDB.getPassword())) {
            userFromDB.setName(user.getName());
            userFromDB.setEmail(user.getEmail());
            userFromDB.setNationality(user.getNationality());
            userFromDB.setDescription(user.getDescription());
            userFromDB.setPhone(user.getPhone());
            userRepository.deleteById(userFromDB.getId());
            userRepository.insert(userFromDB);
            model.addAttribute("user", userFromDB);
            return "student_profile";
        } else {
            model.addAttribute("user", userFromDB);
            return "student_profile";
        }
    }

    @GetMapping("/request_student")
    public String requestStudent(Model model, @RequestParam(required = false) String listingid, @RequestParam String userid) {
        User student = userRepository.findById(userid).orElse(null);
        Listings listings = listingRepository.findById(listingid).orElse(null);
        if (listings != null) {
            User admin = userRepository.findByEmail(listings.getAdminEmailId());
            Status s = new Status();
            s.setAdminEmail(admin.getEmail());
            s.setAdminId(admin.getId());
            s.setStudentEmail(student.getEmail());
            s.setStudentId(student.getId());
            s.setListingId(listingid);
            s.setListingAddress(listings.getAddress());
            s.setStatus("To Be Decided");
            if (statusRepository.StudentEmailAndListingId(student.getEmail(), listingid) == null) {
                statusRepository.insert(s);
                System.out.println("Status Inserted");
            } else {
                System.out.println("Status Already Exists");
            }
        }
        model.addAttribute("user", student);
        model.addAttribute("status", statusRepository.StudentEmail(student.getEmail()));
        return "request_student";
    }

    @GetMapping("/admin_home")
    public String admin_home(Model model, @ModelAttribute("user") User user) {
        model.addAttribute("user", userRepository.findByEmail(user.getEmail()));
        model.addAttribute("listings", listingRepository.AdminEmailId(user.getEmail()));
        return "admin_home";
    }


    @PostMapping("/add_listing")
    public String addListing(Model model, @ModelAttribute("listing") Listings listing, @ModelAttribute("user") User user) {
//        Listings listings = listingRepository.findById(listingid).orElse(null);
        model.addAttribute("user",userRepository.findByEmail(user.getEmail()));
        listingRepository.insert(listing);
        model.addAttribute("listings", listingRepository.AdminEmailId(user.getEmail()));
        return "admin_home";
    }

    @GetMapping("/edit_listing")
    public String editListing(Model model, @RequestParam(required = false) String listingid, @RequestParam String userid) {
        Listings listings = listingRepository.findById(listingid).orElse(null);
        model.addAttribute("user",userRepository.findById(userid));
        model.addAttribute("listing", listings);
        return "edit_listing_admin";
    }

    @PostMapping("/edit_listing")
    public String updateListing(Model model, @ModelAttribute("listing") Listings listing) {
        System.out.println(listing);
        Listings listingsFromDB = listingRepository.findById(listing.getId()).orElse(null);
        listingsFromDB.setAddress(listing.getAddress());
        listingsFromDB.setDuration(listing.getDuration());
        listingsFromDB.setAdminEmailId(listingsFromDB.getAdminEmailId());
        listingsFromDB.setStart_from(listing.getStart_from());
        listingsFromDB.setPrice(listing.getPrice());
        listingsFromDB.setDescription(listing.getDescription());
        listingRepository.deleteById(listing.getId());
        listingRepository.insert(listingsFromDB);
        model.addAttribute("listing", listingsFromDB);
        model.addAttribute("user",userRepository.findByEmail(listing.getAdminEmailId()));
        return "edit_listing_admin";
    }

    @GetMapping("/request_status")
    public String requestStatus(Model model, @RequestParam(required = false) String listingid, @RequestParam String userid) {
        Listings listings = listingRepository.findById(listingid).orElse(null);
        User admin = userRepository.findById(userid).orElse(null);
        List<Status> statuses = statusRepository.AdminEmailAndListingId(admin.getEmail(), listingid);
        model.addAttribute("user",admin);
        model.addAttribute("listing", listings);
        model.addAttribute("statuses",statuses);
        System.out.println(listings);
        System.out.println(admin);
        System.out.println(statuses);
        return "requests_admin";
    }


}
