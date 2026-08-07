package com.itv.internship.controller;

import com.itv.internship.dto.CartResponse;
import com.itv.internship.dto.MoveToCartRequest;
import com.itv.internship.dto.WishlistRequest;
import com.itv.internship.dto.WishlistResponse;
import com.itv.internship.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    public ResponseEntity<WishlistResponse> addToWishlist(@Valid @RequestBody WishlistRequest request) {
        WishlistResponse response = wishlistService.addToWishlist(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // /api/wishlist?customerId=1
    @GetMapping
    public ResponseEntity<List<WishlistResponse>> getWishlist(
            @RequestParam(name = "customerId") Long customerId) {
        return ResponseEntity.ok(wishlistService.getWishlistByCustomer(customerId));
    }

    @DeleteMapping("/{wishlistId}")
    public ResponseEntity<Map<String, String>> removeFromWishlist(@PathVariable Long wishlistId) {
        wishlistService.removeFromWishlist(wishlistId);
        return ResponseEntity.ok(Map.of("message", "Item removed from wishlist"));
    }

    @PostMapping("/{wishlistId}/move-to-cart")
    public ResponseEntity<CartResponse> moveToCart(
            @PathVariable Long wishlistId,
            @RequestBody(required = false) MoveToCartRequest request) {
        CartResponse response = wishlistService.moveToCart(
                wishlistId, request != null ? request : new MoveToCartRequest());
        return ResponseEntity.ok(response);
    }
}