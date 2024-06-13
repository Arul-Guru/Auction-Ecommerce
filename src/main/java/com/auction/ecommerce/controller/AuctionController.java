package com.auction.ecommerce.controller;

import com.auction.ecommerce.model.Auction;
import com.auction.ecommerce.service.AuctionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.exceptions.TemplateInputException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auctions")
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
        logger.info("getAllAuctions = {}",auctions.toString());
        return new ResponseEntity<>(auctions, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAuctionById(@RequestHeader("Authorization") String token,@PathVariable int id) {
    	try {
    	Optional<Auction> auction = auctionService.getAuctionById(id);
        logger.info("getAuctionById = {}",auction.toString());
        	if (auction.isPresent()) {
                return new ResponseEntity<>(auction.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Auction not found", HttpStatus.NOT_FOUND);
            }
        } catch (TemplateInputException e) {
    		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    	}
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Object> getAuctionsByCategoryId(@RequestHeader("Authorization") String token, @PathVariable Long categoryId) {
        List<Auction> auctions = auctionService.findAuctionsByCategoryId(categoryId);

        logger.info("getAuctionByCategoryId = {}", auctions.toString());

        if (auctions.isEmpty()) {
            return new ResponseEntity<>("No auctions created in this category", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(auctions, HttpStatus.OK);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateAuction(@RequestHeader("Authorization") String token,@PathVariable int id, @RequestBody Auction auctionDetails) throws IllegalAccessException {
        try {
            Auction updatedAuction = auctionService.updateAuction(id, auctionDetails);
            logger.info("UpdatedAuction = {} & {}",id,auctionDetails);
            return new ResponseEntity<>(updatedAuction, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAuction(@RequestHeader("Authorization") String token,@PathVariable int id) throws IllegalAccessException {
    	try {
    	logger.info(token,id);
    	auctionService.deleteAuction(id);
        return new ResponseEntity<>("Auction Deleted Successfully",HttpStatus.OK);
    	}
    	catch(IllegalArgumentException e) {
    		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    	}
    	catch(NullPointerException e) {
    		logger.info(e.getMessage());
    		return new ResponseEntity<>("You are not the creator of this auction, so creator() is null", HttpStatus.BAD_REQUEST);
    	}
    	catch(TemplateInputException e) {
    		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    	}
    }
}