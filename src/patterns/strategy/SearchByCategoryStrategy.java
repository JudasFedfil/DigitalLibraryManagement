package patterns.strategy;

import entity.IDocument;
import java.util.ArrayList;
import java.util.List;

/**
 * Chiến lược tìm kiếm tài liệu theo Thể loại.
 */
public class SearchByCategoryStrategy implements ISearchStrategy {
    @Override
    public List<IDocument> search(List<IDocument> documents, String keyword) {
        List<IDocument> result = new ArrayList<>();
        String lowerKey = (keyword == null) ? "" : keyword.trim().toLowerCase();
        for (IDocument doc : documents) {
            if (doc.getCategory().toLowerCase().contains(lowerKey)) {
                result.add(doc);
            }
        }
        return result;
    }
}
