package com.itv.internship.service;

import com.itv.internship.dto.CartRequest;
import com.itv.internship.dto.CartResponse;
import com.itv.internship.dto.CartUpdateRequest;

import java.util.List;

public interface CartService {
    CartResponse addToCart(CartRequest request);
    List<CartResponse> getCartByCustomer(Long customerId);
    List<CartResponse> getAllCarts();
    CartResponse updateCartItem(Long cartId, CartUpdateRequest request);
    void removeFromCart(Long cartId);
}