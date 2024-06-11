package com.auction.ecommerce.repository;

import com.auction.ecommerce.model.Category;
//import com.auction.ecommerce.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	Optional<Category> findById(Long id);
}

