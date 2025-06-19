package com.sathwikhbhat.journalapp.controller;

import com.sathwikhbhat.journalapp.entity.User;
import com.sathwikhbhat.journalapp.service.UserDetailsServiceImpl;
import com.sathwikhbhat.journalapp.service.UserService;
import com.sathwikhbhat.journalapp.utility.JwtUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl  userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/healthCheck")
    public String healthCheck() {
        return "OK";
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody User user) {
        try {
            userService.saveNewUser(user);
            log.info("User created successfully: {}", user.getUserName());
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (DuplicateKeyException e) {
            log.error("Exception occurred: {}", e.getMessage());
            return new ResponseEntity<>("Username already exists", HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Exception occurred: {}", e.getMessage());
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody User user) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUserName(), user.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            log.info("User authenticated successfully: {}", user.getUserName());
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } catch (AuthenticationException e) {
            log.error("Authentication failed: {}", e.getMessage());
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
    }
}