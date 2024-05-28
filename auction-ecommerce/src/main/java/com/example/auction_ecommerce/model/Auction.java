package com.example.auction_ecommerce.model;
import java.time.ZonedDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Auction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
    private int id;
    private String itemName;
    private String description;
    private double startingBid;
    private double highestBid;
    private ZonedDateTime endTime; // Changed from Timestamp to ZonedDateTime
    private int auctioneerId;
    private String status;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getStartingBid() {
        return startingBid;
    }

    public void setStartingBid(double startingBid) {
        this.startingBid = startingBid;
    }

    public double getHighestBid() {
        return highestBid;
    }

    public void setHighestBid(double highestBid) {
        this.highestBid = highestBid;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    public int getAuctioneerId() {
        return auctioneerId;
    }

    public void setAuctioneerId(int auctioneerId) {
        this.auctioneerId = auctioneerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return this.endTime.isAfter(ZonedDateTime.now()) && "active".equalsIgnoreCase(this.status);
    }
}

