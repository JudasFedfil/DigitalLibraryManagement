package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import patterns.observer.IObserver;

public class Reader implements IObserver {
    private final String readerId;
    private final String fullName;
    private final String email;
    private boolean subscribed;
    private final List<String> notifications;

    public Reader(String readerId, String fullName, String email, boolean subscribed) {
        this.readerId = readerId;
        this.fullName = fullName;
        this.email = email;
        this.subscribed = subscribed;
        this.notifications = new ArrayList<>();
    }

    public String getReaderId() { return readerId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public boolean isSubscribed() { return subscribed; }
    public void setSubscribed(boolean subscribed) { this.subscribed = subscribed; }
    public List<String> getNotifications() { return notifications; }

    @Override
    public void update(String notificationMessage) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        notifications.add(String.format("[%s] %s: %s", timestamp, fullName, notificationMessage));
    }
}
