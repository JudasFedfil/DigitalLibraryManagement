package patterns.strategy;

import entity.IDocument;
import java.util.List;

public class SearchContext {
    private ISearchStrategy strategy;

    public SearchContext() {
        this.strategy = new SearchByTitleStrategy();
    }

    public void setStrategy(ISearchStrategy strategy) {
        this.strategy = strategy;
    }

    public List<IDocument> executeSearch(List<IDocument> documents, String keyword) {
        if (strategy == null) {
            strategy = new SearchByTitleStrategy();
        }
        return strategy.search(documents, keyword);
    }
}
