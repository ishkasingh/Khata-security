package com.example.KhataWebSecurity.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String email;
    private String password;
//    private Boolean isDeleted;
//    @ManyToMany(cascade = {CascadeType.PERSIST},fetch=FetchType.EAGER)
//    private List<Role> roles;
}
