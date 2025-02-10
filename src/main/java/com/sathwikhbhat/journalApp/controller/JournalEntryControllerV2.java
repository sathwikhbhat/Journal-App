package com.sathwikhbhat.journalApp.controller;

import com.sathwikhbhat.journalApp.entity.JournalEntry;
import com.sathwikhbhat.journalApp.service.JournalEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/journal")
public class JournalEntryControllerV2 {

    @Autowired
    private JournalEntryService journalEntryService;

    @GetMapping()
    public List<JournalEntry> getAll() {
        return null;
    }

    @PostMapping
    public boolean createEntry(@RequestBody JournalEntry myEntry) {
        journalEntryService.saveEntry(myEntry);
        return true;
    }

    @GetMapping("id/{myId}")
    public JournalEntry getJournalById(@PathVariable Long myId) {
        return null;
    }

    @DeleteMapping("id/{myId}")
    public JournalEntry deleteEntrybyId(@PathVariable Long myId) {
        return null;
    }

    @PutMapping("/id/{id}")
    public JournalEntry updateJournayById(@PathVariable Long myId, @RequestBody JournalEntry myEntry) {
       return null;
    }

}
