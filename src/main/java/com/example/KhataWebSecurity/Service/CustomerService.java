package com.example.KhataWebSecurity.Service;

import com.example.KhataWebSecurity.Model.Customer;
import com.example.KhataWebSecurity.Repos.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepo;

    // ✅ 1. Get all customers
    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }

    // ✅ 2. Get a customer by ID
    public Customer getCustomerById(Long id) {
        return customerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
    }

    // ✅ 3. Add a new customer
    public Customer addCustomer(Customer customer) {
        // Optional: Add validation logic here
        return customerRepo.save(customer);
    }

    // ✅ 4. Update an existing customer
    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        Customer existingCustomer = getCustomerById(id);

        existingCustomer.setName(updatedCustomer.getName());
        existingCustomer.setPhone(updatedCustomer.getPhone());
        existingCustomer.setBalance(updatedCustomer.getBalance());

        return customerRepo.save(existingCustomer);
    }

    // ✅ 5. Delete customer by ID
    public void deleteCustomer(Long id) {
        if (!customerRepo.existsById(id)) {
            throw new RuntimeException("Customer not found with ID: " + id);
        }
        customerRepo.deleteById(id);
    }
}
