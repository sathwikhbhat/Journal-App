package com.sathwikhbhat.journalApp.controller;

import com.mongodb.DuplicateKeyException;
import com.sathwikhbhat.journalApp.entity.User;
import com.sathwikhbhat.journalApp.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Transactional
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/allUsers")
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
