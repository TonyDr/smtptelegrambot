package ru.tony.smtpbot.notification;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;

@Component
public class SubscriptionManager {

    private Map<Long, Set<String>> userSubscriptions = new HashMap<>();
    private Map<String, Set<Long>> emailSubscribers = new HashMap<>();

    public void subscribe(Long chatId, String email) {
        emailSubscribers.merge(email, Set.of(chatId), SubscriptionManager::mergeIds);
        userSubscriptions.merge(chatId, Set.of(email), SubscriptionManager::mergeEmails);
    }

    public Set<String> findSubscriptions(Long i) {
        return userSubscriptions.get(i);
    }

    public void unsubscribe(Long userId, String email) {
        removeFromEmailSubscribers(userId, email);
        removeFromUserSubscriptions(userId, email);
    }

    private void removeFromEmailSubscribers(Long userId, String email) {
        Set<Long> subscribers = emailSubscribers.get(email);
        if (subscribers != null) {
            subscribers.remove(userId);
        }
    }

    private void removeFromUserSubscriptions(Long userId, String email) {
        Set<String> subscriptions = userSubscriptions.get(userId);
        if (subscriptions != null) {
            subscriptions.remove(email);
        }
    }

    private static Set<Long> mergeIds(Set<Long> oldValues, Set<Long> newValues) {
        return concat(oldValues.stream(), newValues.stream()).collect(toSet());
    }

    private static Set<String> mergeEmails(Set<String> oldValues, Set<String> newValues) {
        return concat(oldValues.stream(), newValues.stream()).collect(toSet());
    }

    public Set<Long> findSubscribers(String email) {
        return emailSubscribers.get(email);
    }
}
