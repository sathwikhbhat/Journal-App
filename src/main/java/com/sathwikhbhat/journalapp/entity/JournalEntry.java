package com.sathwikhbhat.journalapp.entity;

import com.sathwikhbhat.journalapp.enums.Sentiment;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "journalEntries")
public class JournalEntry {
    @Id
    private ObjectId id;

    @NonNull
    @NotBlank(message = "Title is required")
    private String title;

    @NonNull
    @NotBlank(message = "Content is required")
    private String content;
    private LocalDateTime date;
    private Sentiment sentiment;
}