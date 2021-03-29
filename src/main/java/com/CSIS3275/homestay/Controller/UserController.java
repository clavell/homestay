package com.CSIS3275.homestay.Controller;

import com.CSIS3275.homestay.Entity.User;
import com.CSIS3275.homestay.Repository.UserRepository;
import com.CSIS3275.homestay.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    @Autowired
    private ProductService service;

    @Autowired
    private UserRepository userRepository;

    private Model model;
    private String prodName;
    private String prodDesc;
    private Double prodPrice;
    private String prodImage;

    @GetMapping("/login")
    public String showSignUpForm(User user) {
        return "login";
    }

    @GetMapping("/student-home")
    public String student_home(Model model , @RequestParam String email, @RequestParam String password ) {
        String emailr = email;
        String passwordr = password;
        this.model = model;
//        String id = model.addAttribute("userID", userRepository.findById().get());
        model.addAttribute("users", userRepository.findAll());
        return "index";
    }


}
