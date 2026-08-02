package com.itv.internship.service;

import com.itv.internship.dto.CustomerRequest;
import com.itv.internship.dto.CustomerResponse;

import java.util.List;

public interface CustomerService {
    CustomerResponse createCustomer(CustomerRequest request);
    List<CustomerResponse> getAllCustomers();
    CustomerResponse getCustomerById(Long id);
    CustomerResponse updateCustomer(Long id, CustomerRequest request);
    void deactivateCustomer(Long id);
    CustomerResponse activateCustomer(Long id);
}