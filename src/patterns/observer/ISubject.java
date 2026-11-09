package patterns.observer;

/**
 * Giao diện chủ thể phát thông báo trong Observer Pattern.
 */
public interface ISubject {
    void attach(IObserver observer);
    void detach(IObserver observer);
    void notifyObservers(String message);
}
