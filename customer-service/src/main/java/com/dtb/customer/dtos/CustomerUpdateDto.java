package com.dtb.customer.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerUpdateDto {
    private String firstName;
    private String lastName;
    private String otherName;
}
