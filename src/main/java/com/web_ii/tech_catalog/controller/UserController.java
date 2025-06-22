package com.web_ii.tech_catalog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.web_ii.tech_catalog.models.User;
import com.web_ii.tech_catalog.service.IUserService;
import java.util.Arrays;

@Controller
public class UserController {

    @Autowired
    private IUserService userService;

    // Go to Registration Page
    @GetMapping("/register")
    public String register() {
        return "user/registerUser";
    }

    @GetMapping("/admin/register")
    public String adminRegister() {
        return "user/registerAdmin";
    }

    // Read Form data to save into DB
    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute User user, Model model) {
        // Define automaticamente o role como USER
        user.setRoles(Arrays.asList("user"));
        
        Integer id = userService.saveUser(user);
        String message = "Usuário '" + user.getName() + "' registrado com sucesso!";
        model.addAttribute("msg", message);
        return "user/registerUser";
    }

    @PostMapping("/admin/saveUser")
    public String saveAdminUser(@ModelAttribute User user, Model model) {
        Integer id = userService.saveUser(user);
        String message = "Usuário '" + user.getName() + "' registrado com sucesso!";
        model.addAttribute("msg", message);
        return "user/registerAdmin";
    }

    @GetMapping("/accessDenied")
	public String getAccessDeniedPage() {
		return "user/accessDeniedPage";
	}
}
