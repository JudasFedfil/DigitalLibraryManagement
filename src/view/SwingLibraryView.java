package view;

import controller.LibraryController;
import entity.IDocument;
import entity.LoanRecord;
import entity.Reader;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class SwingLibraryView extends JFrame implements ILibraryView {
    private LibraryController controller;

    private JLabel statusLabel;
    private DefaultTableModel docTableModel;
    private JTable docTable;
    private DefaultTableModel loanTableModel;
    private JTable loanTable;
    private DefaultTableModel readerTableModel;
    private JTable readerTable;
    private JTextArea notificationArea;

    // Tìm kiếm
    private JComboBox<String> searchTypeCombo;
    private JTextField searchKeywordField;

    // Thêm tài liệu
    private JComboBox<String> docTypeCombo;
    private JTextField docIdField, docTitleField, docAuthorField, docCategoryField, docCostField, docExtraField;

    // Mượn / Trả tài liệu
    private JComboBox<String> borrowReaderCombo;
    private JComboBox<String> borrowDocCombo;
    private JCheckBox drmCheckBox, offlineCheckBox;

    // Đăng ký độc giả
    private JTextField readerIdField, readerNameField, readerEmailField;
    private JCheckBox readerSubCheck;

    public SwingLibraryView() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        setTitle("Thư viện số");
        setSize(1050, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Thanh tiêu đề đơn giản
        JPanel topPanel = new JPanel(new BorderLayout(10, 5));
        JLabel titleLabel = new JLabel("THƯ VIỆN SỐ");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        statusLabel = new JLabel("Đang tải...");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JButton switchDbBtn = new JButton("Chuyển đổi CSDL");
        switchDbBtn.addActionListener(e -> controller.handleToggleDatabase());

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightTop.add(statusLabel);
        rightTop.add(switchDbBtn);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(rightTop, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 3 Tab chức năng chính
        JTabbedPane tabbedPane = new JTabbedPane();
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

        // Thanh tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchPanel.setBorder(new TitledBorder("Tìm kiếm"));
        searchTypeCombo = new JComboBox<>(new String[]{"Tiêu đề", "Tác giả", "Thể loại"});
        searchKeywordField = new JTextField(22);
        JButton searchBtn = new JButton("Tìm kiếm");
        JButton showAllBtn = new JButton("Làm mới");
        JButton deleteBtn = new JButton("Xóa tài liệu");

        searchBtn.addActionListener(e -> controller.handleSearchDocuments(
                (String) searchTypeCombo.getSelectedItem(), searchKeywordField.getText()));
        showAllBtn.addActionListener(e -> {
            searchKeywordField.setText("");
            controller.handleLoadAllDocuments();
        });
        deleteBtn.addActionListener(e -> {
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
        searchPanel.add(deleteBtn);
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

        // Form thêm tài liệu
        JPanel addPanel = new JPanel(new GridLayout(2, 4, 8, 8));
        addPanel.setBorder(new TitledBorder("Thêm tài liệu mới"));

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

        addPanel.add(createField("Loại tài liệu:", docTypeCombo));
        addPanel.add(createField("Mã tài liệu:", docIdField));
        addPanel.add(createField("Tiêu đề:", docTitleField));
        addPanel.add(createField("Tác giả:", docAuthorField));
        addPanel.add(createField("Thể loại:", docCategoryField));
        addPanel.add(createField("Chi phí (VNĐ):", docCostField));
        addPanel.add(createField("Thông tin thêm (Trang;Định dạng):", docExtraField));
        addPanel.add(addBtn);

        panel.add(addPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createLoansTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(6, 6, 6, 6));

        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Khung mượn tài liệu
        JPanel borrowBox = new JPanel(new GridLayout(4, 2, 6, 6));
        borrowBox.setBorder(new TitledBorder("Đăng ký mượn"));
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

        // Khung trả tài liệu
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

        // Bảng danh sách phiếu mượn
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

        JPanel regPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        regPanel.setBorder(new TitledBorder("Thêm độc giả"));
        readerIdField = new JTextField("DG04", 8);
        readerNameField = new JTextField(15);
        readerEmailField = new JTextField(16);
        readerSubCheck = new JCheckBox("Nhận thông báo sách mới", true);
        JButton addReaderBtn = new JButton("Lưu độc giả");

        addReaderBtn.addActionListener(e -> controller.handleRegisterReader(
                readerIdField.getText(),
                readerNameField.getText(),
                readerEmailField.getText(),
                readerSubCheck.isSelected()
        ));

        regPanel.add(new JLabel("Mã ĐG:"));
        regPanel.add(readerIdField);
        regPanel.add(new JLabel("Họ tên:"));
        regPanel.add(readerNameField);
        regPanel.add(new JLabel("Email:"));
        regPanel.add(readerEmailField);
        regPanel.add(readerSubCheck);
        regPanel.add(addReaderBtn);
        panel.add(regPanel, BorderLayout.NORTH);

        String[] readerCols = {"Mã ĐG", "Họ và tên", "Email", "Nhận thông báo", "Số thông báo"};
        readerTableModel = new DefaultTableModel(readerCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        readerTable = new JTable(readerTableModel);
        readerTable.setRowHeight(24);

        notificationArea = new JTextArea();
        notificationArea.setEditable(false);
        notificationArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane notifyScroll = new JScrollPane(notificationArea);
        notifyScroll.setBorder(new TitledBorder("Hộp thư thông báo"));

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(readerTable),
                notifyScroll
        );
        splitPane.setDividerLocation(180);
        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
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
        loanTableModel.setRowCount(0);
        for (LoanRecord l : loans) {
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

    @Override
    public void showReadersAndNotifications(List<Reader> readers) {
        readerTableModel.setRowCount(0);
        borrowReaderCombo.removeAllItems();

        StringBuilder sb = new StringBuilder();
        for (Reader r : readers) {
            readerTableModel.addRow(new Object[]{
                    r.getReaderId(),
                    r.getFullName(),
                    r.getEmail(),
                    r.isSubscribed() ? "Có" : "Không",
                    r.getNotifications().size()
            });
            borrowReaderCombo.addItem(r.getReaderId() + " - " + r.getFullName());

            for (String msg : r.getNotifications()) {
                sb.append("• ").append(msg).append("\n");
            }
        }
        notificationArea.setText(sb.toString());
        notificationArea.setCaretPosition(0);
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
