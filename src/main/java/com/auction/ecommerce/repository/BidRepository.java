package com.auction.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auction.ecommerce.model.Bid;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Integer> {
    List<Bid> findByAuctionIdOrderByBidAmountDesc(int auctionId);
}
