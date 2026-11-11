package entity;

public class EBook implements IDocument {
    private final String id;
    private final String title;
    private final String author;
    private final String category;
    private final double baseCost;
    private final int pageCount;
    private final String fileFormat;

    public EBook(String id, String title, String author, String category, double baseCost, int pageCount, String fileFormat) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.baseCost = baseCost;
        this.pageCount = pageCount;
        this.fileFormat = fileFormat;
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
    public String getDocumentType() { return "Sách điện tử"; }

    @Override
    public double getAccessCost() { return baseCost; }

    public int getPageCount() { return pageCount; }

    public String getFileFormat() { return fileFormat; }

    @Override
    public String getDescription() {
        return String.format("%s (%d trang, %s)", title, pageCount, fileFormat);
    }
}
