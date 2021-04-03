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
import org.springframework.web.bind.annotation.*;

import java.util.List;


@org.springframework.stereotype.Controller
public class Controller {

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
        model.addAttribute("user", new User());
        model.addAttribute("password2", new String());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(Model model, @ModelAttribute("user") User user, @ModelAttribute("password2") String password2) {
        //    public String registerUser(Model model, @ModelAttribute("user") User user,@RequestParam String password, @RequestParam String email, @RequestParam String name ){
        String email = user.getEmail();
        User existingUser = userRepository.findByEmail(email);

        boolean canRegister = password2.equals(user.getPassword()) && email != null &&
                (user.getType() == "Admin" || user.getType() == "Student") &&
                existingUser == null;

        if (canRegister) {
            userRepository.insert(user);
            model.addAttribute("message",registrationSuccessMessage);
            return "newlogin";
        }
        model.addAttribute("message",registrationFailedMessage);
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
        model.addAttribute("user", new User());
        return "newlogin";
    }

    //Once the user has logged in the Get Mapping to decide which kind of user it is
    @GetMapping("/logging_in")
    public String loggingIn(Model model, @ModelAttribute("user") User user) {
        User userFromDB = userRepository.findByEmail(user.getEmail());
        if (userFromDB != null && userFromDB.getPassword() != null && userFromDB.getPassword().equals(user.getPassword())) {
            System.out.println(userFromDB.getType());
            model.addAttribute("user", userFromDB);
            model.addAttribute("listings",listingRepository.findAll());
            model.addAttribute("message", loginSuccessMessage);

            if (userFromDB.getType().equals("Student")) {
                System.out.println(userFromDB);
                return "student_home";
            } else if (userFromDB.getType().equals("Admin")) {
                return "admin_home";
            }
        }
        //If the user doesn't exist it won't log in
        model.addAttribute("message", loginFailedMessage);
        return "newlogin";
    }


    //Get Mapping for Student Home which is called when a Student Logs in
    //Adding the listing element too which will display all the listings.
    //We will limit the view of the list using HTML CSS don't worry
    @GetMapping("/student_home")
    public String student_home(Model model, @ModelAttribute("user") User user) {
        System.out.println("Fuck!");
        model.addAttribute("user", userRepository.findById(user.getId()));
        model.addAttribute("listings",listingRepository.findAll());
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
        User userFromDB = userRepository.findById(user.getId()).orElse(userRepository.findByEmail(user.getEmail()));
        if (user.getPassword().equals(userFromDB.getPassword())) {
            userFromDB.setName(user.getName());
            userFromDB.setEmail(user.getEmail());
            userFromDB.setNationality(user.getNationality());
            userFromDB.setDescription(user.getDescription());
            userFromDB.setPhone(user.getPhone());
            userRepository.deleteById(user.getId());
            userRepository.insert(userFromDB);
            model.addAttribute("user", userFromDB);
            return "student_profile";
        }
        else{
            model.addAttribute("user",userFromDB);
            return "student_profile";
        }
    }

    @GetMapping("/request_student")
    public String requestStudent(Model model,@RequestParam String listingid , @RequestParam String userid) {
        System.out.println(listingid);
        System.out.println(userid);
        User student = userRepository.findById(userid).orElse(null);
        Listings listings = listingRepository.findById(listingid).orElse(null);
        User admin = userRepository.findByEmail(listings.getAdminEmailId());
        Status s = new Status();
        s.setAdminEmail(admin.getEmail());
        s.setAdminId(admin.getId());
        s.setStudentEmail(student.getEmail());
        s.setStudentId(student.getId());
        s.setListingId(listingid);
        s.setStatus("To Be Decided");
        if(statusRepository.StudentEmailAndListingId(student.getEmail(),listingid) == null) {
            statusRepository.insert(s);
            System.out.println("Status Inserted");
        }
        else{
            System.out.println("Status Already Exists");
        }
        model.addAttribute("user",student);
        model.addAttribute("status", statusRepository.findByStudentEmail(student.getEmail()));
        return "test";
    }


//    @GetMapping("/edit/{id}")
//    public String showUpdateForm(@PathVariable("id") String id, Model model) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
//
//        model.addAttribute("user", user);
//        return "update-user";
//    }


//    @PostMapping("/update/{id}")
//    public String updateUser(@PathVariable("id") String id, User user,
//                             BindingResult result, Model model) {
//        if (result.hasErrors()) {
//            user.setId(id);
//            return "update-user";
//        }
//
//        userRepository.save(user);
//        return "redirect:/index";
//    }

//    @GetMapping("/delete/{id}")
//    public String deleteUser(@PathVariable("id") String id, Model model) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
//        userRepository.delete(user);
//        return "redirect:/index";
//    }


}
