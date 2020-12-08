package net.covers1624.springshot.controller;

import net.covers1624.springshot.entity.User;
import net.covers1624.springshot.service.ApiKeyService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by covers1624 on 7/11/20.
 */
@Controller
public class AccountController {

    private final ApiKeyService apiKeyService;

    public AccountController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @GetMapping ("/panel/account")
    public String account(Model model, Authentication auth) {
        if (!auth.isAuthenticated()) {
            return "redirect:/panel/login";
        }
        User user = (User) auth.getPrincipal();
        model.addAttribute("user", user);
        model.addAttribute("api_keys", apiKeyService.getAllKeys(user));
        return "panel/account";
    }

    @PostMapping ("/panel/account/add_key")
    public String addApiKey(Authentication auth) {
        if (!auth.isAuthenticated()) {
            return "redirect:/panel/login";
        }
        User user = (User) auth.getPrincipal();
        apiKeyService.allocateKey(user);
        return "redirect:/panel/account";
    }

//    @PostMapping("/account/revoke_key")
//    public String revokeKey(Authentication auth) {
//
//    }
}
