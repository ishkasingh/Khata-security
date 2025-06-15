package com.example.KhataWebSecurity.Service;

import com.example.KhataWebSecurity.Model.User;
import com.example.KhataWebSecurity.Repos.UserRepository;
import com.example.KhataWebSecurity.Security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtService;

    // REGISTER USER
    public String register(User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return "Username already taken!";
        }

        save(user);
        return "User registered successfully!";
    }

    // LOGIN and return JWT token
    public String authenticateAndGetToken(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return jwtService.generateToken(userDetails);
    }

    // Save user with encrypted password
    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    // Get user by username (used in controller validation if needed)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
