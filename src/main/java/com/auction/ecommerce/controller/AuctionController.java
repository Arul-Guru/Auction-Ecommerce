package com.auction.ecommerce.controller;

import com.auction.ecommerce.model.Auction;
import com.auction.ecommerce.service.AuctionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth/auctions")
public class AuctionController {
	
	private static final Logger logger = LoggerFactory.getLogger(AuctionController.class);
    @Autowired
    private AuctionService auctionService;
    

    @PostMapping
    public ResponseEntity<Object> createAuction(@RequestHeader("Authorization") String token,@RequestBody Auction auction) {
        try {
        	logger.info(auction.toString());
            Auction savedAuction = auctionService.createAuction(auction);
            return new ResponseEntity<>(savedAuction, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Auction>> getAllAuctions(@RequestHeader("Authorization") String token) {
        List<Auction> auctions = auctionService.getAllAuctions();
        return new ResponseEntity<>(auctions, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAuctionById(@RequestHeader("Authorization") String token,@PathVariable int id) {
        Optional<Auction> auction = auctionService.getAuctionById(id);

        if (auction.isPresent()) {
            return new ResponseEntity<>(auction.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Auction not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateAuction(@RequestHeader("Authorization") String token,@PathVariable int id, @RequestBody Auction auctionDetails) {
        try {
            Auction updatedAuction = auctionService.updateAuction(id, auctionDetails);
            return new ResponseEntity<>(updatedAuction, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuction(@RequestHeader("Authorization") String token,@PathVariable int id) {
        auctionService.deleteAuction(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
