package com.sathwikhbhat.journalapp.controller;

import com.sathwikhbhat.journalapp.entity.User;
import com.sathwikhbhat.journalapp.repository.UserRepository;
import com.sathwikhbhat.journalapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "User Controller", description = "Operations pertaining to user data")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @PutMapping
    @Operation(summary = "Update user data")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        try {
            User userInDB = userService.findByUserName(userName);
            if (userInDB == null) {
                log.warn("User not found: {}", userName);
                return new ResponseEntity<>("User not found",HttpStatus.NOT_FOUND);
            }
            userInDB.setUserName(!user.getUserName().isEmpty() ? user.getUserName() : userInDB.getUserName());
            userInDB.setPassword(!user.getPassword().isEmpty() ? user.getPassword() : userInDB.getPassword());
            userService.saveNewUser(userInDB);
            log.info("User updated successfully: {}", userName);
            return new ResponseEntity<>("User updated",HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error updating user: {}", userName, e);
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @DeleteMapping
    @Operation(summary = "Delete user data")
    public ResponseEntity<?> deleteUserById() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        try {
            userRepository.deleteByUserName(authentication.getName());
            log.info("User data deleted successfully: {}", userName);
            return new ResponseEntity<>("User data deleted", HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error updating user: {}", userName, e);
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    @Operation(summary = "Get user data")
    public ResponseEntity<?> getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        if (user == null) {
            log.warn("User not found: {}", userName);
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}