package com.legaltech;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The only purpose of this controller is to redirect user from "http://localhost:8080" to "http://localhost:8080/admin.html"
 */
@Controller
@RequestMapping("/")
public class RedirectToAdminPageController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String sendRedirect() {
        return "redirect:/admin.html";
    }

}
