package com.dtb.customer.repository;

import com.dtb.customer.models.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("SELECT c FROM Customer c WHERE " +
            "(:name IS NULL OR CONCAT(c.firstName, ' ', c.lastName, ' ', COALESCE(c.otherName, '')) LIKE %:name%) AND " +
            "( cast(:startDate as date)  IS NULL OR c.createdAt >= :startDate) AND " +
            "( cast(:endDate as date) IS NULL OR c.createdAt <= :endDate)")
    Page<Customer> findCustomersByNameOrDateCreated( @Param("name") String name, @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,Pageable pageable);
}
