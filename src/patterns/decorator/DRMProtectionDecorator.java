package patterns.decorator;

import entity.IDocument;

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
