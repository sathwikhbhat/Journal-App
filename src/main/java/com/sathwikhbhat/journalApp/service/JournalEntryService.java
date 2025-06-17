package com.sathwikhbhat.journalApp.service;

import com.sathwikhbhat.journalApp.entity.JournalEntry;
import com.sathwikhbhat.journalApp.entity.User;
import com.sathwikhbhat.journalApp.repository.JournalEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public void saveEntry(JournalEntry journalEntry, String userName) {
        try {
            User user = userService.findByUserName(userName);
            journalEntry.setDate(LocalDateTime.now());
            JournalEntry saved = journalEntryRepository.save(journalEntry);
            user.getJournalEntries().add(saved);
            userService.saveUser(user);
        } catch (Exception e) {
            log.error("Exception occurred: {}", e.getMessage());
        }
    }

    public void saveEntry(JournalEntry journalEntry) {
        try {
            journalEntryRepository.save(journalEntry);
            log.info("Journal entry saved successfully");
        } catch (Exception e) {
            log.error("Exception occurred: {}", e.getMessage());
        }
    }

    public JournalEntry findById(ObjectId id) {
        return journalEntryRepository.findById(id).orElse(null);
    }

    @Transactional
    public boolean deleteById(ObjectId id, String userName) {
        boolean removed = false;
        try {
            User user = userService.findByUserName(userName);
            removed = user.getJournalEntries().removeIf(x -> x.getId().equals(id));
            if (!removed) {
                log.error("Journal entry not found: {}", userName);
            } else {
                log.info("Journal entry deleted successfully: {}", userName);
                userService.saveUser(user);
                journalEntryRepository.deleteById(id);
            }
        } catch (Exception e) {
            log.error("Exception occurred: {}", e.getMessage());
        }
        return removed;
    }
}
