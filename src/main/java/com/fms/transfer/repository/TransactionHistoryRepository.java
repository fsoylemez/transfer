package com.fms.transfer.repository;

import com.fms.transfer.entity.TransactionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionHistoryRepository extends PagingAndSortingRepository<TransactionHistory, UUID>, CrudRepository<TransactionHistory, UUID> {

    @Query("select h from TransactionHistory h JOIN FETCH h.client where h.account.id =:accountId")
    Page<TransactionHistory> findAllByAccount(@Param("accountId") UUID accountId, Pageable pageable);
}