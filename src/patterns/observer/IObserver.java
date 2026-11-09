package patterns.observer;

/**
 * [BEHAVIORAL PATTERN 2 - OBSERVER PATTERN]:
 * Giao diện người quan sát (Độc giả đăng ký nhận thông báo).
 */
public interface IObserver {
    void update(String notificationMessage);
}
