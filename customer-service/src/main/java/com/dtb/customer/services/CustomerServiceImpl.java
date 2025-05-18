package com.dtb.customer.services;

import com.dtb.customer.dtos.CustomerListResponseDTO;
import com.dtb.customer.dtos.CustomerUpdateDto;
import com.dtb.customer.exceptions.RecordNotFoundException;
import com.dtb.customer.dtos.CreateCustomerDto;
import com.dtb.customer.dtos.CustomerDto;
import com.dtb.customer.models.Customer;
import com.dtb.customer.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    @Override
    public CustomerListResponseDTO findAllCustomers(String name,
                                                    LocalDateTime startDate,
                                                    LocalDateTime endDate,
                                                    int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Customer> customerPage = customerRepository.findCustomersByNameOrDateCreated(name, startDate, endDate, pageable);
        List<CustomerDto> customerDtos = customerPage.getContent().stream()
                .map(this::mapEntitytoDto).collect(Collectors.toList());

        CustomerListResponseDTO responseDTO = new CustomerListResponseDTO();
        responseDTO.setCustomers(customerDtos);
        responseDTO.setPage(customerPage.getNumber());
        responseDTO.setSize(customerPage.getSize());
        responseDTO.setTotalItems(customerPage.getTotalElements());
        responseDTO.setTotalPages(customerPage.getTotalPages());
        return responseDTO;
    }

    @Override
    public CustomerDto findById(Long id) {
        return customerRepository.findById(id)
                .map(this::mapEntitytoDto)
                .orElseThrow(() -> new RecordNotFoundException("Customer not found with id: " + id));
    }

    @Override
    public CustomerDto createCustomer(CreateCustomerDto createCustomerDto) {
        Customer customer = new Customer();
        customer.setFirstName(createCustomerDto.getFirstName());
        customer.setLastName(createCustomerDto.getLastName());
        customer.setOtherName(createCustomerDto.getOtherName());
        customer.setCreatedAt(LocalDateTime.now());

        Customer savedCust = customerRepository.save(customer);

        return mapEntitytoDto(savedCust);
    }

    @Override
    @Transactional
    public CustomerDto updateCustomer(Long id, CustomerUpdateDto customerDto) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Customer not found with id: " + id));
        if (customerDto.getFirstName() != null) {
            existingCustomer.setFirstName(customerDto.getFirstName());
        }
        if (customerDto.getLastName() != null) {
            existingCustomer.setLastName(customerDto.getLastName());
        }
        if (customerDto.getOtherName() != null) {
            existingCustomer.setOtherName(customerDto.getOtherName());
        }

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return mapEntitytoDto(updatedCustomer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RecordNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    private CustomerDto mapEntitytoDto(Customer customer) {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(customer.getId().toString());
        customerDto.setFirstName(customer.getFirstName());
        customerDto.setLastName(customer.getLastName());
        customerDto.setOtherName(customer.getOtherName());
        customerDto.setCreatedAt(customer.getCreatedAt());
        return customerDto;
    }
}
