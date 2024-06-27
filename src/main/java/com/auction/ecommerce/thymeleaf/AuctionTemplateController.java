package com.auction.ecommerce.thymeleaf;

import com.auction.ecommerce.model.Auction;
import com.auction.ecommerce.model.Bid;
import com.auction.ecommerce.model.Category;
import com.auction.ecommerce.model.User;
import com.auction.ecommerce.repository.UserRepository;
import com.auction.ecommerce.service.AuctionService;
import com.auction.ecommerce.service.BidService;
import com.auction.ecommerce.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
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
    private BidService bidService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/category/{id}")
    public String listAuctionsByCategory(@PathVariable Long id, Model model, HttpSession session) {
        Optional<Category> category = categoryService.getCategoryById(id);
        if (category.isPresent()) {
            List<Auction> auctions = auctionService.findAuctionsByCategoryId(id);
            model.addAttribute("category", category.get());
            model.addAttribute("auctions", auctions);
            
            // Add userId to the model
            Integer userId = (Integer) session.getAttribute("userId");
            model.addAttribute("userId", userId);

            return "list-auctions";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/create")
    public String createAuctionForm(@RequestParam(required = false) Long categoryId, Model model, HttpSession session) {
        Auction auction = new Auction();
        if (categoryId != null) {
            auction.setCategoryId(categoryId);
        }
        model.addAttribute("auction", auction);
        model.addAttribute("categoryId", categoryId);
        
        // Add userId to the model
        Integer userId = (Integer) session.getAttribute("userId");
        model.addAttribute("userId", userId);
        
        return "create-auction";
    }

    @PostMapping
    public String createAuction(@ModelAttribute Auction auction,HttpSession session) {
        // Automatically set the status based on the end time
        String userName = (String)session.getAttribute("userName");
        logger.info("username in auctiontemp : {}",userName);
        User creator = userRepository.findByUsername(userName);
        if (auction.getEndTime().isAfter(LocalDateTime.now())) {
            auction.setStatus("active");
            auction.setCreator(creator);
            logger.info("creator : {} ",creator);
        } else {
            auction.setStatus("closed"); // or any other status you deem appropriate
        }
        auctionService.createAuctionv2(auction);
        return "redirect:/api/v2/auctions/category/" + auction.getCategoryId();
    }

    @GetMapping("/{id}")
    public String getAuctionById(@PathVariable int id, Model model, HttpSession session) {
        Optional<Auction> auction = auctionService.getAuctionById(id);
        if (auction.isPresent()) {
            model.addAttribute("auction", auction.get());
            
            // Add userId to the model
            Integer userId = (Integer) session.getAttribute("userId");
            model.addAttribute("userId", userId);
            
            return "auction-details";
        } else {
            return "redirect:/api/v2/auctions";
        }
    }

    @GetMapping("/update/{id}")
    public String updateAuctionForm(@PathVariable int id, Model model, HttpSession session) {
        Optional<Auction> auction = auctionService.getAuctionById(id);
        if (auction.isPresent()) {
            model.addAttribute("auction", auction.get());

            // Add userId to the model
            Integer userId = (Integer) session.getAttribute("userId");
            model.addAttribute("userId", userId);

            // Check if the logged-in user is the creator
            if (auction.get().getCreator().getId() == userId) {
                return "update-auction";
            } else {
                model.addAttribute("errorMessage", "You are not authorized to update this auction.");
                return "error";
            }
        } else {
            return "redirect:/api/v2/auctions";
        }
    }

    @PostMapping("/update/{id}")
    public String updateAuction(@PathVariable int id, @ModelAttribute Auction auctionDetails, HttpSession session,Model model) {
        try {
        	Optional<Auction> auction = auctionService.getAuctionById(id);
            Integer userId = (Integer) session.getAttribute("userId");
            model.addAttribute("auction",auction.get());
            // Only allow the creator to update
            if (auction.get().getCreator().getId() == userId) {
                auctionService.updateAuctionv2(id, auctionDetails);
            } else {
                logger.error("User not authorized to update auction");
                return "error";
            }
        } catch (IllegalArgumentException e) {
            logger.error("Error updating auction: {}", e.getMessage());
            return "error";
        }
        return "redirect:/api/v2/auctions/category/" + auctionDetails.getCategoryId();
    }

    @GetMapping("/delete/{id}")
    public String deleteAuctionForm(@PathVariable int id, Model model, HttpSession session) {
        Optional<Auction> auction = auctionService.getAuctionById(id);
        if (auction.isPresent()) {
            model.addAttribute("auction", auction.get());

            // Add userId to the model
            Integer userId = (Integer) session.getAttribute("userId");
            model.addAttribute("userId", userId);

            // Check if the logged-in user is the creator
            if (auction.get().getCreator().getId() == userId) {
                return "delete-auction";
            } else {
                model.addAttribute("errorMessage", "You are not authorized to delete this auction.");
                return "error";
            }
        } else {
            return "redirect:/api/v2/auctions";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteAuction(@PathVariable int id, HttpSession session) {
        Optional<Auction> auction = auctionService.getAuctionById(id);
        if (auction.isPresent()) {
            Integer userId = (Integer) session.getAttribute("userId");

            // Only allow the creator to delete
            if (auction.get().getCreator().getId() == userId) {
                auctionService.deleteAuctionv2(id);
                return "redirect:/api/v2/auctions/category/" + auction.get().getCategoryId();
            } else {
                logger.error("User not authorized to delete auction");
                return "error";
            }
        } else {
            return "redirect:/api/v2/auctions";
        }
    }
    
    @GetMapping("/bid/{id}")
    public String showBidForm(@PathVariable int id, Model model, HttpSession session) {
        Optional<Auction> auction = auctionService.getAuctionById(id);
        if (auction.isPresent()) {
            model.addAttribute("auction", auction.get());
            Integer userId = (Integer) session.getAttribute("userId");
            model.addAttribute("userId", userId);
            return "place-bid";
        } else {
            return "redirect:/api/v2/auctions";
        }
    }

    @PostMapping("/place-bid")
    public String placeBid(@RequestParam int auctionId, @RequestParam double bidAmount, Model model, HttpSession session) {
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            Bid bid = bidService.placeBidv2(auctionId, userId, bidAmount);

            return "redirect:/api/v2/auctions/category/" + bid.getAuction().getCategoryId();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            Optional<Auction> auction = auctionService.getAuctionById(auctionId);
            auction.ifPresent(value -> model.addAttribute("auction", value));
            return "place-bid";
        }
    }
}