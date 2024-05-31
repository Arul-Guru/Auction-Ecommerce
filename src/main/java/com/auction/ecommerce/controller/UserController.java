package com.auction.ecommerce.controller;


import com.auction.ecommerce.model.User;
import com.auction.ecommerce.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.AuthenticationException;


//import javax.naming.AuthenticationException;
//import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
    	try {
    	userService.registerUser(user);
        return "User registered successfully";
    	}
    	catch(DataIntegrityViolationException e) {
    		return "Username or email already exists";
    	}
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody User user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "User logged in successfully";
        } catch (AuthenticationException e) {
            return "Invalid username or password";
        }
    }
}