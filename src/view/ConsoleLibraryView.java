package view;

import controller.LibraryController;
import entity.IDocument;
import entity.LoanRecord;
import entity.Reader;
import java.util.List;
import java.util.Scanner;

public class ConsoleLibraryView implements ILibraryView {
    private LibraryController controller;
    private final Scanner scanner = new Scanner(System.in, "UTF-8");

    @Override
    public void setController(LibraryController controller) {
        this.controller = controller;
    }

    @Override
    public void startView() {
        boolean running = true;
        while (running) {
            System.out.println("\n================ THƯ VIỆN SỐ ================");
            controller.requestSystemStatus();
            System.out.println("1. Danh sách tài liệu");
            System.out.println("2. Thêm tài liệu mới (Thủ thư)");
            System.out.println("3. Tìm kiếm tài liệu (Độc giả / Thủ thư)");
            System.out.println("4. Mượn tài liệu (Độc giả)");
            System.out.println("5. Trả tài liệu (Độc giả)");
            System.out.println("6. Danh sách phiếu mượn");
            System.out.println("7. Độc giả & Thông báo");
            System.out.println("8. Đăng ký / Hủy nhận thông báo (Độc giả)");
            System.out.println("9. Chuyển đổi CSDL (Thủ thư)");
            System.out.println("0. Thoát");
            System.out.print("Chọn chức năng: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    controller.handleLoadAllDocuments();
                    break;
                case "2":
                    promptAddDocument();
                    break;
                case "3":
                    promptSearchDocuments();
                    break;
                case "4":
                    promptBorrowDocument();
                    break;
                case "5":
                    System.out.print("Nhập mã phiếu mượn cần trả: ");
                    controller.handleReturnDocument(scanner.nextLine().trim());
                    break;
                case "6":
                    controller.handleLoadAllLoans();
                    break;
                case "7":
                    controller.handleLoadReadersAndNotifications();
                    break;
                case "8":
                    System.out.print("Nhập mã độc giả (VD: DG01): ");
                    String rId = scanner.nextLine().trim();
                    System.out.print("Đăng ký nhận thông báo? (y/n): ");
                    boolean sub = scanner.nextLine().trim().equalsIgnoreCase("y");
                    controller.handleUpdateSubscription(rId, sub);
                    break;
                case "9":
                    controller.handleToggleDatabase();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    showError("Lựa chọn không hợp lệ!");
            }
        }
    }

    private void promptAddDocument() {
        System.out.print("Loại (EBOOK / AUDIOBOOK / JOURNAL): ");
        String type = scanner.nextLine().trim();
        System.out.print("Mã tài liệu: ");
        String id = scanner.nextLine().trim();
        System.out.print("Tiêu đề: ");
        String title = scanner.nextLine().trim();
        System.out.print("Tác giả: ");
        String author = scanner.nextLine().trim();
        System.out.print("Thể loại: ");
        String category = scanner.nextLine().trim();
        System.out.print("Chi phí: ");
        double cost = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Thông tin thêm: ");
        String extra = scanner.nextLine().trim();
        controller.handleAddDocument(type, id, title, author, category, cost, extra);
    }

    private void promptSearchDocuments() {
        System.out.print("Tìm theo (TITLE / AUTHOR / CATEGORY): ");
        String strategy = scanner.nextLine().trim();
        System.out.print("Từ khóa: ");
        String keyword = scanner.nextLine().trim();
        controller.handleSearchDocuments(strategy, keyword);
    }

    private void promptBorrowDocument() {
        System.out.print("Mã độc giả: ");
        String readerId = scanner.nextLine().trim();
        System.out.print("Mã tài liệu: ");
        String docId = scanner.nextLine().trim();
        System.out.print("Bảo vệ bản quyền (y/n): ");
        boolean drm = scanner.nextLine().trim().equalsIgnoreCase("y");
        System.out.print("Tải ngoại tuyến (y/n): ");
        boolean offline = scanner.nextLine().trim().equalsIgnoreCase("y");
        controller.handleBorrowDocument(readerId, docId, drm, offline);
    }

    @Override
    public void showDocuments(List<IDocument> documents) {
        System.out.println("\n--- DANH SÁCH TÀI LIỆU ---");
        for (IDocument doc : documents) {
            System.out.printf("[%s] %s - %s | %s | %,.0f đ | %s\n",
                    doc.getId(), doc.getDocumentType(), doc.getTitle(), doc.getAuthor(), doc.getAccessCost(), doc.getDescription());
        }
    }

    @Override
    public void showLoans(List<LoanRecord> loans) {
        System.out.println("\n--- DANH SÁCH PHIẾU MƯỢN ---");
        for (LoanRecord loan : loans) {
            System.out.printf("[%s] Độc giả: %s | %s | %s | Tổng: %,.0f đ\n",
                    loan.getLoanId(), loan.getReader().getFullName(),
                    loan.getBorrowedDocument().getDescription(), loan.getStatusText(), loan.getTotalCost());
        }
    }

    @Override
    public void showReadersAndNotifications(List<Reader> readers) {
        System.out.println("\n--- ĐỘC GIẢ & THÔNG BÁO ---");
        for (Reader r : readers) {
            System.out.printf("[%s] %s (%s) - Nhận TB: %s\n",
                    r.getReaderId(), r.getFullName(), r.getEmail(), r.isSubscribed() ? "Có" : "Không");
            for (String msg : r.getNotifications()) {
                System.out.println("  • " + msg);
            }
        }
    }

    @Override
    public void showSystemStatus(String status) {
        System.out.println(status);
    }

    @Override
    public void showMessage(String message) {
        System.out.println("[Thông báo] " + message);
    }

    @Override
    public void showError(String errorMessage) {
        System.out.println("[Lỗi] " + errorMessage);
    }
}
