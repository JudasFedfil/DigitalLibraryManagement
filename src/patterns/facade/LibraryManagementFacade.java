package patterns.facade;

import entity.IDocument;
import entity.LoanRecord;
import entity.Reader;
import java.util.List;
import patterns.decorator.DRMProtectionDecorator;
import patterns.decorator.OfflineDownloadDecorator;
import patterns.factory.DocumentFactory;
import patterns.observer.NotificationCenter;
import patterns.singleton.DatabaseConnectionManager;
import patterns.strategy.SearchByAuthorStrategy;
import patterns.strategy.SearchByCategoryStrategy;
import patterns.strategy.SearchByTitleStrategy;
import patterns.strategy.SearchContext;
import repository.IDataRepository;

/**
 * [STRUCTURAL PATTERN 2 - FACADE PATTERN]:
 * Cung cấp giao diện điều phối đơn giản, thống nhất cho tầng Controller,
 * ẩn đi sự phức tạp của việc kết hợp DocumentFactory, IDataRepository,
 * DocumentDecorator, SearchContext, NotificationCenter và DatabaseConnectionManager.
 */
public class LibraryManagementFacade {
    private IDataRepository repository;
    private final DocumentFactory documentFactory;
    private final NotificationCenter notificationCenter;
    private final SearchContext searchContext;
    private int loanCounter = 1;

    public LibraryManagementFacade(IDataRepository repository) {
        this.repository = repository;
        this.documentFactory = new DocumentFactory();
        this.notificationCenter = new NotificationCenter();
        this.searchContext = new SearchContext();
        seedInitialData();
    }

    // Chuyển đổi nguồn lưu trữ dữ liệu lúc runtime (Tuân thủ DIP & OCP)
    public void switchRepository(IDataRepository newRepository) {
        List<IDocument> currentDocs = this.repository.findAllDocuments();
        List<Reader> currentReaders = this.repository.findAllReaders();
        List<LoanRecord> currentLoans = this.repository.findAllLoans();

        this.repository = newRepository;
        for (IDocument d : currentDocs) this.repository.saveDocument(d);
        for (Reader r : currentReaders) this.repository.saveReader(r);
        for (LoanRecord l : currentLoans) this.repository.saveLoan(l);
    }

    public IDataRepository getRepository() {
        return repository;
    }

    public String getSystemStatusSummary() {
        return DatabaseConnectionManager.getInstance().getConnectionInfo()
                + " | Nguồn lưu trữ: " + repository.getRepositoryName();
    }

    // Đăng ký độc giả & gắn vào danh sách nhận thông báo (Observer Pattern)
    public Reader registerReader(String readerId, String fullName, String email, boolean subscribeNotifications) {
        if (readerId == null || readerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã độc giả không được để trống!");
        }
        Reader reader = new Reader(readerId.trim(), fullName, email, subscribeNotifications);
        repository.saveReader(reader);
        if (subscribeNotifications) {
            notificationCenter.attach(reader);
        }
        return reader;
    }

    // Thêm tài liệu mới (Sử dụng Factory Method + Repository + Observer Pattern)
    public IDocument addNewDocument(String type, String id, String title, String author,
                                    String category, double baseCost, String extraMetadata) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã tài liệu không được để trống!");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Tiêu đề tài liệu không được để trống!");
        }
        IDocument newDoc = documentFactory.createDocument(type, id.trim(), title.trim(), author, category, baseCost, extraMetadata);
        repository.saveDocument(newDoc);
        notificationCenter.notifyObservers(
                String.format("Tài liệu mới cập nhật: %s - Tác giả: %s", newDoc.getTitle(), newDoc.getAuthor()));
        return newDoc;
    }

    public boolean deleteDocument(String docId) {
        return repository.deleteDocument(docId);
    }

    // Mượn tài liệu kèm tùy chọn dịch vụ bổ sung (Sử dụng Decorator Pattern)
    public LoanRecord borrowDocument(String readerId, String docId, boolean enableDRM, boolean enableOffline) {
        Reader reader = repository.findReaderById(readerId);
        if (reader == null) {
            throw new IllegalArgumentException("Không tìm thấy độc giả: " + readerId);
        }

        IDocument doc = repository.findDocumentById(docId);
        if (doc == null) {
            throw new IllegalArgumentException("Không tìm thấy tài liệu: " + docId);
        }

        IDocument customizedDoc = doc;
        if (enableDRM) {
            customizedDoc = new DRMProtectionDecorator(customizedDoc);
        }
        if (enableOffline) {
            customizedDoc = new OfflineDownloadDecorator(customizedDoc);
        }

        String loanId = String.format("PM%03d", loanCounter++);
        LoanRecord loanRecord = new LoanRecord(loanId, reader, customizedDoc);
        repository.saveLoan(loanRecord);

        reader.update(String.format("Đã mượn phiếu [%s]: %s (Tổng phí: %,.0f đ)",
                loanId, customizedDoc.getDescription(), customizedDoc.getAccessCost()));
        return loanRecord;
    }

    public LoanRecord returnDocument(String loanId) {
        LoanRecord loan = repository.findLoanById(loanId);
        if (loan == null) {
            throw new IllegalArgumentException("Không tìm thấy mã phiếu mượn: " + loanId);
        }
        if (loan.isReturned()) {
            throw new IllegalStateException("Phiếu mượn " + loanId + " đã được trả trước đó!");
        }
        loan.markAsReturned();
        repository.saveLoan(loan);
        loan.getReader().update("Đã hoàn tất trả tài liệu cho phiếu [" + loanId + "].");
        return loan;
    }

    // Tìm kiếm tài liệu (Sử dụng Strategy Pattern)
    public List<IDocument> searchDocuments(String strategyType, String keyword) {
        if ("AUTHOR".equalsIgnoreCase(strategyType) || "Tác giả".equalsIgnoreCase(strategyType)) {
            searchContext.setStrategy(new SearchByAuthorStrategy());
        } else if ("CATEGORY".equalsIgnoreCase(strategyType) || "Thể loại".equalsIgnoreCase(strategyType)) {
            searchContext.setStrategy(new SearchByCategoryStrategy());
        } else {
            searchContext.setStrategy(new SearchByTitleStrategy());
        }
        return searchContext.executeSearch(repository.findAllDocuments(), keyword);
    }

    public List<IDocument> getAllDocuments() { return repository.findAllDocuments(); }
    public List<Reader> getAllReaders() { return repository.findAllReaders(); }
    public List<LoanRecord> getAllLoans() { return repository.findAllLoans(); }

    private void seedInitialData() {
        registerReader("DG01", "Nguyễn Thanh Tài", "tai.nguyen@email.com", true);
        registerReader("DG02", "Trần Minh Khoa", "khoa.tran@email.com", true);
        registerReader("DG03", "Lê Hoàng Nam", "nam.le@email.com", false);

        addNewDocument("EBOOK", "TL01", "Lập trình Hướng đối tượng Java",
                "Nguyễn Văn A", "Công nghệ thông tin", 20000, "320;PDF");
        addNewDocument("EBOOK", "TL02", "Cấu trúc Dữ liệu và Giải thuật",
                "Trần Văn B", "Khoa học máy tính", 25000, "410;EPUB");
        addNewDocument("AUDIOBOOK", "TL03", "Tư duy Thiết kế Phần mềm",
                "Robert Martin", "Kỹ năng phần mềm", 30000, "360;Minh Quân");
        addNewDocument("JOURNAL", "TL04", "Ứng dụng Trí tuệ Nhân tạo trong Y tế",
                "Viện Nghiên cứu CNTT", "Khoa học công nghệ", 15000, "ISSN-2588;3.8");

        borrowDocument("DG01", "TL01", true, true);
    }
}
