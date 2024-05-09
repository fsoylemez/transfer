package com.fms.transfer.repository;

import com.fms.transfer.entity.Account;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends CrudRepository<Account, UUID> {

    @Query("SELECT a FROM Account a JOIN FETCH a.client WHERE a.client.id= :clientId ORDER BY a.creationTime desc")
    List<Account> findAllByClient(@Param("clientId") UUID clientId);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT a FROM Account a JOIN FETCH a.client WHERE a.id = :accountId")
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    Optional<Account> findForTransfer(@Param("accountId") UUID accountId);

    @Modifying
    @Transactional(propagation = Propagation.MANDATORY)
    @Query("UPDATE Account a SET a.balance = :newBalance WHERE a.id = :accountId")
    void updateBalance(@Param("accountId") UUID accountId, @Param("newBalance") double newBalance);
}
