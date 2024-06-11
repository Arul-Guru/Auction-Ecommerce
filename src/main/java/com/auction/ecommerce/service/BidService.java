package com.auction.ecommerce.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.ecommerce.exception.ResourceNotFoundException;
import com.auction.ecommerce.model.Auction;
import com.auction.ecommerce.model.Bid;
import com.auction.ecommerce.model.User;
import com.auction.ecommerce.repository.AuctionRepository;
import com.auction.ecommerce.repository.BidRepository;
import com.auction.ecommerce.repository.UserRepository;

@Service
public class BidService {

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;

    public BidService(BidRepository bidRepository, AuctionRepository auctionRepository, UserRepository userRepository) {
        this.bidRepository = bidRepository;
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Bid placeBid(int auctionId, int userId, BigDecimal bidAmount) { // Change userId to int
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found"));

        if (auction.getEndTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Auction is closed");
        }

        Bid highestBid = bidRepository.findByAuctionIdOrderByBidAmountDesc(auctionId)
                .stream().findFirst().orElse(null);

        if (highestBid != null && bidAmount.compareTo(highestBid.getBidAmount()) <= 0) {
            throw new IllegalArgumentException("Bid must be higher than the current highest bid");
        }

        // Fetch the user from the repository using int userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Bid newBid = new Bid();
        newBid.setAuction(auction);
        newBid.setUser(user); // Set the fetched user
        newBid.setBidAmount(bidAmount);
        newBid.setBidTime(LocalDateTime.now());

        return bidRepository.save(newBid);
    }
}
