package com.sathwikhbhat.journalApp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import com.sathwikhbhat.journalApp.entity.User;
import com.sathwikhbhat.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllEntries() {
        List<User> allEntries = userService.getAllEntries();
        if (!allEntries.isEmpty() && allEntries != null) {
            return new ResponseEntity<>(allEntries, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            userService.saveEntry(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (DuplicateKeyException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>("Username already exists: ", HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{userName}")
    public ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable String userName) {
        try {
            User userInDB = userService.findByUserName(userName);
            if (userInDB != null) {
                userInDB.setUserName(user.getUserName() != null && !user.getUserName().isEmpty() ? user.getUserName() : userInDB.getUserName());
                userInDB.setPassword(user.getPassword() != null && !user.getPassword().isEmpty() ? user.getPassword() : userInDB.getPassword());
                userService.saveEntry(userInDB);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
