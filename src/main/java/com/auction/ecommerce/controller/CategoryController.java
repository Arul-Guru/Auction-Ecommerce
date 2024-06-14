package com.auction.ecommerce.controller;

import com.auction.ecommerce.model.Category;
import com.auction.ecommerce.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
	private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestHeader("Authorization") String token,@RequestBody Category category) {
    	Category savedCategory = categoryService.createCategory(category);
    	logger.info("savedCategory = {}",savedCategory.toString());
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories(@RequestHeader("Authorization") String token) {
        List<Category> categories = categoryService.getAllCategories();
        logger.info("All categories = {}",categories.toString());
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCategoryById(@RequestHeader("Authorization") String token,@PathVariable Long id) {
        Optional<Category> category = categoryService.getCategoryById(id);
        //logger.info("getCategoryById = {}",category);
        if (category.isPresent()) {
            return new ResponseEntity<>(category.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Category not found", HttpStatus.NOT_FOUND);
        }
    }
}