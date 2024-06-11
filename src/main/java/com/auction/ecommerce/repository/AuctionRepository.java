package com.auction.ecommerce.repository;

import com.auction.ecommerce.model.Auction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, Integer> {
	List<Auction> findByCategoryId(Long categoryId);
}
