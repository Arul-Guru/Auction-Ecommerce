package com.auction.ecommerce.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auction.ecommerce.model.Bid;
import com.auction.ecommerce.model.User;
import com.auction.ecommerce.service.BidService;

@RestController
@RequestMapping("/api/bids")
public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping
    public ResponseEntity<?> placeBid(
            @RequestParam int auctionId,
            @RequestParam BigDecimal bidAmount,
            @AuthenticationPrincipal User user) {
        
        try {
            Bid bid = bidService.placeBid(auctionId, user.getId(), bidAmount);
            return ResponseEntity.ok(bid);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
