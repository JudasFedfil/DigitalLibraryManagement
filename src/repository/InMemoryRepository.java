package repository;

import entity.IDocument;
import entity.LoanRecord;
import entity.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import patterns.singleton.DatabaseConnectionManager;


public class InMemoryRepository implements IDataRepository {
    private final Map<String, IDocument> documents = new LinkedHashMap<>();
    private final Map<String, Reader> readers = new LinkedHashMap<>();
    private final Map<String, LoanRecord> loans = new LinkedHashMap<>();

    public InMemoryRepository() {
        DatabaseConnectionManager.getInstance().configureDatabase(
                "Bộ nhớ đệm (In-Memory)", "jdbc:memory://localhost/digital_library");
    }

    @Override
    public void saveDocument(IDocument doc) {
        documents.put(doc.getId().toUpperCase(), doc);
    }

    @Override
    public IDocument findDocumentById(String id) {
        if (id == null) return null;
        return documents.get(id.trim().toUpperCase());
    }

    @Override
    public List<IDocument> findAllDocuments() {
        return new ArrayList<>(documents.values());
    }

    @Override
    public boolean deleteDocument(String id) {
        if (id == null) return false;
        return documents.remove(id.trim().toUpperCase()) != null;
    }

    @Override
    public void saveReader(Reader reader) {
        readers.put(reader.getReaderId().toUpperCase(), reader);
    }

    @Override
    public Reader findReaderById(String readerId) {
        if (readerId == null) return null;
        return readers.get(readerId.trim().toUpperCase());
    }

    @Override
    public List<Reader> findAllReaders() {
        return new ArrayList<>(readers.values());
    }

    @Override
    public void saveLoan(LoanRecord loan) {
        loans.put(loan.getLoanId().toUpperCase(), loan);
    }

    @Override
    public LoanRecord findLoanById(String loanId) {
        if (loanId == null) return null;
        return loans.get(loanId.trim().toUpperCase());
    }

    @Override
    public List<LoanRecord> findAllLoans() {
        return new ArrayList<>(loans.values());
    }

    @Override
    public String getRepositoryName() {
        return "Bộ nhớ trong (In-Memory)";
    }
}
