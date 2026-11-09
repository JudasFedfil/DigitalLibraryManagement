package patterns.strategy;

import entity.IDocument;
import java.util.ArrayList;
import java.util.List;

/**
 * Chiến lược tìm kiếm tài liệu theo Tiêu đề.
 */
public class SearchByTitleStrategy implements ISearchStrategy {
    @Override
    public List<IDocument> search(List<IDocument> documents, String keyword) {
        List<IDocument> result = new ArrayList<>();
        String lowerKey = (keyword == null) ? "" : keyword.trim().toLowerCase();
        for (IDocument doc : documents) {
            if (doc.getTitle().toLowerCase().contains(lowerKey)) {
                result.add(doc);
            }
        }
        return result;
    }
}
