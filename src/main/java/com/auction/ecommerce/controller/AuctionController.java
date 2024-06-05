package com.auction.ecommerce.controller;

import com.auction.ecommerce.model.Auction;
import com.auction.ecommerce.service.AuctionService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    @Autowired
    private AuctionService auctionService;

    @PostMapping
    public ResponseEntity<?> createAuction(@RequestBody Auction auction, @RequestParam Long categoryId) {
        if (auction.getAuctioneerId() == null || auction.getAuctioneerId() <= 0) {
            return new ResponseEntity<>("Auctioneer ID must be a positive number", HttpStatus.BAD_REQUEST);
        }
        Auction savedAuction = auctionService.createAuction(auction, categoryId);
        return new ResponseEntity<>(savedAuction, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Auction>> getAllAuctions() {
        List<Auction> auctions = auctionService.getAllAuctions();
        return new ResponseEntity<>(auctions, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Auction> getAuctionById(@PathVariable Long id) {
        Optional<Auction> auction = auctionService.getAuctionById(id);
        return auction.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Auction> updateAuction(@PathVariable Long id, @RequestBody Auction auctionDetails, @RequestParam Long categoryId) {
        Auction updatedAuction = auctionService.updateAuction(id, auctionDetails, categoryId);
        return new ResponseEntity<>(updatedAuction, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuction(@PathVariable Long id) {
        auctionService.deleteAuction(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
