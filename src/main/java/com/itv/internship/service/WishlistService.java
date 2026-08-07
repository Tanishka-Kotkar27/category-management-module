package com.itv.internship.service;

import com.itv.internship.dto.CartResponse;
import com.itv.internship.dto.MoveToCartRequest;
import com.itv.internship.dto.WishlistRequest;
import com.itv.internship.dto.WishlistResponse;

import java.util.List;

public interface WishlistService {
    WishlistResponse addToWishlist(WishlistRequest request);
    List<WishlistResponse> getWishlistByCustomer(Long customerId);
    void removeFromWishlist(Long wishlistId);
    CartResponse moveToCart(Long wishlistId, MoveToCartRequest request);
}