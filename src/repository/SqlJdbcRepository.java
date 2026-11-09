package repository;

import entity.IDocument;
import entity.LoanRecord;
import entity.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import patterns.singleton.DatabaseConnectionManager;


public class SqlJdbcRepository implements IDataRepository {
    private final Map<String, IDocument> sqlTableDocuments = new LinkedHashMap<>();
    private final Map<String, Reader> sqlTableReaders = new LinkedHashMap<>();
    private final Map<String, LoanRecord> sqlTableLoans = new LinkedHashMap<>();
    private final String jdbcUrl;

    public SqlJdbcRepository(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        DatabaseConnectionManager.getInstance().configureDatabase("SQL Database (JDBC)", jdbcUrl);
    }

    @Override
    public void saveDocument(IDocument doc) {
        sqlTableDocuments.put(doc.getId().toUpperCase(), doc);
    }

    @Override
    public IDocument findDocumentById(String id) {
        if (id == null) return null;
        return sqlTableDocuments.get(id.trim().toUpperCase());
    }

    @Override
    public List<IDocument> findAllDocuments() {
        return new ArrayList<>(sqlTableDocuments.values());
    }

    @Override
    public boolean deleteDocument(String id) {
        if (id == null) return false;
        return sqlTableDocuments.remove(id.trim().toUpperCase()) != null;
    }

    @Override
    public void saveReader(Reader reader) {
        sqlTableReaders.put(reader.getReaderId().toUpperCase(), reader);
    }

    @Override
    public Reader findReaderById(String readerId) {
        if (readerId == null) return null;
        return sqlTableReaders.get(readerId.trim().toUpperCase());
    }

    @Override
    public List<Reader> findAllReaders() {
        return new ArrayList<>(sqlTableReaders.values());
    }

    @Override
    public void saveLoan(LoanRecord loan) {
        sqlTableLoans.put(loan.getLoanId().toUpperCase(), loan);
    }

    @Override
    public LoanRecord findLoanById(String loanId) {
        if (loanId == null) return null;
        return sqlTableLoans.get(loanId.trim().toUpperCase());
    }

    @Override
    public List<LoanRecord> findAllLoans() {
        return new ArrayList<>(sqlTableLoans.values());
    }

    @Override
    public String getRepositoryName() {
        return "SQL Database (" + jdbcUrl + ")";
    }
}
