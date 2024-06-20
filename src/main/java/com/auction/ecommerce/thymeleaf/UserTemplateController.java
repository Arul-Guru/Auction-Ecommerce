package com.auction.ecommerce.thymeleaf;

import com.auction.ecommerce.model.AuthRequest;
import com.auction.ecommerce.model.User;
import com.auction.ecommerce.repository.UserRepository;
import com.auction.ecommerce.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v2")
public class UserTemplateController {
	private final UserService userService;
	private static final Logger logger = LoggerFactory.getLogger(UserTemplateController.class);
    @Autowired
    public UserTemplateController(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            result.rejectValue("username", "error.user", "Username is already taken");
        }

        if (result.hasErrors()) {
            model.addAttribute("org.springframework.validation.BindingResult.user", result);
            model.addAttribute("user", user);
            return "register";
        } else {
            try {
                userService.registerUser(user);
                //logger added for registration
                logger.info("registered successfull");
                model.addAttribute("successMessage", "Registered successfully!");
                return "redirect:/api/v2/login";
            } catch (Exception e) {
                model.addAttribute("errorMessage", "An error occurred while processing your request: " + e.getMessage());
                return "register";
            }
        }
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("authRequest", new AuthRequest());
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("authRequest") AuthRequest authRequest,BindingResult result, HttpSession session, Model model) {
    	logger.info("entered login api");
    	User foundUser = userRepository.findByUsername(authRequest.getUsername());
    	if (foundUser != null && passwordEncoder.matches(authRequest.getPassword(), foundUser.getPassword())) {
    		logger.info("logged in successfull");
    		return "redirect:/api/v2/home";
    	}
    	else {
    		logger.info("invalid username or password");
            model.addAttribute("errorMessage", "Invalid username or password");
            model.addAttribute("authRequest", authRequest);
    		return "/login";
    	}
   }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login"; // Redirect to login page after logout
    }
}
