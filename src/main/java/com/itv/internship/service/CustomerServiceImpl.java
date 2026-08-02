package com.itv.internship.service;

import com.itv.internship.dto.CustomerRequest;
import com.itv.internship.dto.CustomerResponse;
import com.itv.internship.entity.User;
import com.itv.internship.exception.DuplicateEmailException;
import com.itv.internship.exception.ResourceNotFoundException;
import com.itv.internship.repository.OrderRepository;
import com.itv.internship.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        if (request.getEmail() != null && !request.getEmail().isBlank()
                && userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateEmailException(
                    "A customer with email '" + request.getEmail() + "' already exists");
        }

        User user = new User();
        applyRequestToEntity(user, request);
        user.setStatus(true);

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        User user = findCustomerOrThrow(id);
        return toResponse(user);
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        User user = findCustomerOrThrow(id);

        boolean emailChanged = request.getEmail() != null
                && !request.getEmail().equalsIgnoreCase(user.getEmail());

        if (emailChanged && userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateEmailException(
                    "A customer with email '" + request.getEmail() + "' already exists");
        }

        applyRequestToEntity(user, request);

        User updated = userRepository.save(user);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deactivateCustomer(Long id) {
        User user = findCustomerOrThrow(id);
        user.setStatus(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public CustomerResponse activateCustomer(Long id) {
        User user = findCustomerOrThrow(id);
        user.setStatus(true);
        return toResponse(userRepository.save(user));
    }

    private void applyRequestToEntity(User user, CustomerRequest request) {
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
    }

    private User findCustomerOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    private CustomerResponse toResponse(User user) {
        long orderCount = orderRepository.countByCustomer_UserId(user.getUserId());
        return new CustomerResponse(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getStatus(),
                orderCount
        );
    }
}