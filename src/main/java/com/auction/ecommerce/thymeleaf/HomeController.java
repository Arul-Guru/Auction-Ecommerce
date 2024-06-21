package com.auction.ecommerce.thymeleaf;

import com.auction.ecommerce.model.Category;
import com.auction.ecommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/v2")
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/home")
    public String viewHomePage(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "index"; // Ensure this matches the name of your Thymeleaf template (index.html)
    }
}
