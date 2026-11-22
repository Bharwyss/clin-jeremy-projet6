package com.openclassrooms.projet6.paymybuddy.controller;

import com.openclassrooms.projet6.paymybuddy.dto.TransferForm;
import com.openclassrooms.projet6.paymybuddy.entity.User;
import com.openclassrooms.projet6.paymybuddy.exception.BusinessException;
import com.openclassrooms.projet6.paymybuddy.service.TransferService;
import com.openclassrooms.projet6.paymybuddy.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class TransferController {

    private static final Logger logger = LoggerFactory.getLogger(TransferController.class);

    private final UserService userService;
    private final TransferService transferService;

    public TransferController(UserService userService, TransferService transferService) {
        this.userService = userService;
        this.transferService = transferService;
    }

    @GetMapping("/transfer")
    public String showTransferPage(Authentication authentication, Model model) {
        User user = userService.getByEmail(authentication.getName());
        populateTransferModel(user, model);
        model.addAttribute("transferForm", new TransferForm(null, "", null));
        return "transfer";
    }

    @PostMapping("/transfer")
    public String processTransfer(@Valid @ModelAttribute("transferForm") TransferForm form,
                                  BindingResult result,
                                  Authentication authentication,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        User user = userService.getByEmail(authentication.getName());

        if (result.hasErrors()) {
            populateTransferModel(user, model);
            return "transfer";
        }

        try {
            transferService.sendMoneyFromSenderToReceiver(
                    user.getId(), form.receiverId(), form.amount(), form.description());
            redirectAttributes.addFlashAttribute("success", "Virement effectué.");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/transfer";
    }

    private void populateTransferModel(User user, Model model) {
        model.addAttribute("currentUserId", user.getId());
        model.addAttribute("balance", user.getBalance());
        model.addAttribute("connections", userService.getConnections(user.getId()));
        model.addAttribute("transfers", transferService.getTransferHistory(user.getId()));
    }
}