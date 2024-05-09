package com.fms.transfer.dto;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class TransferResponseDTO implements Serializable {

    private boolean success;

    private String message;
}
