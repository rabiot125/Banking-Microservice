package com.dtb.customer.dtos;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class CustomerDto {
    private String id;
    private String firstName;
    private String lastName;
    private String otherName;
    private LocalDateTime createdAt;
}
