package com.auction.ecommerce.service;

import com.auction.ecommerce.model.Auction;
import com.auction.ecommerce.repository.AuctionRepository;
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

    @Autowired
    public AuctionService(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    public Auction createAuction(Auction auction) {
        logger.info("Creating auction: {}", auction);
        auction.setHighestBid(0); // Assuming highest bid starts at 0
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
        return auctionRepository.findById(id).map(auction -> {
            auction.setItemName(auctionDetails.getItemName());
            auction.setItemDescription(auctionDetails.getItemDescription());
            auction.setStartingPrice(auctionDetails.getStartingPrice());
            auction.setEndTime(auctionDetails.getEndTime());
            auction.setAuctioneerId(auctionDetails.getAuctioneerId());
            auction.setStatus(auctionDetails.getStatus());
            return auctionRepository.save(auction);
        }).orElseGet(() -> {
            auctionDetails.setId(id);
            return auctionRepository.save(auctionDetails);
        });
    }

    public void deleteAuction(Long id) {
        auctionRepository.deleteById(id);
    }
}
