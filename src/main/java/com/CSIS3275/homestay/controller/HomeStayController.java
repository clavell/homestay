package com.CSIS3275.homestay.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeStayController {

        @RequestMapping("/")
        String getJokes(Model model){


            model.addAttribute("joke","Chicken");
            return "index";
        }

}
