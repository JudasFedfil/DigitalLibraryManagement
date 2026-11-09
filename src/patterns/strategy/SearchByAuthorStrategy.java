package patterns.strategy;

import entity.IDocument;
import java.util.ArrayList;
import java.util.List;

public class SearchByAuthorStrategy implements ISearchStrategy {
    @Override
    public List<IDocument> search(List<IDocument> documents, String keyword) {
        List<IDocument> result = new ArrayList<>();
        String lowerKey = (keyword == null) ? "" : keyword.trim().toLowerCase();
        for (IDocument doc : documents) {
            if (doc.getAuthor().toLowerCase().contains(lowerKey)) {
                result.add(doc);
            }
        }
        return result;
    }
}
