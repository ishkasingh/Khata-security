package com.example.KhataWebSecurity.Controller;

import com.example.KhataWebSecurity.DTO.AuthRequest;
import com.example.KhataWebSecurity.DTO.AuthResponse;
import com.example.KhataWebSecurity.Model.User;
import com.example.KhataWebSecurity.Security.JwtUtil;
import com.example.KhataWebSecurity.Service.MyUserDetailsService;
import com.example.KhataWebSecurity.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        System.out.println("=== LOGIN ATTEMPT ===");
        System.out.println("Username: " + authRequest.getUsername());
        System.out.println("Password length: " + (authRequest.getPassword() != null ? authRequest.getPassword().length() : "null"));

        // Step 1: Check if user exists in database
        try {
            User user = userService.findByUsername(authRequest.getUsername());
            if (user == null) {
                System.out.println("❌ User not found in database");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User not found");
            }
            System.out.println("✅ User found in database");
            System.out.println("Stored password hash: " + user.getPassword().substring(0, 10) + "...");

            // Step 2: Test password manually
            boolean passwordMatches = passwordEncoder.matches(authRequest.getPassword(), user.getPassword());
            System.out.println("Password matches: " + passwordMatches);

            if (!passwordMatches) {
                System.out.println("❌ Password doesn't match");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid password");
            }

        } catch (Exception e) {
            System.out.println("❌ Error checking user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Database error");
        }

        // Step 3: Test UserDetailsService
        try {
            System.out.println("Testing UserDetailsService...");
            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
            System.out.println("✅ UserDetailsService working");
            System.out.println("Authorities: " + userDetails.getAuthorities());
        } catch (Exception e) {
            System.out.println("❌ UserDetailsService error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("UserDetailsService error");
        }

        // Step 4: Test AuthenticationManager
        try {
            System.out.println("Testing AuthenticationManager...");
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );
            System.out.println("✅ Authentication successful!");

        } catch (BadCredentialsException e) {
            System.out.println("❌ BadCredentialsException: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
        } catch (Exception e) {
            System.out.println("❌ Authentication error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication failed");
        }

        // Step 5: Generate token
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        String token = jwtUtil.generateToken(userDetails);
        System.out.println("✅ Token generated successfully");

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        System.out.println("Registering user: " + user.getUsername());

        String result = userService.register(user);

        if (result.equals("Username already taken!")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }

        return ResponseEntity.ok(result);
    }
    @GetMapping("/dashboard")
    public ResponseEntity<String> dashboard() {
        return ResponseEntity.ok("Welcome to the dashboard!");
    }

}
