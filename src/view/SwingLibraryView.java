package view;

import controller.LibraryController;
import entity.IDocument;
import entity.LoanRecord;
import entity.Reader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class SwingLibraryView extends JFrame implements ILibraryView {
    private LibraryController controller;

    // Dữ liệu đệm phục vụ lọc hiển thị theo vai trò
    private List<Reader> cachedReaders = new ArrayList<>();
    private List<LoanRecord> cachedLoans = new ArrayList<>();
    private boolean updatingCombos = false;

    // Thanh chuyển đổi Vai trò (Actor Switcher)
    private JComboBox<String> roleCombo;
    private JLabel activeReaderLabel;
    private JComboBox<String> activeReaderCombo;
    private JLabel statusLabel;
    private JButton switchDbBtn;

    // TabPane & Các thành phần ẩn/hiện theo quyền
    private JTabbedPane tabbedPane;
    private JButton deleteDocBtn;
    private JPanel librarianAddDocPanel;
    private JPanel librarianAddReaderPanel;
    private JPanel readerSubscriptionPanel;
    private JLabel readerSubStatusLabel;
    private JButton toggleSubBtn;
    private JScrollPane readerTableScroll;

    // Bảng dữ liệu
    private DefaultTableModel docTableModel;
    private JTable docTable;
    private DefaultTableModel loanTableModel;
    private JTable loanTable;
    private DefaultTableModel readerTableModel;
    private JTable readerTable;
    private JTextArea notificationArea;

    // Tìm kiếm (UC1)
    private JComboBox<String> searchTypeCombo;
    private JTextField searchKeywordField;

    // Thêm tài liệu (UC5 - Thủ thư)
    private JComboBox<String> docTypeCombo;
    private JTextField docIdField, docTitleField, docAuthorField, docCategoryField, docCostField, docExtraField;

    // Mượn / Trả tài liệu (UC2, UC3)
    private JComboBox<String> borrowReaderCombo;
    private JComboBox<String> borrowDocCombo;
    private JCheckBox drmCheckBox, offlineCheckBox;

    // Thêm độc giả (UC6 - Thủ thư)
    private JTextField readerIdField, readerNameField, readerEmailField;
    private JCheckBox readerSubCheck;

    public SwingLibraryView() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        setTitle("Thư viện số");
        setSize(1080, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. THANH TIÊU ĐỀ & CHỌN VAI TRÒ (THỦ THƯ / ĐỘC GIẢ)
        JPanel topPanel = new JPanel(new BorderLayout(10, 5));
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(0, 0, 6, 0)
        ));

        JPanel leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JLabel titleLabel = new JLabel("THƯ VIỆN SỐ");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        roleCombo = new JComboBox<>(new String[]{
                "Thủ thư / Quản trị viên",
                "Độc giả (Sinh viên / Giảng viên)"
        });
        roleCombo.setFont(new Font("SansSerif", Font.BOLD, 12));

        activeReaderLabel = new JLabel("Tài khoản Độc giả:");
        activeReaderCombo = new JComboBox<>();
        activeReaderLabel.setVisible(false);
        activeReaderCombo.setVisible(false);

        roleCombo.addActionListener(e -> applyRolePermissions());
        activeReaderCombo.addActionListener(e -> {
            if (!updatingCombos) {
                syncViewsForCurrentRole();
            }
        });

        leftTop.add(titleLabel);
        leftTop.add(new JLabel("  |   Vai trò:"));
        leftTop.add(roleCombo);
        leftTop.add(activeReaderLabel);
        leftTop.add(activeReaderCombo);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        statusLabel = new JLabel("Đang tải...");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        switchDbBtn = new JButton("Chuyển đổi CSDL");
        switchDbBtn.addActionListener(e -> controller.handleToggleDatabase());

        rightTop.add(statusLabel);
        rightTop.add(switchDbBtn);

        topPanel.add(leftTop, BorderLayout.WEST);
        topPanel.add(rightTop, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 2. 3 TAB CHỨC NĂNG CHÍNH
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));

        tabbedPane.addTab("Quản lý tài liệu", createDocumentsTab());
        tabbedPane.addTab("Mượn / Trả tài liệu", createLoansTab());
        tabbedPane.addTab("Độc giả & Thông báo", createReadersTab());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private JPanel createDocumentsTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(6, 6, 6, 6));

        // Thanh tìm kiếm (Cả Thủ thư & Độc giả đều dùng - UC1)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchPanel.setBorder(new TitledBorder("Tìm kiếm tài liệu"));
        searchTypeCombo = new JComboBox<>(new String[]{"Tiêu đề", "Tác giả", "Thể loại"});
        searchKeywordField = new JTextField(22);
        JButton searchBtn = new JButton("Tìm kiếm");
        JButton showAllBtn = new JButton("Làm mới");
        deleteDocBtn = new JButton("Xóa tài liệu");

        searchBtn.addActionListener(e -> controller.handleSearchDocuments(
                (String) searchTypeCombo.getSelectedItem(), searchKeywordField.getText()));
        showAllBtn.addActionListener(e -> {
            searchKeywordField.setText("");
            controller.handleLoadAllDocuments();
        });
        deleteDocBtn.addActionListener(e -> {
            int row = docTable.getSelectedRow();
            if (row < 0) {
                showError("Vui lòng chọn một tài liệu trên bảng để xóa!");
                return;
            }
            String docId = (String) docTableModel.getValueAt(row, 0);
            controller.handleDeleteDocument(docId);
        });

        searchPanel.add(new JLabel("Tìm theo:"));
        searchPanel.add(searchTypeCombo);
        searchPanel.add(new JLabel("Từ khóa:"));
        searchPanel.add(searchKeywordField);
        searchPanel.add(searchBtn);
        searchPanel.add(showAllBtn);
        searchPanel.add(deleteDocBtn);
        panel.add(searchPanel, BorderLayout.NORTH);

        // Bảng danh sách tài liệu
        String[] cols = {"Mã TL", "Loại", "Tiêu đề", "Tác giả", "Thể loại", "Chi phí (VNĐ)", "Thông tin chi tiết"};
        docTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        docTable = new JTable(docTableModel);
        docTable.setRowHeight(24);
        panel.add(new JScrollPane(docTable), BorderLayout.CENTER);

        // Form thêm tài liệu (Chỉ hiển thị cho Thủ thư - UC5)
        librarianAddDocPanel = new JPanel(new GridLayout(2, 4, 8, 8));
        librarianAddDocPanel.setBorder(new TitledBorder("Thêm tài liệu mới (Dành cho Thủ thư)"));

        docTypeCombo = new JComboBox<>(new String[]{"EBOOK", "AUDIOBOOK", "JOURNAL"});
        docIdField = new JTextField("TL05");
        docTitleField = new JTextField();
        docAuthorField = new JTextField();
        docCategoryField = new JTextField();
        docCostField = new JTextField("20000");
        docExtraField = new JTextField("250;PDF");

        JButton addBtn = new JButton("Thêm tài liệu");
        addBtn.addActionListener(e -> {
            try {
                double cost = Double.parseDouble(docCostField.getText().trim());
                controller.handleAddDocument(
                        (String) docTypeCombo.getSelectedItem(),
                        docIdField.getText(),
                        docTitleField.getText(),
                        docAuthorField.getText(),
                        docCategoryField.getText(),
                        cost,
                        docExtraField.getText()
                );
            } catch (NumberFormatException ex) {
                showError("Chi phí phải là số hợp lệ!");
            }
        });

        librarianAddDocPanel.add(createField("Loại tài liệu:", docTypeCombo));
        librarianAddDocPanel.add(createField("Mã tài liệu:", docIdField));
        librarianAddDocPanel.add(createField("Tiêu đề:", docTitleField));
        librarianAddDocPanel.add(createField("Tác giả:", docAuthorField));
        librarianAddDocPanel.add(createField("Thể loại:", docCategoryField));
        librarianAddDocPanel.add(createField("Chi phí (VNĐ):", docCostField));
        librarianAddDocPanel.add(createField("Thông tin thêm (Trang;Định dạng):", docExtraField));
        librarianAddDocPanel.add(addBtn);

        panel.add(librarianAddDocPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createLoansTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(6, 6, 6, 6));

        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Khung mượn tài liệu (UC2)
        JPanel borrowBox = new JPanel(new GridLayout(4, 2, 6, 6));
        borrowBox.setBorder(new TitledBorder("Đăng ký mượn & Tùy chọn dịch vụ"));
        borrowReaderCombo = new JComboBox<>();
        borrowDocCombo = new JComboBox<>();
        drmCheckBox = new JCheckBox("Bảo vệ bản quyền (+5,000đ)", true);
        offlineCheckBox = new JCheckBox("Tải ngoại tuyến (+10,000đ)", false);
        JButton borrowBtn = new JButton("Mượn tài liệu");

        borrowBtn.addActionListener(e -> {
            String readerItem = (String) borrowReaderCombo.getSelectedItem();
            String docItem = (String) borrowDocCombo.getSelectedItem();
            if (readerItem == null || docItem == null) {
                showError("Vui lòng chọn độc giả và tài liệu!");
                return;
            }
            String readerId = readerItem.split(" - ")[0].trim();
            String docId = docItem.split(" - ")[0].trim();
            controller.handleBorrowDocument(readerId, docId, drmCheckBox.isSelected(), offlineCheckBox.isSelected());
        });

        borrowBox.add(new JLabel("Độc giả:"));
        borrowBox.add(borrowReaderCombo);
        borrowBox.add(new JLabel("Tài liệu:"));
        borrowBox.add(borrowDocCombo);
        borrowBox.add(drmCheckBox);
        borrowBox.add(offlineCheckBox);
        borrowBox.add(new JLabel(""));
        borrowBox.add(borrowBtn);

        // Khung trả tài liệu (UC3)
        JPanel returnBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 20));
        returnBox.setBorder(new TitledBorder("Trả tài liệu"));
        JButton returnSelectedBtn = new JButton("Trả phiếu đang chọn dưới bảng");
        returnSelectedBtn.addActionListener(e -> {
            int row = loanTable.getSelectedRow();
            if (row < 0) {
                showError("Vui lòng chọn một phiếu mượn dưới bảng để trả!");
                return;
            }
            String loanId = (String) loanTableModel.getValueAt(row, 0);
            controller.handleReturnDocument(loanId);
        });
        returnBox.add(returnSelectedBtn);

        topPanel.add(borrowBox);
        topPanel.add(returnBox);
        panel.add(topPanel, BorderLayout.NORTH);

        // Bảng danh sách phiếu mượn (UC3 & UC6)
        String[] loanCols = {"Mã phiếu", "Độc giả", "Ngày mượn", "Trạng thái", "Chi tiết tài liệu & Tùy chọn", "Tổng phí (VNĐ)"};
        loanTableModel = new DefaultTableModel(loanCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        loanTable = new JTable(loanTableModel);
        loanTable.setRowHeight(24);
        panel.add(new JScrollPane(loanTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createReadersTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(6, 6, 6, 6));

        // 1. Khung Thêm độc giả mới (Dành cho Thủ thư - UC6)
        librarianAddReaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        librarianAddReaderPanel.setBorder(new TitledBorder("Quản lý hồ sơ độc giả (Dành cho Thủ thư)"));
        readerIdField = new JTextField("DG04", 8);
        readerNameField = new JTextField(15);
        readerEmailField = new JTextField(16);
        readerSubCheck = new JCheckBox("Nhận thông báo sách mới", true);
        JButton addReaderBtn = new JButton("Thêm độc giả");

        addReaderBtn.addActionListener(e -> controller.handleRegisterReader(
                readerIdField.getText(),
                readerNameField.getText(),
                readerEmailField.getText(),
                readerSubCheck.isSelected()
        ));

        librarianAddReaderPanel.add(new JLabel("Mã ĐG:"));
        librarianAddReaderPanel.add(readerIdField);
        librarianAddReaderPanel.add(new JLabel("Họ tên:"));
        librarianAddReaderPanel.add(readerNameField);
        librarianAddReaderPanel.add(new JLabel("Email:"));
        librarianAddReaderPanel.add(readerEmailField);
        librarianAddReaderPanel.add(readerSubCheck);
        librarianAddReaderPanel.add(addReaderBtn);

        // 2. Khung Đăng ký / Hủy nhận thông báo tự động
        readerSubscriptionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        readerSubscriptionPanel.setBorder(new TitledBorder("Cài đặt nhận thông báo tài liệu mới tự động"));
        readerSubStatusLabel = new JLabel("Trạng thái hiện tại: Đang nhận thông báo");
        readerSubStatusLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        toggleSubBtn = new JButton("Hủy đăng ký nhận thông báo");

        toggleSubBtn.addActionListener(e -> {
            Reader active = getSelectedActiveReader();
            if (active != null) {
                controller.handleUpdateSubscription(active.getReaderId(), !active.isSubscribed());
            }
        });

        readerSubscriptionPanel.add(readerSubStatusLabel);
        readerSubscriptionPanel.add(toggleSubBtn);

        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.add(librarianAddReaderPanel, BorderLayout.NORTH);
        topWrapper.add(readerSubscriptionPanel, BorderLayout.SOUTH);
        readerSubscriptionPanel.setVisible(false);
        panel.add(topWrapper, BorderLayout.NORTH);

        String[] readerCols = {"Mã ĐG", "Họ và tên", "Email", "Nhận thông báo", "Số thông báo"};
        readerTableModel = new DefaultTableModel(readerCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        readerTable = new JTable(readerTableModel);
        readerTable.setRowHeight(24);
        readerTableScroll = new JScrollPane(readerTable);

        notificationArea = new JTextArea();
        notificationArea.setEditable(false);
        notificationArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane notifyScroll = new JScrollPane(notificationArea);
        notifyScroll.setBorder(new TitledBorder("Hộp thư thông báo"));

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                readerTableScroll,
                notifyScroll
        );
        splitPane.setDividerLocation(180);
        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private boolean isReaderMode() {
        return roleCombo.getSelectedIndex() == 1;
    }

    private Reader getSelectedActiveReader() {
        String item = (String) activeReaderCombo.getSelectedItem();
        if (item == null) return null;
        String id = item.split(" - ")[0].trim();
        for (Reader r : cachedReaders) {
            if (r.getReaderId().equalsIgnoreCase(id)) {
                return r;
            }
        }
        return null;
    }

    // Phân quyền hiển thị các chức năng theo đúng Vai trò
    private void applyRolePermissions() {
        boolean readerMode = isReaderMode();

        // Thanh công cụ trên cùng
        activeReaderLabel.setVisible(readerMode);
        activeReaderCombo.setVisible(readerMode);
        switchDbBtn.setVisible(!readerMode); // UC7 chỉ dành cho Thủ thư

        // Tab 1: Độc giả chỉ Tìm kiếm & Xem (UC1); Thủ thư có thêm Thêm/Xóa tài liệu (UC5)
        deleteDocBtn.setVisible(!readerMode);
        librarianAddDocPanel.setVisible(!readerMode);
        tabbedPane.setTitleAt(0, readerMode ? "Tra cứu tài liệu" : "Quản lý tài liệu");

        // Tab 2: Ở chế độ Độc giả, khóa ô chọn Độc giả vào đúng tài khoản đang đăng nhập
        borrowReaderCombo.setEnabled(!readerMode);
        tabbedPane.setTitleAt(1, readerMode ? "Mượn / Trả & Lịch sử" : "Mượn / Trả tài liệu");

        // Tab 3: Thủ thư quản lý toàn bộ độc giả (UC6); Độc giả bật/tắt thông báo & xem hộp thư riêng (UC4)
        librarianAddReaderPanel.setVisible(!readerMode);
        readerSubscriptionPanel.setVisible(readerMode);
        tabbedPane.setTitleAt(2, readerMode ? "Thông báo của tôi" : "Độc giả & Thông báo");

        syncViewsForCurrentRole();
    }

    private void syncViewsForCurrentRole() {
        boolean readerMode = isReaderMode();
        Reader activeReader = getSelectedActiveReader();

        if (readerMode && activeReader != null) {
            // Đồng bộ ComboBox mượn sách về đúng Độc giả đang chọn
            String targetItem = activeReader.getReaderId() + " - " + activeReader.getFullName();
            borrowReaderCombo.setSelectedItem(targetItem);

            // Cập nhật trạng thái nút Đăng ký / Hủy nhận thông báo (UC4)
            if (activeReader.isSubscribed()) {
                readerSubStatusLabel.setText("Trạng thái của " + activeReader.getFullName() + ": Đang đăng ký nhận thông báo tự động");
                toggleSubBtn.setText("Hủy nhận thông báo");
            } else {
                readerSubStatusLabel.setText("Trạng thái của " + activeReader.getFullName() + ": Đã tắt nhận thông báo");
                toggleSubBtn.setText("Đăng ký nhận thông báo");
            }
        }

        // Cập nhật bảng Phiếu mượn
        loanTableModel.setRowCount(0);
        for (LoanRecord l : cachedLoans) {
            if (!readerMode || (activeReader != null && l.getReader().getReaderId().equalsIgnoreCase(activeReader.getReaderId()))) {
                loanTableModel.addRow(new Object[]{
                        l.getLoanId(),
                        l.getReader().getFullName(),
                        l.getBorrowDate().toString(),
                        l.getStatusText(),
                        l.getBorrowedDocument().getDescription(),
                        String.format("%,.0f", l.getTotalCost())
                });
            }
        }

        // Cập nhật bảng Độc giả & Hộp thư thông báo
        readerTableModel.setRowCount(0);
        StringBuilder sb = new StringBuilder();
        for (Reader r : cachedReaders) {
            if (!readerMode || (activeReader != null && r.getReaderId().equalsIgnoreCase(activeReader.getReaderId()))) {
                readerTableModel.addRow(new Object[]{
                        r.getReaderId(),
                        r.getFullName(),
                        r.getEmail(),
                        r.isSubscribed() ? "Đang nhận tin" : "Đã tắt",
                        r.getNotifications().size()
                });
                for (String msg : r.getNotifications()) {
                    sb.append("• ").append(msg).append("\n");
                }
            }
        }
        if (sb.length() == 0) {
            sb.append("(Chưa có thông báo nào trong hộp thư)");
        }
        notificationArea.setText(sb.toString());
        notificationArea.setCaretPosition(0);
    }

    private JPanel createField(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(2, 2));
        p.add(new JLabel(label), BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    @Override
    public void setController(LibraryController controller) {
        this.controller = controller;
    }

    @Override
    public void startView() {
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
            controller.refreshAllViews();
        });
    }

    @Override
    public void showDocuments(List<IDocument> documents) {
        docTableModel.setRowCount(0);
        borrowDocCombo.removeAllItems();
        for (IDocument d : documents) {
            docTableModel.addRow(new Object[]{
                    d.getId(),
                    d.getDocumentType(),
                    d.getTitle(),
                    d.getAuthor(),
                    d.getCategory(),
                    String.format("%,.0f", d.getAccessCost()),
                    d.getDescription()
            });
            borrowDocCombo.addItem(d.getId() + " - " + d.getTitle());
        }
    }

    @Override
    public void showLoans(List<LoanRecord> loans) {
        this.cachedLoans = new ArrayList<>(loans);
        syncViewsForCurrentRole();
    }

    @Override
    public void showReadersAndNotifications(List<Reader> readers) {
        this.cachedReaders = new ArrayList<>(readers);
        updatingCombos = true;
        String prevSelected = (String) activeReaderCombo.getSelectedItem();
        borrowReaderCombo.removeAllItems();
        activeReaderCombo.removeAllItems();
        for (Reader r : readers) {
            String item = r.getReaderId() + " - " + r.getFullName();
            borrowReaderCombo.addItem(item);
            activeReaderCombo.addItem(item);
        }
        if (prevSelected != null) {
            activeReaderCombo.setSelectedItem(prevSelected);
        }
        updatingCombos = false;
        syncViewsForCurrentRole();
    }

    @Override
    public void showSystemStatus(String status) {
        statusLabel.setText(status);
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showError(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
