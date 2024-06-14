package com.auction.ecommerce.service;

import com.auction.ecommerce.model.Role;
import com.auction.ecommerce.model.User;
import com.auction.ecommerce.repository.RoleRepository;
import com.auction.ecommerce.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    
    @Autowired
    public UserService(UserRepository userRepository,RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User registerUser(User user){
        validateEmail(user.getEmail());
        validatePhoneNumber(user.getPhone());
        validateGender(user.getGender());
        validatePassword(user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
        }
        user.setRole(userRole);
        //logger added
        logger.info(user.toString());
        return userRepository.save(user);
    }
    //implementation pending
    public void assignAdminRole(User user) {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        if (adminRole == null) {
            adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);
        }
        user.setRole(adminRole);
        userRepository.save(user);
    }

    public boolean authenticateUser(User user) {
        User foundUser = userRepository.findByUsername(user.getUsername());
        logger.info(foundUser.toString());
        if (foundUser != null && passwordEncoder.matches(user.getPassword(), foundUser.getPassword())) {
            return true;
        }
        return false;
    }

    private void validatePassword(String password) {
    	logger.info("validating password = {}",password);
        if (password == null || !Pattern.matches("^(?=.*[A-Z])(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", password)) {
            throw new IllegalArgumentException("Password must be at least 8 characters long, contain one uppercase letter, and one special symbol");
        }
    }
    
    private void validateEmail(String email) {
    	logger.info("validating email = {}",email);
        if (email == null || !Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private void validatePhoneNumber(String phoneNumber) {
    	logger.info("validating phoneNumber = {}",phoneNumber);
        if (phoneNumber == null || !Pattern.matches("^\\+?[0-9. ()-]{7,25}$", phoneNumber)) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
    }
    private void validateGender(String gender) {
    	logger.info("validating gender = {}",gender);
        if (!gender.equalsIgnoreCase("male") && !gender.equalsIgnoreCase("female") && !gender.equalsIgnoreCase("others")) {
            throw new IllegalArgumentException("Invalid gender");
        }
    }

}