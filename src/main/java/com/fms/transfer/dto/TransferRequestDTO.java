package com.fms.transfer.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Builder
@Getter
@Setter
public class TransferRequestDTO implements Serializable {

    @NotNull(message = "senderAccountId must be provided")
    private UUID senderAccountId;

    @NotNull(message = "receiverAccountId must be provided")
    private UUID receiverAccountId;

    @Digits(integer=10, fraction=2)
    @Min(value = 1, message = "Transfer amount must be greater than 0")
    @NotNull(message = "transferAmount must be provided")
    private Double transferAmount;

    @Size(min = 3, message = "Transfer currency must be 3 characters long.")
    @Size(max = 3, message = "Transfer currency must be 3 characters long.")
    @NotNull(message = "transferCurrency must be provided")
    private String transferCurrency;
}
