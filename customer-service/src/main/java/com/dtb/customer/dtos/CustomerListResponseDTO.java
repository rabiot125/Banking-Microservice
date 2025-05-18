package com.dtb.customer.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CustomerListResponseDTO {
    private List<CustomerDto> customers;
    private int page;
    private int size;
    private long totalItems;
    private int totalPages;
}
