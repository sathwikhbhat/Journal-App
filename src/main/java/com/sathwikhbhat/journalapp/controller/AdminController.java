package com.sathwikhbhat.journalapp.controller;

import com.mongodb.DuplicateKeyException;
import com.sathwikhbhat.journalapp.entity.User;
import com.sathwikhbhat.journalapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Transactional
@RequestMapping("/admin")
@Tag(name = "Admin Controller", description = "Admin operations")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/allUsers")
    @Operation(summary = "Get all users")
    public ResponseEntity<?> getAllUsers() {
        List<User> allUsers = userService.getAllEntries();
        if (!allUsers.isEmpty()) {
            log.info("Users found");
            return new ResponseEntity<>(allUsers, HttpStatus.OK);
        }
        log.error("No users found");
        return new ResponseEntity<>("No users found", HttpStatus.NOT_FOUND);
    }

    @PostMapping("createAdminUser")
    @Operation(summary = "Create a new admin user")
    public ResponseEntity<?> createAdminUser(@Valid @RequestBody User user) {
        try {
            userService.saveNewAdmin(user);
            log.info("User created successfully: {}", user.getUserName());
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (DuplicateKeyException e) {
            log.error("Exception occured: ", e);
            return new ResponseEntity<>("Username already exists", HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Exception occured: ", e);
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}