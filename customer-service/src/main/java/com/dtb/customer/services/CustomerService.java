package com.dtb.customer.services;

import com.dtb.customer.dtos.CreateCustomerDto;
import com.dtb.customer.dtos.CustomerDto;
import com.dtb.customer.dtos.CustomerListResponseDTO;
import com.dtb.customer.dtos.CustomerUpdateDto;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface CustomerService {
    CustomerListResponseDTO findAllCustomers(String name,
                                             LocalDateTime startDate,
                                             LocalDateTime endDate,
                                             int page,
                                             int size);

    //    PagedResponse<CustomerDTO> searchCustomers(String name, String createdDateStart, String createdDateEnd, int page, int size);
    CustomerDto findById(Long id);
    CustomerDto createCustomer(CreateCustomerDto createCustomerDto);
    @Transactional
    CustomerDto updateCustomer(Long id, CustomerUpdateDto customerDto);
    @Transactional
    void deleteCustomer(Long id);
}
