package entity;

public interface IDocument {
    String getId();
    String getTitle();
    String getAuthor();
    String getCategory();
    String getDocumentType();
    double getAccessCost();
    String getDescription();
}
