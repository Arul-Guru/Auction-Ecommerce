package com.auction.ecommerce.service;

import com.auction.ecommerce.model.Bid;
import com.auction.ecommerce.model.Auction;
import com.auction.ecommerce.model.User;
import com.auction.ecommerce.repository.BidRepository;
import com.auction.ecommerce.repository.AuctionRepository;
import com.auction.ecommerce.repository.UserRepository;
import com.auction.ecommerce.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BidService {

    private static final Logger logger = LoggerFactory.getLogger(BidService.class);

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;

    public BidService(BidRepository bidRepository, AuctionRepository auctionRepository, UserRepository userRepository) {
        this.bidRepository = bidRepository;
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Bid placeBid(int auctionId, int userId, double bidAmount) {
        logger.info("Placing bid for auctionId: {}, userId: {}, bidAmount: {}", auctionId, userId, bidAmount);

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> {
                    logger.error("Auction not found for id: {}", auctionId);
                    return new IllegalArgumentException("Auction not found");
                });

        if (auction.getEndTime().isBefore(LocalDateTime.now())) {
            logger.warn("Attempted to place a bid on a closed auction. AuctionId: {}", auctionId);
            throw new IllegalArgumentException("Auction is closed");
        }

        Bid highestBid = bidRepository.findBidsByAuctionIdOrderByBidAmountDesc(auctionId)
                .stream().findFirst().orElse(null);

        if (highestBid != null && bidAmount <= highestBid.getBidAmount()) {
            logger.warn("Bid amount {} is not higher than the current highest bid {} for auctionId: {}", 
                        bidAmount, highestBid.getBidAmount(), auctionId);
            throw new IllegalArgumentException("Bid must be higher than the current highest bid");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found for id: {}", userId);
                    throw new IllegalArgumentException("User not found");
                });

        Bid newBid = new Bid();
        newBid.setAuction(auction);
        newBid.setBidderId(userId); // Set the user ID directly
        newBid.setBidAmount(bidAmount);
        newBid.setBidTime(LocalDateTime.now());

        Bid savedBid = bidRepository.save(newBid);
        logger.info("Bid successfully placed for auctionId: {}, userId: {}, bidAmount: {}", 
                    auctionId, userId, bidAmount);
        
        auction.setHighestBid(bidAmount);
        auctionRepository.save(auction);

        return savedBid;
    }

    // Add the method to get all bids by auction ID
    public List<Bid> getBidsByAuctionId(int auctionId) {
        logger.info("Fetching bids for auctionId: {}", auctionId);
        return bidRepository.findByAuctionId(auctionId);
    }

    // Add the method to get a bid by its ID
    public Optional<Bid> getBidById(int id) {
        logger.info("Fetching bid by id: {}", id);
        return bidRepository.findById(id);
    }
}
