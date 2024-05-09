package com.fms.transfer.service;

import com.fms.transfer.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public boolean existsById(UUID clientId) {
        return clientRepository.existsById(clientId);
    }
}
