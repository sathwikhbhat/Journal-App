package com.sathwikhbhat.journalapp.controller;

import com.sathwikhbhat.journalapp.entity.JournalEntry;
import com.sathwikhbhat.journalapp.entity.User;
import com.sathwikhbhat.journalapp.service.JournalEntryService;
import com.sathwikhbhat.journalapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/journalEntries")
@Tag(name = "Journal Entry Controller", description = "Operations pertaining to journal entries")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(summary = "Get all journal entries of the user")
    public ResponseEntity<?> getAllJournalEntriesOfUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        List<JournalEntry> allEntries = user.getJournalEntries();
        if (!allEntries.isEmpty()) {
            return new ResponseEntity<>(allEntries, HttpStatus.OK);
        }
        return new ResponseEntity<>("No entries found", HttpStatus.NOT_FOUND);
    }

    @PostMapping
    @Operation(summary = "Create a new journal entry")
    public ResponseEntity<?> createEntry(@Valid @RequestBody JournalEntry myEntry) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        try {
            journalEntryService.saveEntry(myEntry, userName);
            log.info("Journal entry created successfully: {}", userName);
            return new ResponseEntity<>("Journal entry created", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("id/{myId}")
    @Operation(summary = "Get a journal entry by id")
    public ResponseEntity<?> getJournalEntryById(@PathVariable ObjectId myId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        List<JournalEntry> journalEntries = user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).toList();
        if (!journalEntries.isEmpty()) {
            JournalEntry myEntry = journalEntryService.findById(myId);
            if (myEntry == null) {
                return new ResponseEntity<>("Journal entry is empty", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(myEntry, HttpStatus.OK);
        }
        log.error("Journal entry not found: {}", userName);
        return new ResponseEntity<>("Journal entry not found", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("id/{myId}")
    @Operation(summary = "Delete a journal entry by id")
    public ResponseEntity<JournalEntry> deleteJournalEntryById(@PathVariable ObjectId myId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        try {
            boolean removed = journalEntryService.deleteById(myId, userName);
            if (removed) {
                log.info("Journal entry deleted successfully: {}", userName);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                log.error("Journal entry not found: {}", userName);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("id/{myId}")
@Operation(summary = "Update a journal entry by id")
    public ResponseEntity<?> updateJournalById(@PathVariable ObjectId myId, @RequestBody JournalEntry newEntry) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        List<JournalEntry> journalEntries = user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).toList();
        if (!journalEntries.isEmpty()) {
            JournalEntry old = journalEntryService.findById(myId);
            if (old != null) {
                old.setTitle(!newEntry.getTitle().isEmpty() ? newEntry.getTitle() : old.getTitle());
                old.setContent(newEntry.getContent() != null && !newEntry.getContent().isEmpty() ? newEntry.getContent() : old.getContent());
                journalEntryService.saveEntry(old);
                return new ResponseEntity<>(old, HttpStatus.OK);
            }
        }
        log.error("Journal entry not found: {}", userName);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}