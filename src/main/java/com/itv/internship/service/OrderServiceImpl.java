package com.itv.internship.service;

import com.itv.internship.dto.*;
import com.itv.internship.entity.*;
import com.itv.internship.exception.InsufficientStockException;
import com.itv.internship.exception.InvalidOrderStatusTransitionException;
import com.itv.internship.exception.ResourceNotFoundException;
import com.itv.internship.repository.OrderRepository;
import com.itv.internship.repository.ProductRepository;
import com.itv.internship.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        User customer = findOrCreateCustomer(request.getCustomerName(), request.getCustomerEmail());

        Order order = new Order();
        order.setCustomer(customer);
        order.setShippingAddress(request.getShippingAddress());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setStatus(true);

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: " + itemReq.getProductId()));

            if (product.getInventoryCount() == null || product.getInventoryCount() < itemReq.getQuantity()) {
                throw new InsufficientStockException(
                        "Not enough stock for product '" + product.getProductName() + "'. Available: "
                                + (product.getInventoryCount() == null ? 0 : product.getInventoryCount())
                                + ", requested: " + itemReq.getQuantity());
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(product.getPrice());
            order.getOrderItems().add(item);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));

            product.setInventoryCount(product.getInventoryCount() - itemReq.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders(OrderStatus statusFilter) {
        List<Order> orders = (statusFilter != null)
                ? orderRepository.findByOrderStatus(statusFilter)
                : orderRepository.findAll();

        return orders.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = findOrderOrThrow(id);
        return toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatusUpdateRequest request) {
        Order order = findOrderOrThrow(id);

        OrderStatus current = order.getOrderStatus();
        OrderStatus target = request.getOrderStatus();

        if (current == OrderStatus.CANCELLED || current == OrderStatus.DELIVERED) {
            throw new InvalidOrderStatusTransitionException(
                    "Cannot change status of an order that is already " + current);
        }

        order.setOrderStatus(target);
        Order updated = orderRepository.save(order);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void cancelOrder(Long id) {
        Order order = findOrderOrThrow(id);

        if (order.getOrderStatus() == OrderStatus.SHIPPED || order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new InvalidOrderStatusTransitionException(
                    "This order has already been shipped and can no longer be cancelled");
        }

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setInventoryCount(product.getInventoryCount() + item.getQuantity());
            productRepository.save(product);
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setStatus(false);
        orderRepository.save(order);
    }

    private User findOrCreateCustomer(String customerName, String customerEmail) {
        String trimmedName = customerName.trim();

        if (customerEmail != null && !customerEmail.isBlank()) {
            var existingByEmail = userRepository.findByEmailIgnoreCase(customerEmail.trim());
            if (existingByEmail.isPresent()) {
                return existingByEmail.get();
            }
        }

        String[] parts = trimmedName.split("\\s+", 2);
        String firstName = parts[0];
        String lastName = parts.length > 1 ? parts[1] : null;

        User newUser = new User();
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(customerEmail != null && !customerEmail.isBlank() ? customerEmail.trim() : null);
        newUser.setStatus(true);
        return userRepository.save(newUser);
    }

    private Order findOrderOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getOrderItemId(),
                        item.getProduct().getProductId(),
                        item.getProduct().getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .toList();

        return new OrderResponse(
                order.getOrderId(),
                order.getCustomer().getUserId(),
                order.getCustomer().getFullName(),
                order.getTotalAmount(),
                order.getOrderStatus(),
                order.getShippingAddress(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getStatus(),
                items
        );
    }
}