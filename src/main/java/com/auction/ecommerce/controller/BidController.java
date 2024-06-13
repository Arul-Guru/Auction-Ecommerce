package com.auction.ecommerce.controller;

import com.auction.ecommerce.model.Auction;
import com.auction.ecommerce.model.Bid;
import com.auction.ecommerce.service.BidService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth/bids")
public class BidController {

    private static final Logger logger = LoggerFactory.getLogger(BidController.class);

    @Autowired
    private BidService bidService;

    @PostMapping
    public ResponseEntity<Object> placeBid(
            @RequestHeader("Authorization") String token, @RequestBody Bid bids) {
    	
        try {
            logger.info("Placing bid for auctionId: {}, userId: {}, bidAmount: {}", bids.getAuction().getId(), bids.getBidderId(), bids.getBidAmount());
            Bid bid = bidService.placeBid(bids.getAuction().getId(), bids.getBidderId(), bids.getBidAmount());
            return new ResponseEntity<>(bid, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Error placing bid: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<Bid>> getBidsByAuctionId(
            @RequestHeader("Authorization") String token,
            @PathVariable int auctionId) {
        logger.info("Fetching bids for auctionId: {}", auctionId);
        List<Bid> bids = bidService.getBidsByAuctionId(auctionId);
        return new ResponseEntity<>(bids, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBidById(
            @RequestHeader("Authorization") String token,
            @PathVariable int id) {
        logger.info("Fetching bid by id: {}", id);
        Optional<Bid> bid = bidService.getBidById(id);
        if (bid.isPresent()) {
            return new ResponseEntity<>(bid.get(), HttpStatus.OK);
        } else {
            logger.warn("Bid not found for id: {}", id);
            return new ResponseEntity<>("Bid not found", HttpStatus.NOT_FOUND);
        }
    }
}
