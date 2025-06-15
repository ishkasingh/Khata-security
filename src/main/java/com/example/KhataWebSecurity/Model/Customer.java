package com.example.KhataWebSecurity.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phone;
    private Double balance;

//    // Many customers are managed by one user
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
}
