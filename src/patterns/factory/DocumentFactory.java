package patterns.factory;

import entity.AudioBook;
import entity.EBook;
import entity.IDocument;
import entity.ScientificJournal;

/**
 * [CREATIONAL PATTERN 2 - FACTORY METHOD PATTERN]:
 * Đóng gói quá trình khởi tạo các đối tượng tài liệu số khác nhau (EBook, AudioBook, ScientificJournal).
 * - Tuân thủ OCP (Open/Closed Principle): Dễ dàng mở rộng thêm loại tài liệu mới mà không làm ảnh hưởng tầng gọi.
 * - Tuân thủ DIP (Dependency Inversion Principle): Trả về kiểu trừu tượng IDocument.
 */
public class DocumentFactory {

    public IDocument createDocument(String type, String id, String title, String author,
                                    String category, double baseCost, String extraMetadata) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Loại tài liệu không được để trống!");
        }

        String normalizedType = type.trim().toUpperCase();
        String[] parts = (extraMetadata != null && extraMetadata.contains(";"))
                ? extraMetadata.split(";", 2)
                : new String[]{extraMetadata != null ? extraMetadata : "", ""};

        switch (normalizedType) {
            case "EBOOK":
            case "SÁCH ĐIỆN TỬ":
                int pages = parseIntSafe(parts[0], 250);
                String format = parts[1].isEmpty() ? "PDF" : parts[1].trim();
                return new EBook(id, title, author, category, baseCost, pages, format);

            case "AUDIOBOOK":
            case "SÁCH NÓI":
                double duration = parseDoubleSafe(parts[0], 120.0);
                String narrator = parts[1].isEmpty() ? "Mặc định" : parts[1].trim();
                return new AudioBook(id, title, author, category, baseCost, duration, narrator);

            case "JOURNAL":
            case "TẠP CHÍ":
                String issn = parts[0].isEmpty() ? "ISSN-0000" : parts[0].trim();
                double impactFactor = parseDoubleSafe(parts[1], 2.0);
                return new ScientificJournal(id, title, author, category, baseCost, issn, impactFactor);

            default:
                throw new IllegalArgumentException("Loại tài liệu không hợp lệ: " + type);
        }
    }

    private int parseIntSafe(String val, int defaultVal) {
        try {
            return Integer.parseInt(val.trim());
        } catch (Exception e) {
            return defaultVal;
        }
    }

    private double parseDoubleSafe(String val, double defaultVal) {
        try {
            return Double.parseDouble(val.trim());
        } catch (Exception e) {
            return defaultVal;
        }
    }
}
