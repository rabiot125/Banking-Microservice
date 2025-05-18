package com.dtb.customer.services;

import com.dtb.customer.dtos.*;
import com.dtb.customer.exceptions.RecordNotFoundException;
import com.dtb.customer.models.Customer;
import com.dtb.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {
    @Mock
    private CustomerRepository customerRepository;
    @InjectMocks
    private CustomerServiceImpl customerService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    private Customer getSampleCustomer(Long id) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setOtherName("Middle");
        customer.setCreatedAt(LocalDateTime.now());
        return customer;
    }

    @Test
    void findAllCustomers_returnsPagedCustomerList() {
        // Arrange
        Customer sample = getSampleCustomer(1L);
        Page<Customer> page = new PageImpl<>(List.of(sample));

        when(customerRepository.findCustomersByNameOrDateCreated(
                eq("John"), any(), any(), any()))
                .thenReturn(page);

        // Act
        CustomerListResponseDTO response = customerService.findAllCustomers("John", null, null, 0, 10);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getCustomers()).hasSize(1);
        assertThat(response.getTotalItems()).isEqualTo(1);
        assertThat(response.getTotalPages()).isEqualTo(1);
        verify(customerRepository).findCustomersByNameOrDateCreated(eq("John"), any(), any(), any());
    }

    @Test
    void findById_existingId_returnsCustomer() {
        Customer customer = getSampleCustomer(1L);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerDto dto = customerService.findById(1L);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo("1");
        assertThat(dto.getFirstName()).isEqualTo("John");
        verify(customerRepository).findById(1L);
    }

    @Test
    void findById_nonExistingId_throwsRecordNotFoundException() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findById(999L))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessageContaining("Customer not found with id: 999");

        verify(customerRepository).findById(999L);
    }

    @Test
    void createCustomer_validDto_returnsSavedCustomer() {
        CreateCustomerDto dto = new CreateCustomerDto();
        dto.setFirstName("Alice");
        dto.setLastName("Smith");
        dto.setOtherName("L");

        Customer saved = getSampleCustomer(2L);
        saved.setFirstName("Alice");
        saved.setLastName("Smith");
        saved.setOtherName("L");

        when(customerRepository.save(any())).thenReturn(saved);

        CustomerDto result = customerService.createCustomer(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("2");
        assertThat(result.getFirstName()).isEqualTo("Alice");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void updateCustomer_existingId_updatesAndReturnsCustomer() {
        Customer existing = getSampleCustomer(1L);
        CustomerUpdateDto updateDto = new CustomerUpdateDto();
        updateDto.setFirstName("Updated");

        Customer updated = getSampleCustomer(1L);
        updated.setFirstName("Updated");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.save(any())).thenReturn(updated);

        CustomerDto result = customerService.updateCustomer(1L, updateDto);

        assertThat(result.getFirstName()).isEqualTo("Updated");
        verify(customerRepository).save(any());
    }

    @Test
    void updateCustomer_nonExistingId_throwsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        CustomerUpdateDto updateDto = new CustomerUpdateDto();
        updateDto.setFirstName("Update");

        assertThatThrownBy(() -> customerService.updateCustomer(1L, updateDto))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessageContaining("Customer not found with id: 1");
    }

    @Test
    void deleteCustomer_existingId_deletesCustomer() {
        when(customerRepository.existsById(1L)).thenReturn(true);

        customerService.deleteCustomer(1L);

        verify(customerRepository).deleteById(1L);
    }

    @Test
    void deleteCustomer_nonExistingId_throwsException() {
        when(customerRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> customerService.deleteCustomer(1L))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessageContaining("Customer not found with id: 1");

        verify(customerRepository, never()).deleteById(any());
    }
}
