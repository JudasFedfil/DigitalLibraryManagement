package patterns.decorator;

import entity.IDocument;

public class OfflineDownloadDecorator extends DocumentDecorator {
    private static final double OFFLINE_FEE = 10000.0;

    public OfflineDownloadDecorator(IDocument wrappedDocument) {
        super(wrappedDocument);
    }

    @Override
    public double getAccessCost() {
        return super.getAccessCost() + OFFLINE_FEE;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + [Tải ngoại tuyến]";
    }
}
