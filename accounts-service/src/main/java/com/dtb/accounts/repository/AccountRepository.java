package com.dtb.accounts.repository;

import com.dtb.accounts.dtos.AccountDto;
import com.dtb.accounts.models.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a WHERE " +
            "(:iban IS NULL OR a.iban LIKE %:iban%) AND " +
            "(:bicSwift IS NULL OR a.bicSwift LIKE %:bicSwift%)")
    Page<Account> findWithFilters(@Param("iban") String iban, @Param("bicSwift") String bicSwift, Pageable pageable);
}
