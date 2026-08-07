package com.itv.internship.controller;

import com.itv.internship.dto.CartRequest;
import com.itv.internship.dto.CartResponse;
import com.itv.internship.dto.CartUpdateRequest;
import com.itv.internship.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Add to cart
    @PostMapping
    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody CartRequest request) {
        CartResponse response = cartService.addToCart(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Customer's own cart: /api/cart?customerId=1
    @GetMapping
    public ResponseEntity<List<CartResponse>> getCart(
            @RequestParam(name = "customerId", required = false) Long customerId) {
        if (customerId != null) {
            return ResponseEntity.ok(cartService.getCartByCustomer(customerId));
        }
        // Admin view: all customer carts (for cart abandonment analysis)
        return ResponseEntity.ok(cartService.getAllCarts());
    }

    // Update quantity
    @PutMapping("/{cartId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable Long cartId,
            @Valid @RequestBody CartUpdateRequest request) {
        return ResponseEntity.ok(cartService.updateCartItem(cartId, request));
    }

    // Remove from cart
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Map<String, String>> removeFromCart(@PathVariable Long cartId) {
        cartService.removeFromCart(cartId);
        return ResponseEntity.ok(Map.of("message", "Item removed from cart successfully"));
    }
}