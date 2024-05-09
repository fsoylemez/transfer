package com.fms.transfer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Table(name = "CLIENT")
@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private UUID id;

    @NotNull
    @Size(max = 12, message = "National identifier must be 12 characters")
    @Column(name = "SSN", unique = true)
    private String ssn;

    @NotNull
    @Size(max = 255, message = "Name must be at most 255 characters")
    @Column(name = "FIRST_NAME")
    private String firstName;

    @NotNull
    @Size(max = 255, message = "Lastname must be at most 255 characters")
    @Column(name = "LAST_NAME")
    private String lastName;

    @Email(message = "Email should be valid")
    @Column(name = "EMAIL_ADDRESS")
    private String emailAddress;

    @Size(max = 255, message = "Phone number must be at most 255 characters")
    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "CREATION_TIME")
    private LocalDateTime creationTime;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "client")
    private List<Account> accounts;

    @Version
    private int version;
}
