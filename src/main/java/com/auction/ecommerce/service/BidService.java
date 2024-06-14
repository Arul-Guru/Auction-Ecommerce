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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    public Bid placeBid(Bid bid) {
        int auctionId = bid.getAuction().getId();
        double bidAmount = bid.getBidAmount();
        logger.info("Placing bid for auctionId: {}, bidAmount: {}", auctionId, bidAmount);

        // Retrieve the current user and set the bidderId
        User currentUser = getCurrentUser();
        bid.setBidderId(currentUser.getId());
        logger.info("Bidder ID set to current user: {}", currentUser.getId());

        // Retrieve the auction by its ID
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> {
                    logger.error("Auction not found for id: {}", auctionId);
                    return new IllegalArgumentException("Auction not found");
                });

        // Check if the auctioneer is trying to place a bid on their own auction
        if (auction.getCreator().equals(currentUser)) { //have to change pojo
            logger.warn("Auctioneer (userId: {}) cannot place a bid on their own auction (auctionId: {})", currentUser.getId(), auctionId);
            throw new IllegalArgumentException("Auctioneer cannot place a bid on their own auction");
        }

        // Check if the auction has already ended
        if (auction.getEndTime().isBefore(LocalDateTime.now())) {
            logger.warn("Attempted to place a bid on a closed auction. AuctionId: {}", auctionId);
            throw new IllegalArgumentException("Auction is closed");
        }

        // Get the highest bid for the auction
        Bid highestBid = bidRepository.findBidsByAuctionIdOrderByBidAmountDesc(auctionId)
                .stream().findFirst().orElse(null);

        // Validate that the new bid amount is higher than the current highest bid
        if (highestBid != null && bidAmount <= highestBid.getBidAmount()) {
            logger.warn("Bid amount {} is not higher than the current highest bid {} for auctionId: {}", 
                        bidAmount, highestBid.getBidAmount(), auctionId);
            throw new IllegalArgumentException("Bid must be higher than the current highest bid");
        }

        // Set auction details and bid time
        bid.setAuction(auction);
        bid.setBidTime(LocalDateTime.now());

        // Save the new bid
        Bid savedBid = bidRepository.save(bid);
        logger.info("Bid successfully placed for auctionId: {}, bidderId: {}, bidAmount: {}", 
                    auctionId, bid.getBidderId(), bidAmount);
        
        // Update the auction's highest bid
        auction.setHighestBid(bidAmount);
        auctionRepository.save(auction);

        return savedBid;
    }
    
    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = null;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        logger.info(username);
        return userRepository.findByUsername(username);
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
