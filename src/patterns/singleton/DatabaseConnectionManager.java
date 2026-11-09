package patterns.singleton;

public class DatabaseConnectionManager {
    private static volatile DatabaseConnectionManager instance;

    private String databaseType;
    private String connectionUrl;
    private boolean connected;

    private DatabaseConnectionManager() {
        this.databaseType = "IN_MEMORY";
        this.connectionUrl = "jdbc:memory://localhost/digital_library";
        this.connected = true;
    }

    public static DatabaseConnectionManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnectionManager.class) {
                if (instance == null) {
                    instance = new DatabaseConnectionManager();
                }
            }
        }
        return instance;
    }

    public void configureDatabase(String databaseType, String connectionUrl) {
        this.databaseType = databaseType;
        this.connectionUrl = connectionUrl;
        this.connected = true;
    }

    public String getDatabaseType() { return databaseType; }
    public String getConnectionUrl() { return connectionUrl; }
    public boolean isConnected() { return connected; }

    public String getConnectionInfo() {
        return String.format("CSDL: %s (%s)", databaseType, connected ? "Đang kết nối" : "Ngắt kết nối");
    }
}
