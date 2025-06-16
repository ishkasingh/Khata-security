package com.example.KhataWebSecurity.Controller;

import com.example.KhataWebSecurity.DTO.AuthRequest;
import com.example.KhataWebSecurity.DTO.AuthResponse;
import com.example.KhataWebSecurity.Model.User;
import com.example.KhataWebSecurity.Security.JwtUtil;
import com.example.KhataWebSecurity.Security.MyUserDetails;
import com.example.KhataWebSecurity.Service.MyUserDetailsService;
import com.example.KhataWebSecurity.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

    // 🔐 Login - Generates JWT with role
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

            // Add role as a claim in JWT
            User domainUser = ((MyUserDetails) userDetails).getUser();

            String role = domainUser.getRole().name(); // e.g., ROLE_ADMIN
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", role);

            String token = jwtUtil.generateToken(userDetails, claims);

// ⬇️ Add role to response
            return ResponseEntity.ok(new AuthResponse(token, role));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Authentication failed: " + e.getMessage());
        }
    }

    // 🧾 Registration
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        System.out.println("Registering user: " + user.getUsername());

        String result = userService.register(user);

        if (result.equals("Username already taken!")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }

        return ResponseEntity.ok(result);
    }

    // 🏠 Accessible to all logged-in users
    @GetMapping("/dashboard")
    public ResponseEntity<String> dashboard() {
        return ResponseEntity.ok("Welcome to the dashboard!");
    }

    // 🔐 Only accessible to users with ADMIN role
    @GetMapping("/admin-area")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminArea() {
        return ResponseEntity.ok("Access granted to ADMIN only");
    }
}
