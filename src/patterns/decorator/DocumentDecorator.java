package patterns.decorator;

import entity.IDocument;

/**
 * [STRUCTURAL PATTERN 1 - DECORATOR PATTERN]:
 * Lớp trừu tượng bọc (wrap) một đối tượng IDocument để gắn thêm tính năng bổ sung lúc runtime
 * mà không cần sửa đổi lớp tài liệu gốc (Tuân thủ OCP & LSP).
 */
public abstract class DocumentDecorator implements IDocument {
    protected final IDocument wrappedDocument;

    public DocumentDecorator(IDocument wrappedDocument) {
        this.wrappedDocument = wrappedDocument;
    }

    @Override
    public String getId() { return wrappedDocument.getId(); }

    @Override
    public String getTitle() { return wrappedDocument.getTitle(); }

    @Override
    public String getAuthor() { return wrappedDocument.getAuthor(); }

    @Override
    public String getCategory() { return wrappedDocument.getCategory(); }

    @Override
    public String getDocumentType() { return wrappedDocument.getDocumentType(); }

    @Override
    public double getAccessCost() { return wrappedDocument.getAccessCost(); }

    @Override
    public String getDescription() { return wrappedDocument.getDescription(); }
}
