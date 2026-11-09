package patterns.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Trung tâm thông báo tự động (ConcreteSubject trong Observer Pattern).
 * Khi thư viện có tài liệu mới, NotificationCenter tự động gửi thông báo tới các độc giả đã đăng ký.
 */
public class NotificationCenter implements ISubject {
    private final List<IObserver> observers = new ArrayList<>();

    @Override
    public void attach(IObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void detach(IObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (IObserver observer : observers) {
            observer.update(message);
        }
    }

    public int getSubscriberCount() {
        return observers.size();
    }
}
