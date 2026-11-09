package patterns.strategy;

import entity.IDocument;
import java.util.List;

public interface ISearchStrategy {
    List<IDocument> search(List<IDocument> documents, String keyword);
}
