package com.auction.ecommerce.thymeleaf;

import com.auction.ecommerce.exception.DuplicateCategoryException;
import com.auction.ecommerce.model.Category;
import com.auction.ecommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/categories")
public class CategoryTemplateController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/create")
    public String showCreateCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "create-category";
    }

    @PostMapping
    public String createCategory(Category category, BindingResult result, Model model) {
        try {
            categoryService.createCategory(category);
        } catch (DuplicateCategoryException e) {
            result.rejectValue("name", "error.category", "Category name already exists.");
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("category", category);
            return "create-category";
        }
        return "redirect:/api/v2/home";
    }

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "list-categories";
    }
}
