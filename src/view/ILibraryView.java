package view;

import controller.LibraryController;
import entity.IDocument;
import entity.LoanRecord;
import entity.Reader;
import java.util.List;

public interface ILibraryView {
    void setController(LibraryController controller);
    void startView();
    void showDocuments(List<IDocument> documents);
    void showLoans(List<LoanRecord> loans);
    void showReadersAndNotifications(List<Reader> readers);
    void showSystemStatus(String status);
    void showMessage(String message);
    void showError(String errorMessage);
}
