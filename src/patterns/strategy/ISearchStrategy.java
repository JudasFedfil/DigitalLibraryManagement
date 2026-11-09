package patterns.strategy;

import entity.IDocument;
import java.util.List;

/**
 * [BEHAVIORAL PATTERN 1 - STRATEGY PATTERN]:
 * Giao diện định nghĩa họ các thuật toán tìm kiếm tài liệu.
 * Tuân thủ ISP, OCP và DIP.
 */
public interface ISearchStrategy {
    List<IDocument> search(List<IDocument> documents, String keyword);
}
