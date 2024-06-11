package com.auction.ecommerce.service;

import com.auction.ecommerce.exception.ResourceNotFoundException;
import com.auction.ecommerce.model.Auction;
import com.auction.ecommerce.model.Category;
import com.auction.ecommerce.repository.AuctionRepository;
import com.auction.ecommerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class AuctionService {

    private static final Logger logger = LoggerFactory.getLogger(AuctionService.class);

    private final AuctionRepository auctionRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public AuctionService(AuctionRepository auctionRepository, CategoryRepository categoryRepository) {
        this.auctionRepository = auctionRepository;
        this.categoryRepository = categoryRepository;
    }

    public Auction createAuction(Auction auction, Long categoryId) {
        logger.info("Creating auction: {}", auction);
        auction.setHighestBid(0); // Assuming highest bid starts at 0
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        auction.setCategory(category);
        Auction savedAuction = auctionRepository.save(auction);
        logger.info("Saved auction: {}", savedAuction);
        return savedAuction;
    }

    public List<Auction> getAllAuctions() {
        return auctionRepository.findAll();
    }

    public Optional<Auction> getAuctionById(int id) {
        return auctionRepository.findById(id);
    }

    public Auction updateAuction(int id, Auction auctionDetails, Long categoryId) {
        return auctionRepository.findById(id).map(auction -> {
            auction.setItemName(auctionDetails.getItemName());
            auction.setItemDescription(auctionDetails.getItemDescription());
            auction.setStartingPrice(auctionDetails.getStartingPrice());
            auction.setEndTime(auctionDetails.getEndTime());
            auction.setAuctioneerId(auctionDetails.getAuctioneerId());
            auction.setStatus(auctionDetails.getStatus());
            
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            auction.setCategory(category);
            
            return auctionRepository.save(auction);
        }).orElseGet(() -> {
            auctionDetails.setId(id);
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            auctionDetails.setCategory(category);
            return auctionRepository.save(auctionDetails);
        });
    }

    public void deleteAuction(int id) {
        auctionRepository.deleteById(id);
    }
}