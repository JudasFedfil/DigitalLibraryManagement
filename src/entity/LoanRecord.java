package entity;

import java.time.LocalDate;

public class LoanRecord {
    private final String loanId;
    private final Reader reader;
    private final IDocument borrowedDocument;
    private final LocalDate borrowDate;
    private LocalDate returnDate;
    private boolean returned;

    public LoanRecord(String loanId, Reader reader, IDocument borrowedDocument) {
        this.loanId = loanId;
        this.reader = reader;
        this.borrowedDocument = borrowedDocument;
        this.borrowDate = LocalDate.now();
        this.returned = false;
    }

    public String getLoanId() { return loanId; }
    public Reader getReader() { return reader; }
    public IDocument getBorrowedDocument() { return borrowedDocument; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isReturned() { return returned; }

    public void markAsReturned() {
        this.returned = true;
        this.returnDate = LocalDate.now();
    }

    public double getTotalCost() {
        return borrowedDocument.getAccessCost();
    }

    public String getStatusText() {
        return returned ? "Đã trả (" + returnDate + ")" : "Đang mượn";
    }
}
