package com.CSIS3275.homestay.Controller;

import com.CSIS3275.homestay.Entity.User;
import com.CSIS3275.homestay.Repository.UserRepository;
//import com.CSIS3275.homestay.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.Console;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    @Value("${login.failed.message}")
    String loginFailedMessage;

    @Value("${registration.failed.message}")
    String registrationFailedMessage;
//    @Autowired
//    private ProductService service;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(Model model, @ModelAttribute("user") User user, @RequestParam String password2) {
        //    public String registerUser(Model model, @ModelAttribute("user") User user,@RequestParam String password, @RequestParam String email, @RequestParam String name ){
        String email = user.getEmail();
        if (user.getPassword2().equals(user.getPassword()) && email != null && (user.getType() == "Admin" || user.getType() == "Student")) {
            userRepository.insert(user);
            return "test";
        }
//        model.addAttribute("registrationFailedMessage", registrationFailedMessage);
        return "register";
    }

    @GetMapping("/newlogin")
    public String showSignInForm(Model model) {
        model.addAttribute("user", new User());
        return "newlogin";
    }

    @GetMapping("/logging_in")
    public String loggingIn(Model model, @ModelAttribute("user") User user) {
        System.out.println("**********************************User email: " + user.getEmail());
        User userFromDB = userRepository.findByEmail(user.getEmail());
        System.out.println("User from DB: " + userFromDB.getEmail());
        if (userFromDB != null && userFromDB.getPassword().equals(user.getPassword())) {
            System.out.println(userFromDB.getType());
            model.addAttribute("user", userFromDB);
            if (userFromDB.getType().equals("Student")) {
                System.out.println(userFromDB);
                return "student_home";
            } else if (userFromDB.getType().equals("Admin")) {
                return "admin_home";
            }

        }
        System.out.println("User Trying to login " + user.getEmail());
        System.out.println("a" + user.getType());
        System.out.println("b" + userFromDB.getType());

        return "newlogin";
    }

//    @GetMapping("/login")
//    public String showSignUpForm(User user) {
//        return "login";
//    }

    @PostMapping("/adduser")
    public String addUser(User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-user";
        }

        userRepository.save(user);
        return "redirect:/index";
    }

    @GetMapping("/student_home")
    public String student_home(Model model, @ModelAttribute("user") User user) {
        model.addAttribute("user", userRepository.findById(user.getId()));
        return "student_home";
    }

    @GetMapping("/student_profile")
    public String student_profile(Model model, @ModelAttribute("user") User user) {
        User userFromDB = userRepository.findByEmail(user.getEmail());

        model.addAttribute("user", user);
        return "student_profile";
    }

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

    @GetMapping("/index")
    public String showUserList(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "index";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") String id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        model.addAttribute("user", user);
        return "update-user";
    }


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

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") String id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        userRepository.delete(user);
        return "redirect:/index";
    }


}
