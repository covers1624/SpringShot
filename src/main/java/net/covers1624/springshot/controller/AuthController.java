package net.covers1624.springshot.controller;

import net.covers1624.springshot.form.UserForm;
import net.covers1624.springshot.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

/**
 * Created by covers1624 on 5/8/20.
 */
@Controller
public class AuthController {

    private static final Logger logger = LogManager.getLogger();

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping ("/panel/login")
    public String signIn() {
        return "panel/login";
    }

    @GetMapping ("/panel/register")
    public String register(Model model) {
        UserForm formData = new UserForm();
        model.addAttribute("user", formData);
        return "panel/register";
    }

    @PostMapping ("/panel/register")
    public String doRegister(@ModelAttribute ("user") @Valid UserForm user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "panel/register";
        }
        userService.signUpUser(user, result);
        if (result.hasErrors()) {
            return "panel/register";
        }
        return "redirect:/panel/login";
    }
}
