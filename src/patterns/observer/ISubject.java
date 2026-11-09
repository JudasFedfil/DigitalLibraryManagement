package patterns.observer;

public interface ISubject {
    void attach(IObserver observer);
    void detach(IObserver observer);
    void notifyObservers(String message);
}
