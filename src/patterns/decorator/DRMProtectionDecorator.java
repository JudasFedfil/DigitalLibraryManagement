package patterns.decorator;

import entity.IDocument;

/**
 * ConcreteDecorator 1: Bổ sung tính năng bảo vệ bản quyền số (DRM) vào tài liệu mượn.
 */
public class DRMProtectionDecorator extends DocumentDecorator {
    private static final double DRM_FEE = 5000.0;

    public DRMProtectionDecorator(IDocument wrappedDocument) {
        super(wrappedDocument);
    }

    @Override
    public double getAccessCost() {
        return super.getAccessCost() + DRM_FEE;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + [Bảo vệ bản quyền]";
    }
}
