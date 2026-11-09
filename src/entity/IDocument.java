package entity;

/**
 * Interface đại diện cho Tài liệu số.
 * - Tuân thủ ISP (Interface Segregation Principle): Giao diện tinh gọn, chỉ chứa các phương thức chung.
 * - Tuân thủ LSP (Liskov Substitution Principle): Các lớp con (EBook, AudioBook, ScientificJournal)
 *   và các lớp Decorator đều thay thế được cho IDocument.
 */
public interface IDocument {
    String getId();
    String getTitle();
    String getAuthor();
    String getCategory();
    String getDocumentType();
    double getAccessCost();
    String getDescription();
}
