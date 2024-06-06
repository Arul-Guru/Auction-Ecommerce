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

    public Auction createAuction(Auction auction) {
        validateAuction(auction);

        Long categoryId = auction.getCategoryId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));

        auction.setCategory(category);
        auction.setHighestBid(0);
        Auction savedAuction = auctionRepository.save(auction);
        logger.info("Saved auction: {}", savedAuction);

        return savedAuction;
    }

    public List<Auction> getAllAuctions() {
        return auctionRepository.findAll();
    }

    public Optional<Auction> getAuctionById(Long id) {
        return auctionRepository.findById(id);
    }

    public Auction updateAuction(Long id, Auction auctionDetails) {
        validateAuction(auctionDetails);

        return auctionRepository.findById(id).map(auction -> {
            auction.setItemName(auctionDetails.getItemName());
            auction.setItemDescription(auctionDetails.getItemDescription());
            auction.setStartingPrice(auctionDetails.getStartingPrice());
            auction.setEndTime(auctionDetails.getEndTime());
            auction.setAuctioneerId(auctionDetails.getAuctioneerId());
            auction.setStatus(auctionDetails.getStatus());

            Long categoryId = auctionDetails.getCategoryId();
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));
            auction.setCategory(category);

            return auctionRepository.save(auction);
        }).orElseGet(() -> {
            auctionDetails.setId(id);
            Long categoryId = auctionDetails.getCategoryId();
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));
            auctionDetails.setCategory(category);
            return auctionRepository.save(auctionDetails);
        });
    }

    public void deleteAuction(Long id) {
        auctionRepository.deleteById(id);
    }

    private void validateAuction(Auction auction) {
        if (auction.getStartingPrice() < 0) {
            throw new IllegalArgumentException("Starting price cannot be negative");
        }
        if (auction.getHighestBid() < 0) {
            throw new IllegalArgumentException("Highest bid cannot be negative");
        }
        if (!"active".equalsIgnoreCase(auction.getStatus()) && !"closed".equalsIgnoreCase(auction.getStatus())) {
            throw new IllegalArgumentException("Status must be either 'active' or 'closed'");
        }
        if (auction.getCategoryId() == null || auction.getCategoryId() <= 0) {
            throw new IllegalArgumentException("Category ID must be a positive number");
        }
    }
}
