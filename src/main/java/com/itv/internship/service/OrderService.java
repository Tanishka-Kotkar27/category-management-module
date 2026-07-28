package com.itv.internship.service;

import com.itv.internship.dto.OrderRequest;
import com.itv.internship.dto.OrderResponse;
import com.itv.internship.dto.OrderStatusUpdateRequest;
import com.itv.internship.entity.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    List<OrderResponse> getAllOrders(OrderStatus statusFilter);
    OrderResponse getOrderById(Long id);
    OrderResponse updateOrderStatus(Long id, OrderStatusUpdateRequest request);
    void cancelOrder(Long id);
}