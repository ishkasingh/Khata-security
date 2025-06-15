package com.example.KhataWebSecurity.Repos;

import com.example.KhataWebSecurity.Model.Customer;
//import com.example.KhataWebSecurity.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {

   // List<Customer> findByUser(User user);

}
