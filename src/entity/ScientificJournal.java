package entity;

/**
 * Thực thể Tạp chí Khoa học (ScientificJournal).
 * Tuân thủ SRP & LSP.
 */
public class ScientificJournal implements IDocument {
    private final String id;
    private final String title;
    private final String author;
    private final String category;
    private final double baseCost;
    private final String issn;
    private final double impactFactor;

    public ScientificJournal(String id, String title, String author, String category, double baseCost, String issn, double impactFactor) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.baseCost = baseCost;
        this.issn = issn;
        this.impactFactor = impactFactor;
    }

    @Override
    public String getId() { return id; }

    @Override
    public String getTitle() { return title; }

    @Override
    public String getAuthor() { return author; }

    @Override
    public String getCategory() { return category; }

    @Override
    public String getDocumentType() { return "Tạp chí"; }

    @Override
    public double getAccessCost() { return baseCost; }

    public String getIssn() { return issn; }

    public double getImpactFactor() { return impactFactor; }

    @Override
    public String getDescription() {
        return String.format("%s (ISSN: %s, IF: %.2f)", title, issn, impactFactor);
    }
}
