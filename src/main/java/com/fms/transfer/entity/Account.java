package com.fms.transfer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Setter
@Getter
@Table(name = "ACCOUNT")
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLIENT_ID")
    private Client client;

    @NotNull
    @Size(min = 3, max = 255, message = "Account name must be between 3 and 255 characters")
    @Column(name = "ACCOUNT_NAME")
    private String accountName;

    @NotNull
    @Size(min = 3, max = 3, message = "Currency must be exactly 3 characters")
    @Column(name = "CURRENCY")
    private String currency;

    @NotNull
    @Column(name = "BALANCE")
    private Double balance;

    @Column(name = "CREATION_TIME")
    private LocalDateTime creationTime;

    @Version
    private int version;
}
