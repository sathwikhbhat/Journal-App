package com.sathwikhbhat.journalApp.service;

import com.sathwikhbhat.journalApp.entity.User;
import com.sathwikhbhat.journalApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveEntry(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("USER"));
        userRepository.save(user);
    }

//    public List<User> getAllEntries() {
//        return userRepository.findAll();
//    }

//    public User findById(ObjectId id) {
//        return userRepository.findById(id).orElse(null);
//    }

//    public void deleteById(ObjectId id) {
//        try {
//            userRepository.deleteById(id);
//        } catch (Exception e) {
//            //log.error(e.getMessage());
//        }
//    }

    public User findByUserName(String username) {
        return userRepository.findByUserName(username);
    }
}
