package com.web_ii.tech_catalog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.web_ii.tech_catalog.models.User;
import com.web_ii.tech_catalog.service.IUserService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    // Listar todos os usuários (apenas para Admin)
    @GetMapping("/admin/users")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "user/listUsers";
    }

    // Ir para página de edição de usuário
    @GetMapping("/admin/users/edit/{id}")
    public String editUserForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userService.getUserById(id);
        
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            return "user/editUser";
        } else {
            redirectAttributes.addFlashAttribute("error", "Usuário não encontrado!");
            return "redirect:/admin/users";
        }
    }

    // Atualizar usuário
    @PostMapping("/admin/users/update")
    public String updateUser(@ModelAttribute User user, String password, RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(user, password);
            redirectAttributes.addFlashAttribute("msg", "Usuário '" + user.getName() + "' atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar usuário: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // Excluir usuário
    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            Optional<User> userOpt = userService.getUserById(id);
            if (userOpt.isPresent()) {
                userService.deleteUser(id);
                redirectAttributes.addFlashAttribute("msg", "Usuário '" + userOpt.get().getName() + "' excluído com sucesso!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Usuário não encontrado!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao excluir usuário: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/accessDenied")
    public String getAccessDeniedPage() {
        return "user/accessDeniedPage";
    }
}