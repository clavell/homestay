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
    public String showRegistrationForm(Model model){
        model.addAttribute("user", new User());
        return "register";
    }

    @GetMapping("/newlogin")
    public String showSignInForm(Model model){
        model.addAttribute("user", new User());
        return "newlogin";
    }

    @PostMapping("/register")
    public String registerUser(Model model, @ModelAttribute("user") User user,@RequestParam String password2){
        //    public String registerUser(Model model, @ModelAttribute("user") User user,@RequestParam String password, @RequestParam String email, @RequestParam String name ){
        String email = user.getEmail();
        if(user.getPassword2().equals(user.getPassword()) && email != null && (user.getType() == "Host" || user.getType() == "Student")) {
            userRepository.insert(user);
            return "test";
        }
//        model.addAttribute("registrationFailedMessage", registrationFailedMessage);
        return "register";
    }

    @GetMapping("/logging_in")
    public String loggingIn(Model model, @ModelAttribute("user") User user)
    {
        User userFromDB = userRepository.findByEmail(user.getEmail());
        if(userFromDB!=null && userFromDB.getPassword()!= null && userFromDB.getPassword().equals( user.getPassword())) {
            model.addAttribute("user",userFromDB);
           return "test";

        }
        model.addAttribute("message", loginFailedMessage);
        return "newlogin";
    }

    @GetMapping("/login")
    public String showSignUpForm(User user) {
        return "login";
    }

    @PostMapping("/adduser")
    public String addUser(User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-user";
        }

        userRepository.save(user);
        return "redirect:/index";
    }

//    @GetMapping("/student-home")
//    public String student_home(Model model , @RequestParam String email, @RequestParam String password ) {
//        String emailr = email;
//        String passwordr = password;
////        this.model = model;
////        String id = model.addAttribute("userID", userRepository.findById().get());
//        model.addAttribute("users", userRepository.findAll());
//        return "index";
//    }

//    @GetMapping("/student_profile")
//    public String student_profile(Model model , @ModelAttribute("user") User user ) {
////        User userFromDB = userRepository.findByEmail(user.getEmail());
//        System.out.println("Hello Darkness");
//        System.out.println(user.getId());
//        model.addAttribute("user",user);
//        return "student_profile";
//    }

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
