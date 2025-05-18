package com.dtb.customer.controllers;

import com.dtb.customer.dtos.*;
import com.dtb.customer.exceptions.ExceptionsController;
import com.dtb.customer.exceptions.RecordNotFoundException;
import com.dtb.customer.services.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CustomerControllerTest {
    @InjectMocks
    private CustomerController customerController;
    private MockMvc mockMvc;
    @Mock
    private CustomerService customerService;
    private ObjectMapper objectMapper;
    private CustomerDto sampleDto;
    private CreateCustomerDto createDto;
    private CustomerUpdateDto updateDto;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .setControllerAdvice(new ExceptionsController())
                .build();
        objectMapper = new ObjectMapper();

        sampleDto = new CustomerDto();
        sampleDto.setId("1");
        sampleDto.setFirstName("John");
        sampleDto.setLastName("Doe");
        sampleDto.setOtherName("Smith");
        sampleDto.setCreatedAt(LocalDateTime.now());

        createDto = new CreateCustomerDto();
        createDto.setFirstName("John");
        createDto.setLastName("Doe");
        createDto.setOtherName("Smith");

        updateDto = new CustomerUpdateDto();
        updateDto.setFirstName("Johnny");
    }
    @Test
    void testGetCustomerById_Success() throws Exception {
        when(customerService.findById(1L)).thenReturn(sampleDto);

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")));
    }
    @Test
    void testGetCustomerById_NotFound() throws Exception {
        when(customerService.findById(1L))
                .thenThrow(new RecordNotFoundException("Customer not found"));

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isNotFound());
    }
    @Test
    void testGetAllCustomers_Success() throws Exception {
        CustomerListResponseDTO responseDTO = new CustomerListResponseDTO();
        responseDTO.setCustomers(Collections.singletonList(sampleDto));
        responseDTO.setPage(0);
        responseDTO.setSize(1);
        responseDTO.setTotalItems(1L);
        responseDTO.setTotalPages(1);

        when(customerService.findAllCustomers(any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(responseDTO);

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customers", hasSize(1)))
                .andExpect(jsonPath("$.customers[0].firstName", is("John")));
    }

    @Test
    void testCreateCustomer_Success() throws Exception {
        when(customerService.createCustomer(any(CreateCustomerDto.class)))
                .thenReturn(sampleDto);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("John")));
    }
    @Test
    void testUpdateCustomer_Success() throws Exception {
        sampleDto.setFirstName("Johnny");
        when(customerService.updateCustomer(eq(1L), any(CustomerUpdateDto.class)))
                .thenReturn(sampleDto);

        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Johnny")));
    }
    @Test
    void testDeleteCustomer_Success() throws Exception {
        doNothing().when(customerService).deleteCustomer(1L);

        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNoContent());
    }
    @Test
    void testDeleteCustomer_NotFound() throws Exception {
        doThrow(new RecordNotFoundException("Customer not found"))
                .when(customerService).deleteCustomer(1L);

        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNotFound());
    }

}
