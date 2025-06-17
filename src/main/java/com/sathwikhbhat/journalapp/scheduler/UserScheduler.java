package com.sathwikhbhat.journalapp.scheduler;

import com.sathwikhbhat.journalapp.entity.JournalEntry;
import com.sathwikhbhat.journalapp.entity.User;
import com.sathwikhbhat.journalapp.enums.Sentiment;
import com.sathwikhbhat.journalapp.repository.UserRepositoryImpl;
import com.sathwikhbhat.journalapp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class UserScheduler {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepositoryImpl userRepository;

    @Scheduled(cron = "0 0 9 * * SUN")
    public void fetchUsersAndSendEmails() {
        List<User> users = userRepository.getUsersForSA();

        for (User user : users) {
            List<JournalEntry> journalEntries = user.getJournalEntries();
            List<Sentiment> sentiments = journalEntries.stream()
                    .filter(
                            x -> x.getDate()
                                    .isAfter(LocalDateTime.now().minusDays(7)))
                    .map(JournalEntry::getSentiment)
                    .toList();

            Map<Sentiment, Integer> sentimentCount = new EnumMap<>(Sentiment.class);

            for (Sentiment sentiment : sentiments) {
                if (sentiment != null) {
                    sentimentCount.put(sentiment, sentimentCount.getOrDefault(sentiment, 0) + 1);
                }
                Sentiment mostFrequentSentiment = null;
                int maxCount = 0;
                for (Map.Entry<Sentiment, Integer> entry : sentimentCount.entrySet()) {
                    if (entry.getValue() > maxCount) {
                        maxCount = entry.getValue();
                        mostFrequentSentiment = entry.getKey();
                    }
                }
                if (mostFrequentSentiment != null) {
                    emailService.sendEmail(user.getEmail(), "Sentiment Analysis Report of last 7 days", mostFrequentSentiment.toString());
                }
            }
        }
    }

}
