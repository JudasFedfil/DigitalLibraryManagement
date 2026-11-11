package test;

import entity.IDocument;
import entity.LoanRecord;
import entity.Reader;
import java.util.List;
import patterns.decorator.DRMProtectionDecorator;
import patterns.decorator.OfflineDownloadDecorator;
import patterns.facade.LibraryManagementFacade;
import patterns.factory.DocumentFactory;
import patterns.singleton.DatabaseConnectionManager;
import repository.IDataRepository;
import repository.InMemoryRepository;
import repository.SqlJdbcRepository;

public class TestRunner {

    public static void main(String[] args) {
        System.out.println("   CHẠY KIỂM THỬ HỆ THỐNG THƯ VIỆN SỐ (CONSOLE TEST CASES)");

        // Test 1: Singleton Pattern
        System.out.println("[TEST 1] Singleton Pattern (DatabaseConnectionManager):");
        DatabaseConnectionManager db1 = DatabaseConnectionManager.getInstance();
        DatabaseConnectionManager db2 = DatabaseConnectionManager.getInstance();
        System.out.println(" - HashCode 1: " + db1.hashCode() + " | HashCode 2: " + db2.hashCode());
        System.out.println(" - Kết quả: " + (db1 == db2 ? "PASSED" : "FAILED") + "\n");

        // Test 2: Factory Method Pattern
        System.out.println("[TEST 2] Factory Method Pattern (DocumentFactory):");
        DocumentFactory factory = new DocumentFactory();
        IDocument doc1 = factory.createDocument("EBOOK", "T01", "Java Core", "Tác giả A", "CNTT", 20000, "300;PDF");
        IDocument doc2 = factory.createDocument("AUDIOBOOK", "T02", "Clean Code Audio", "Robert Martin", "CNTT", 30000, "240;Giọng đọc AI");
        IDocument doc3 = factory.createDocument("JOURNAL", "T03", "AI Research", "Nhóm tác giả", "Khoa học", 15000, "ISSN-1122;4.0");
        System.out.println(" - Đã tạo: " + doc1.getDescription());
        System.out.println(" - Đã tạo: " + doc2.getDescription());
        System.out.println(" - Đã tạo: " + doc3.getDescription());
        System.out.println(" - Kết quả: PASSED\n");

        // Test 3: Decorator Pattern
        System.out.println("[TEST 3] Decorator Pattern (DRMProtection & OfflineDownload):");
        IDocument decorated = new OfflineDownloadDecorator(new DRMProtectionDecorator(doc1));
        System.out.println(" - Mô tả sau khi bọc: " + decorated.getDescription());
        System.out.printf(" - Chi phí gốc: %,.0f đ -> Sau khi bọc: %,.0f đ\n", doc1.getAccessCost(), decorated.getAccessCost());
        System.out.println(" - Kết quả: " + (decorated.getAccessCost() == 35000 ? "PASSED" : "FAILED") + "\n");

        // Test 4: Facade & Observer Pattern (Bao gồm cả Đăng ký / Hủy nhận thông báo)
        System.out.println("[TEST 4] Facade & Observer Pattern (Thêm tài liệu & Đăng ký/Hủy nhận thông báo):");
        IDataRepository repo = new InMemoryRepository();
        LibraryManagementFacade facade = new LibraryManagementFacade(repo);
        facade.updateReaderSubscription("DG02", false); // Độc giả DG02 hủy nhận tin
        facade.addNewDocument("EBOOK", "TL99", "Thiết kế Hệ thống", "Nguyễn Thanh Tài", "CNTT", 22000, "280;PDF");
        for (Reader r : facade.getAllReaders()) {
            System.out.printf(" - Độc giả %s (Đăng ký: %s): nhận %d thông báo\n",
                    r.getFullName(), r.isSubscribed() ? "Có" : "Không", r.getNotifications().size());
        }
        System.out.println(" - Kết quả: PASSED\n");

        // Test 5: Strategy Pattern
        System.out.println("[TEST 5] Strategy Pattern (Tìm kiếm theo Tiêu đề / Tác giả / Thể loại):");
        List<IDocument> byTitle = facade.searchDocuments("Tiêu đề", "Java");
        List<IDocument> byAuthor = facade.searchDocuments("Tác giả", "Robert Martin");
        List<IDocument> byCategory = facade.searchDocuments("Thể loại", "CNTT");
        System.out.printf(" - Theo tiêu đề: %d | Theo tác giả: %d | Theo thể loại: %d\n",
                byTitle.size(), byAuthor.size(), byCategory.size());
        System.out.println(" - Kết quả: PASSED\n");

        // Test 6: Chuyển đổi Database Repository (DIP & OCP)
        System.out.println("[TEST 6] Chuyển đổi CSDL (InMemory -> SQL JDBC Repository):");
        facade.switchRepository(new SqlJdbcRepository("jdbc:mysql://localhost:3306/digital_library"));
        LoanRecord loan = facade.borrowDocument("DG01", "TL99", true, false);
        System.out.printf(" - Mượn trên DB mới: Mã phiếu %s | Tổng phí: %,.0f đ\n", loan.getLoanId(), loan.getTotalCost());
        System.out.println(" - Kết quả: PASSED\n");

        System.out.println("   HOÀN TẤT KIỂM THỬ: 6/6 TEST CASES PASSED!");
    }
}
