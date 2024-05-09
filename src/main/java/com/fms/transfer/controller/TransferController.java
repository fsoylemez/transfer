package com.fms.transfer.controller;

import com.fms.transfer.dto.TransferRequestDTO;
import com.fms.transfer.dto.TransferResponseDTO;
import com.fms.transfer.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/transfer")
public class TransferController {

    @Autowired
    private TransferService transferService;

    @PostMapping
    public ResponseEntity<TransferResponseDTO> transfer(
            @Valid
            @RequestBody
                    TransferRequestDTO requestDto) {

        TransferResponseDTO transferResponse = transferService.transfer(requestDto);

        return ResponseEntity.ok().body(transferResponse);
    }
}
