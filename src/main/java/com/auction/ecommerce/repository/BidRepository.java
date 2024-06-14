package com.auction.ecommerce.repository;

import com.auction.ecommerce.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Integer> {
    // Finds all bids by auction ID using a derived query method
    List<Bid> findByAuctionId(int auctionId);

    // Optionally, you can define a custom query if more control is needed
    @Query("SELECT b FROM Bid b WHERE b.auction.id = :auctionId ORDER BY b.bidAmount DESC")
    List<Bid> findBidsByAuctionIdOrderByBidAmountDesc(int auctionId);
}
