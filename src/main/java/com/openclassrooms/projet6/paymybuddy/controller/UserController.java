package com.openclassrooms.projet6.paymybuddy.controller;

import com.openclassrooms.projet6.paymybuddy.dto.ConnectionForm;
import com.openclassrooms.projet6.paymybuddy.dto.ProfileForm;
import com.openclassrooms.projet6.paymybuddy.dto.RegisterForm;
import com.openclassrooms.projet6.paymybuddy.entity.User;
import com.openclassrooms.projet6.paymybuddy.exception.BusinessException;
import com.openclassrooms.projet6.paymybuddy.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/connections")
    public String showConnectionsPage(Authentication authentication, Model model) {
        User user = userService.getByEmail(authentication.getName());
        populateConnectionsModel(user, model);
        model.addAttribute("connectionForm", new ConnectionForm(""));
        return "connections";
    }

    @PostMapping("/connections")
    public String addConnection(@Valid @ModelAttribute("connectionForm") ConnectionForm form,
                                BindingResult result,
                                Authentication authentication,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        User user = userService.getByEmail(authentication.getName());

        if (result.hasErrors()) {
            populateConnectionsModel(user, model);
            return "connections";
        }

        try {
            userService.addConnection(user.getId(), form.email());
            redirectAttributes.addFlashAttribute("success", "Relation ajoutée.");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/connections";
    }


    private void populateConnectionsModel(User user, Model model) {
        model.addAttribute("connections", userService.getConnections(user.getId()));
    }


    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerForm", new RegisterForm("", "", ""));
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm form,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "register";
        }

        try {
            userService.register(form.username(), form.email(), form.password());
            redirectAttributes.addFlashAttribute("success", "Compte créé, vous pouvez vous connecter.");
            return "redirect:/login";
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }


    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }


    @GetMapping("/profile")
    public String showProfilePage(Authentication authentication, Model model) {
        User user = userService.getByEmail(authentication.getName());
        model.addAttribute("profileForm",
                new ProfileForm(user.getUsername(), user.getEmail(), ""));
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profileForm") ProfileForm form,
                                BindingResult result,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "profile";
        }

        User user = userService.getByEmail(authentication.getName());

        try {
            userService.updateProfile(user.getId(), form.username(), form.email(), form.password());
            redirectAttributes.addFlashAttribute("success", "Profil mis à jour.");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }
}