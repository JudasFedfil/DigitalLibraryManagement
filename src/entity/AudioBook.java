package entity;

public class AudioBook implements IDocument {
    private final String id;
    private final String title;
    private final String author;
    private final String category;
    private final double baseCost;
    private final double durationMinutes;
    private final String narrator;

    public AudioBook(String id, String title, String author, String category, double baseCost, double durationMinutes, String narrator) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.baseCost = baseCost;
        this.durationMinutes = durationMinutes;
        this.narrator = narrator;
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
    public String getDocumentType() { return "Sách nói"; }

    @Override
    public double getAccessCost() { return baseCost; }

    public double getDurationMinutes() { return durationMinutes; }

    public String getNarrator() { return narrator; }

    @Override
    public String getDescription() {
        return String.format("%s (%.0f phút, Giọng đọc: %s)", title, durationMinutes, narrator);
    }
}
