package patterns.observer;

import java.util.ArrayList;
import java.util.List;

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
