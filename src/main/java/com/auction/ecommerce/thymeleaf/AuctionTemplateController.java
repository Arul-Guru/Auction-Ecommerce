package com.auction.ecommerce.thymeleaf;

import com.auction.ecommerce.model.Auction;
import com.auction.ecommerce.model.Category;
import com.auction.ecommerce.service.AuctionService;
import com.auction.ecommerce.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/v2/auctions")
public class AuctionTemplateController {

    private static final Logger logger = LoggerFactory.getLogger(AuctionTemplateController.class);

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private CategoryService categoryService;
    
    @GetMapping("/category/{id}")
    public String listAuctionsByCategory(@PathVariable Long id, Model model) {
        Optional<Category> category = categoryService.getCategoryById(id);
        if (category.isPresent()) {
            List<Auction> auctions = auctionService.findAuctionsByCategoryId(id);
            model.addAttribute("category", category.get()); // Ensure the category object is passed to the model
            model.addAttribute("auctions", auctions);
            return "list-auctions";
        } else {
            return "redirect:/";
        }
    }


    @GetMapping("/create")
    public String createAuctionForm(@RequestParam(required = false) Long categoryId, Model model) {
        Auction auction = new Auction();
        if (categoryId != null) {
            auction.setCategoryId(categoryId);
        }
        model.addAttribute("auction", auction);
        model.addAttribute("categoryId", categoryId); // Pass categoryId to the template
        return "create-auction";
    }


    @PostMapping
    public String createAuction(@ModelAttribute Auction auction) {
        // Automatically set the status based on the end time
        if (auction.getEndTime().isAfter(LocalDateTime.now())) {
            auction.setStatus("active");
        } else {
            auction.setStatus("closed"); // or any other status you deem appropriate
        }
        auctionService.createAuction(auction);
        return "redirect:/api/v2/auctions/category/" + auction.getCategoryId();
    }


    @GetMapping("/{id}")
    public String getAuctionById(@PathVariable int id, Model model) {
        Optional<Auction> auction = auctionService.getAuctionById(id);
        if (auction.isPresent()) {
            model.addAttribute("auction", auction.get());
            return "auction-details";
        } else {
            return "redirect:/api/v2/auctions";
        }
    }

    @GetMapping("/update/{id}")
    public String updateAuctionForm(@PathVariable int id, Model model) {
        Optional<Auction> auction = auctionService.getAuctionById(id);
        if (auction.isPresent()) {
            model.addAttribute("auction", auction.get());
            return "update-auction";
        } else {
            return "redirect:/api/v2/auctions";
        }
    }

    @PostMapping("/update/{id}")
    public String updateAuction(@PathVariable int id, @ModelAttribute Auction auctionDetails) {
        try {
            auctionService.updateAuction(id, auctionDetails);
        } catch (IllegalArgumentException e) {
            logger.error("Error updating auction: {}", e.getMessage());
            return "error"; // Ensure you have an error.html to handle such cases
        }
        return "redirect:/api/v2/auctions/category/" + auctionDetails.getCategoryId();
    }

    @GetMapping("/delete/{id}")
    public String deleteAuctionForm(@PathVariable int id, Model model) {
        Optional<Auction> auction = auctionService.getAuctionById(id);
        if (auction.isPresent()) {
            model.addAttribute("auction", auction.get());
            return "delete-auction";
        } else {
            return "redirect:/api/v2/auctions";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteAuction(@PathVariable int id) {
        Optional<Auction> auction = auctionService.getAuctionById(id);
        if (auction.isPresent()) {
            auctionService.deleteAuction(id);
            return "redirect:/api/v2/auctions/category/" + auction.get().getCategoryId();
        } else {
            return "redirect:/api/v2/auctions";
        }
    }
}