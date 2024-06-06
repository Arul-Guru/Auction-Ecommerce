package com.auction.ecommerce.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "auctions")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "description")
    private String itemDescription;

    @Column(name = "starting_bid")
    private double startingPrice;

    @Column(name = "highest_bid")
    private double highestBid;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "auctioneer_id")
    private int auctioneerId;

    @Column(name = "status")
    private String status;

    @Column(name = "category_id")
    private Long categoryId;

    @ManyToOne
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(double startingPrice) {
        this.startingPrice = startingPrice;
    }

    public double getHighestBid() {
        return highestBid;
    }

    public void setHighestBid(double highestBid) {
        this.highestBid = highestBid;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    @Override
    public String toString() {
        return "Auction{" +
                "id=" + id +
                ", itemName='" + itemName + '\'' +
                ", itemDescription='" + itemDescription + '\'' +
                ", startingPrice=" + startingPrice +
                ", highestBid=" + highestBid +
                ", endTime=" + endTime +
                ", auctioneerId=" + auctioneerId +
                ", status='" + status + '\'' +
                ", category=" + category +
                '}';
    }
}