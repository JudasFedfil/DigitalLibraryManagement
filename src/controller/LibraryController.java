package controller;

import entity.IDocument;
import entity.LoanRecord;
import entity.Reader;
import java.util.List;
import patterns.facade.LibraryManagementFacade;
import repository.IDataRepository;
import repository.InMemoryRepository;
import repository.SqlJdbcRepository;
import view.ILibraryView;

/**
 * Tầng Điều khiển (Controller trong mô hình MVC).
 * Tuân thủ SRP & DIP: Điều phối các sự kiện từ View xuống Facade và cập nhật lại View.
 */
public class LibraryController {
    private final LibraryManagementFacade facade;
    private final ILibraryView view;
    private boolean usingSqlRepo = false;

    public LibraryController(LibraryManagementFacade facade, ILibraryView view) {
        this.facade = facade;
        this.view = view;
        this.view.setController(this);
    }

    public void startApplication() {
        view.startView();
    }

    public void refreshAllViews() {
        view.showSystemStatus(facade.getSystemStatusSummary());
        view.showDocuments(facade.getAllDocuments());
        view.showLoans(facade.getAllLoans());
        view.showReadersAndNotifications(facade.getAllReaders());
    }

    public void requestSystemStatus() {
        view.showSystemStatus(facade.getSystemStatusSummary());
    }

    public void handleLoadAllDocuments() {
        view.showDocuments(facade.getAllDocuments());
    }

    public void handleLoadAllLoans() {
        view.showLoans(facade.getAllLoans());
    }

    public void handleLoadReadersAndNotifications() {
        view.showReadersAndNotifications(facade.getAllReaders());
    }

    public void handleRegisterReader(String readerId, String fullName, String email, boolean subscribe) {
        try {
            Reader r = facade.registerReader(readerId, fullName, email, subscribe);
            refreshAllViews();
            view.showMessage("Đã thêm độc giả: " + r.getFullName());
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    public void handleUpdateSubscription(String readerId, boolean subscribe) {
        try {
            Reader r = facade.updateReaderSubscription(readerId, subscribe);
            refreshAllViews();
            view.showMessage(subscribe
                    ? "Đã bật nhận thông báo tự động cho độc giả: " + r.getFullName()
                    : "Đã hủy nhận thông báo tự động cho độc giả: " + r.getFullName());
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    public void handleAddDocument(String type, String id, String title, String author,
                                  String category, double baseCost, String extraMetadata) {
        try {
            IDocument created = facade.addNewDocument(type, id, title, author, category, baseCost, extraMetadata);
            refreshAllViews();
            view.showMessage("Đã thêm tài liệu: " + created.getTitle());
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    public void handleDeleteDocument(String docId) {
        try {
            boolean ok = facade.deleteDocument(docId);
            refreshAllViews();
            if (ok) {
                view.showMessage("Đã xóa tài liệu: " + docId);
            } else {
                view.showError("Không tìm thấy tài liệu: " + docId);
            }
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    public void handleSearchDocuments(String strategyType, String keyword) {
        try {
            List<IDocument> results = facade.searchDocuments(strategyType, keyword);
            view.showDocuments(results);
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    public void handleBorrowDocument(String readerId, String docId, boolean enableDRM, boolean enableOffline) {
        try {
            LoanRecord loan = facade.borrowDocument(readerId, docId, enableDRM, enableOffline);
            refreshAllViews();
            view.showMessage(String.format("Mượn thành công phiếu [%s]\nTổng chi phí: %,.0f VNĐ",
                    loan.getLoanId(), loan.getTotalCost()));
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    public void handleReturnDocument(String loanId) {
        try {
            LoanRecord returned = facade.returnDocument(loanId);
            refreshAllViews();
            view.showMessage("Đã trả phiếu mượn: " + returned.getLoanId());
        } catch (Exception e) {
            view.showError(e.getMessage());
        }
    }

    public void handleToggleDatabase() {
        IDataRepository newRepo;
        if (!usingSqlRepo) {
            newRepo = new SqlJdbcRepository("jdbc:mysql://localhost:3306/digital_library");
            usingSqlRepo = true;
        } else {
            newRepo = new InMemoryRepository();
            usingSqlRepo = false;
        }
        facade.switchRepository(newRepo);
        refreshAllViews();
        view.showMessage("Đã chuyển sang: " + newRepo.getRepositoryName());
    }
}
