package com.sathwikhbhat.journalApp.repository;

import com.sathwikhbhat.journalApp.entity.JournalEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JournalEntryRepository extends MongoRepository<JournalEntry, String> {
}
