package com.itv.internship.service;

import com.itv.internship.dto.CartResponse;
import com.itv.internship.dto.MoveToCartRequest;
import com.itv.internship.dto.WishlistRequest;
import com.itv.internship.dto.WishlistResponse;
import com.itv.internship.entity.Product;
import com.itv.internship.entity.User;
import com.itv.internship.entity.Wishlist;
import com.itv.internship.exception.DuplicateWishlistItemException;
import com.itv.internship.exception.ResourceNotFoundException;
import com.itv.internship.repository.ProductRepository;
import com.itv.internship.repository.UserRepository;
import com.itv.internship.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    @Override
    @Transactional
    public WishlistResponse addToWishlist(WishlistRequest request) {
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + request.getCustomerId()));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + request.getProductId()));

        if (wishlistRepository.existsByCustomer_UserIdAndProduct_ProductId(
                request.getCustomerId(), request.getProductId())) {
            throw new DuplicateWishlistItemException(
                    "'" + product.getProductName() + "' is already in this customer's wishlist");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setCustomer(customer);
        wishlist.setProduct(product);

        Wishlist saved = wishlistRepository.save(wishlist);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistResponse> getWishlistByCustomer(Long customerId) {
        return wishlistRepository.findByCustomer_UserId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void removeFromWishlist(Long wishlistId) {
        Wishlist wishlist = findWishlistItemOrThrow(wishlistId);
        wishlistRepository.delete(wishlist);
    }

    @Override
    @Transactional
    public CartResponse moveToCart(Long wishlistId, MoveToCartRequest request) {
        Wishlist wishlist = findWishlistItemOrThrow(wishlistId);

        com.itv.internship.dto.CartRequest cartRequest = new com.itv.internship.dto.CartRequest();
        cartRequest.setCustomerId(wishlist.getCustomer().getUserId());
        cartRequest.setProductId(wishlist.getProduct().getProductId());
        cartRequest.setQuantity(request.getQuantity() != null ? request.getQuantity() : 1);

        // addToCart() validates stock and throws InsufficientStockException if not enough
        CartResponse cartResponse = cartService.addToCart(cartRequest);

        // only remove from wishlist after successfully added to cart
        wishlistRepository.delete(wishlist);

        return cartResponse;
    }

    private Wishlist findWishlistItemOrThrow(Long wishlistId) {
        return wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found with id: " + wishlistId));
    }

    private WishlistResponse toResponse(Wishlist wishlist) {
        Product product = wishlist.getProduct();
        int available = product.getInventoryCount() == null ? 0 : product.getInventoryCount();
        return new WishlistResponse(
                wishlist.getWishlistId(),
                wishlist.getCustomer().getUserId(),
                wishlist.getCustomer().getFullName(),
                product.getProductId(),
                product.getProductName(),
                product.getPrice(),
                available > 0 && product.getStatus(),
                available,
                wishlist.getCreatedAt()
        );
    }
}