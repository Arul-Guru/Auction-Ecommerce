package com.auction.ecommerce.thymeleaf;

import com.auction.ecommerce.model.Category;
import com.auction.ecommerce.service.CategoryService;

import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/v2")
public class HomeController {
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/home")
    public String viewHomePage(Model model,HttpSession session) {
        List<Category> categories = categoryService.getAllCategories();
        String userName = (String)session.getAttribute("userName");
        logger.info("username : {}",userName);
        //session.setAttribute("userName", userName);
        model.addAttribute("categories", categories);
        return "index"; // Ensure this matches the name of your Thymeleaf template (index.html)
    }
}
