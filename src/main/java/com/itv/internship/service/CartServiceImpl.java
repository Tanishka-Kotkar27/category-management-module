package com.itv.internship.service;

import com.itv.internship.dto.CartRequest;
import com.itv.internship.dto.CartResponse;
import com.itv.internship.dto.CartUpdateRequest;
import com.itv.internship.entity.Cart;
import com.itv.internship.entity.Product;
import com.itv.internship.entity.User;
import com.itv.internship.exception.CartItemNotFoundException;
import com.itv.internship.exception.InsufficientStockException;
import com.itv.internship.exception.ResourceNotFoundException;
import com.itv.internship.repository.CartRepository;
import com.itv.internship.repository.ProductRepository;
import com.itv.internship.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public CartResponse addToCart(CartRequest request) {
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + request.getCustomerId()));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + request.getProductId()));

        var existing = cartRepository.findByCustomer_UserIdAndProduct_ProductId(
                request.getCustomerId(), request.getProductId());

        int newQuantity = request.getQuantity() + existing.map(Cart::getQuantity).orElse(0);

        validateStock(product, newQuantity);

        Cart cart = existing.orElseGet(() -> {
            Cart c = new Cart();
            c.setCustomer(customer);
            c.setProduct(product);
            return c;
        });

        cart.setQuantity(newQuantity);
        cart.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));

        Cart saved = cartRepository.save(cart);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartResponse> getCartByCustomer(Long customerId) {
        return cartRepository.findByCustomer_UserId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartResponse> getAllCarts() {
        return cartRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(Long cartId, CartUpdateRequest request) {
        Cart cart = findCartItemOrThrow(cartId);
        Product product = cart.getProduct();

        validateStock(product, request.getQuantity());

        cart.setQuantity(request.getQuantity());
        cart.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));

        Cart updated = cartRepository.save(cart);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void removeFromCart(Long cartId) {
        Cart cart = findCartItemOrThrow(cartId);
        cartRepository.delete(cart);
    }

    private void validateStock(Product product, int requestedQuantity) {
        int available = product.getInventoryCount() == null ? 0 : product.getInventoryCount();
        if (requestedQuantity > available) {
            throw new InsufficientStockException(
                    "Not enough stock for product '" + product.getProductName() + "'. Available: "
                            + available + ", requested: " + requestedQuantity);
        }
    }

    private Cart findCartItemOrThrow(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found with id: " + cartId));
    }

    private CartResponse toResponse(Cart cart) {
        return new CartResponse(
                cart.getCartId(),
                cart.getCustomer().getUserId(),
                cart.getCustomer().getFullName(),
                cart.getProduct().getProductId(),
                cart.getProduct().getProductName(),
                cart.getProduct().getPrice(),
                cart.getQuantity(),
                cart.getTotalPrice(),
                cart.getCreatedAt(),
                cart.getUpdatedAt()
        );
    }
}