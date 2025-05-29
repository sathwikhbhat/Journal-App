package com.sathwikhbhat.journalApp.controller;

import com.sathwikhbhat.journalApp.entity.User;
import com.sathwikhbhat.journalApp.repository.UserRepository;
import com.sathwikhbhat.journalApp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        try {
            User userInDB = userService.findByUserName(userName);
            if (userInDB == null) {
                log.warn("User not found: {}", userName);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            userInDB.setUserName(!user.getUserName().isEmpty() ? user.getUserName() : userInDB.getUserName());
            userInDB.setPassword(!user.getPassword().isEmpty() ? user.getPassword() : userInDB.getPassword());
            userService.saveEntry(userInDB);
            log.info("User updated successfully: {}", userName);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error updating user: {}", userName, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @DeleteMapping
    public ResponseEntity<User> deleteUserById() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        try {
            userRepository.deleteByUserName(authentication.getName());
            log.info("User data deleted successfully: {}", userName);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error updating user: {}", userName, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}