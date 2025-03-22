package com.jitenbr.ecom.customerservice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @Column(name = "tokenid", nullable = false)
    private Integer tokenid;

    @Column(name = "username", length = 50)
    private String username;

    @Column(name = "status", length = 20)
    private String status;

}