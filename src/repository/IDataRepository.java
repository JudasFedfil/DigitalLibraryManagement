package repository;

import entity.IDocument;
import entity.LoanRecord;
import entity.Reader;
import java.util.List;

public interface IDataRepository {
    void saveDocument(IDocument doc);
    IDocument findDocumentById(String id);
    List<IDocument> findAllDocuments();
    boolean deleteDocument(String id);

    void saveReader(Reader reader);
    Reader findReaderById(String readerId);
    List<Reader> findAllReaders();

    void saveLoan(LoanRecord loan);
    LoanRecord findLoanById(String loanId);
    List<LoanRecord> findAllLoans();

    String getRepositoryName();
}
