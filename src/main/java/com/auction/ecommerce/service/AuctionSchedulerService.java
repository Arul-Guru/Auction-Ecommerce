package com.auction.ecommerce.service;

import com.auction.ecommerce.model.Auction;
import com.auction.ecommerce.repository.AuctionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuctionSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(AuctionSchedulerService.class);

    private final AuctionRepository auctionRepository;

    public AuctionSchedulerService(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    @Scheduled(fixedRate = 60000) // Runs every minute
    public void closeExpiredAuctions() {
        logger.info("Checking for expired auctions to close...");

        LocalDateTime now = LocalDateTime.now();
        List<Auction> expiredAuctions = auctionRepository.findByEndTimeBeforeAndStatus(now, "active");

        for (Auction auction : expiredAuctions) {
            auction.setStatus("closed");
            auctionRepository.save(auction);
            logger.info("Closed auction with ID: {}", auction.getId());
        }
    }
}
