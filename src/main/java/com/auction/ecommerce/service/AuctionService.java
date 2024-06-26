package com.auction.ecommerce.service;

import com.auction.ecommerce.model.Auction;
import com.auction.ecommerce.model.Category;
import com.auction.ecommerce.model.User;
import com.auction.ecommerce.repository.AuctionRepository;
import com.auction.ecommerce.repository.CategoryRepository;
import com.auction.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AuctionService {
	 
	private static final Logger logger = LoggerFactory.getLogger(AuctionService.class);
	private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public AuctionService(AuctionRepository auctionRepository, CategoryRepository categoryRepository,UserRepository userRepository) {
        this.auctionRepository = auctionRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public Auction createAuctionv2(Auction auction) {
        // Additional business logic can be added here before saving
        auction.setStatus("active"); // Set default status to active
        return auctionRepository.save(auction);
    }

    public List<Auction> getAllAuctions() {
        List<Auction> auctions = auctionRepository.findAll();
        auctions.forEach(auction -> System.out.println("Fetched auction: " + auction)); // Log each auction fetched
        return auctions;
    }

    public List<Auction> findAuctionsByCategoryId(Long categoryId) {
        return auctionRepository.findByCategoryId(categoryId);
    }

    public Optional<Auction> getAuctionById(int id) {
        return auctionRepository.findById(id);
    }

    public Auction updateAuctionv2(int id, Auction auctionDetails) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found"));
        
        // Update fields
        auction.setItemName(auctionDetails.getItemName());
        auction.setItemDescription(auctionDetails.getItemDescription());
        auction.setStartingPrice(auctionDetails.getStartingPrice());
        auction.setEndTime(auctionDetails.getEndTime());
        auction.setStatus(auctionDetails.getStatus());
        
        // Save the updated auction
        return auctionRepository.save(auction);
    }

    public void deleteAuctionv2(int id) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found"));
        
        auctionRepository.delete(auction);
    }

   // REST API methods
    
    public Auction createAuction(Auction auction) {
        validateAuction(auction);
       
        User user = getCurrentUser();
        Long categoryId = auction.getCategoryId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));
        auction.setCreator(user);
        auction.setCategory(category);
        auction.setHighestBid(0);
        Auction savedAuction = auctionRepository.save(auction);
        logger.info(savedAuction.toString());
        return savedAuction;
    }

    public Auction updateAuction(int id, Auction auctionDetails) throws IllegalAccessException {
        validateAuction(auctionDetails);
        Auction auctions = auctionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid auction ID"));
        User currentUser = getCurrentUser();
        if (!auctions.getCreator().equals(currentUser) && !currentUser.getRole().getName().equals("ROLE_ADMIN")) {
            throw new IllegalAccessException("You are not authorized to update this auction");
        }
   
        

        return auctionRepository.findById(id).map(auction -> {
            auction.setItemName(auctionDetails.getItemName());
            auction.setItemDescription(auctionDetails.getItemDescription());
            auction.setStartingPrice(auctionDetails.getStartingPrice());
            auction.setEndTime(auctionDetails.getEndTime());
            auction.setStatus(auctionDetails.getStatus());

            Long categoryId = auctionDetails.getCategoryId();
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));
            auction.setCategory(category);
            logger.info("auction = {} ",auction.toString());
            return auctionRepository.save(auction);
        }).orElseGet(() -> {
            auctionDetails.setId(id);
            Long categoryId = auctionDetails.getCategoryId();
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));
            auctionDetails.setCategory(category);
            logger.info("auctionDetails = {} ",auctionDetails.toString());
            return auctionRepository.save(auctionDetails);
        });
    }
    
    @Transactional
    public void deleteAuction(int id) throws IllegalAccessException   {
    	Auction auction = auctionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid auction ID"));
        User currentUser = getCurrentUser();

        if (!auction.getCreator().equals(currentUser) && !currentUser.getRole().getName().equals("ROLE_ADMIN")) {
            throw new IllegalAccessException("You are not authorized to delete this auction");
        }

        auctionRepository.delete(auction);
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

    private void validateAuction(Auction auction) {
    	logger.info("Auction {}",auction.toString());
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
        logger.info("categoryid : {}",auction.getCategoryId());
            throw new IllegalArgumentException("Category ID must be a positive number");
        }
    }
}
