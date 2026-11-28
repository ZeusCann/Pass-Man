package passwordmanager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.ArrayList;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:passman_encrypted_dev.db";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS password_entries ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "service_name TEXT NOT NULL,"
                + "username TEXT NOT NULL,"
                + "password_ciphertext BLOB NOT NULL,"
                + "password_iv BLOB NOT NULL"
                + ");";

        try (Connection connection = getConnection();
            Statement statement = connection.createStatement()) {
                statement.execute(sql);

            }
    }

    public static void createVaultMetadataTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS vault_metadata("
            + "id INTEGER PRIMARY KEY CHECK (id = 1),"
            + "salt BLOB NOT NULL,"
            + "verifier_ciphertext BLOB NOT NULL,"
            + "verifier_iv BLOB NOT NULL"
            + ");";

        try (Connection connection = getConnection();
            Statement statement = connection.createStatement()) {
                statement.execute(sql);

        }
    }

    public static VaultMetadata loadVaultMetadata() throws SQLException {
        String sql = "SELECT salt, verifier_ciphertext, verifier_iv FROM vault_metadata WHERE id = 1;";

        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet results = statement.executeQuery()) {

            if (results.next()) {
                byte[] salt = results.getBytes("salt");
                byte[] verifierCiphertext = results.getBytes("verifier_ciphertext");
                byte[] verifierIv = results.getBytes("verifier_iv");

                // return avoids creating an unnecessary local variable
                return new VaultMetadata(salt, verifierCiphertext, verifierIv);
                }
            }
        return null;
    }

    public static void saveVaultMetadata(byte[] salt, byte[] verifierCiphertext, byte[] verifierIv) throws SQLException {
        String sql = "INSERT INTO vault_metadata(id, salt, verifier_ciphertext, verifier_iv) VALUES (1, ?, ?, ?);";

        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setBytes(1, salt);
                statement.setBytes(2, verifierCiphertext);
                statement.setBytes(3, verifierIv);
                statement.executeUpdate();
                
            }
    }

    public static void insertEntry(PasswordEntry entry, byte[] ciphertext, byte[] iv) throws SQLException{
        String sql = "INSERT INTO password_entries(service_name, username, password_ciphertext, password_iv) VALUES (?, ?, ?, ?);";

        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
 
                statement.setString(1, entry.getServiceName());
                statement.setString(2, entry.getUsername());
                statement.setBytes(3, ciphertext);
                statement.setBytes(4, iv);
                statement.executeUpdate();
                
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        entry.setId(generatedId);
                    }
                }

            }
    }

    public static List<EncryptedPasswordEntry> loadEntries() throws SQLException{
        String sql = "SELECT id, service_name, username, password_ciphertext, password_iv FROM password_entries;";

        List<EncryptedPasswordEntry> entries = new ArrayList<>();

        try (Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(sql)) {

            while(results.next()) {
                int id = results.getInt("id");
                String serviceName = results.getString("service_name");
                String username = results.getString("username");
                byte[] passwordCiphertext = results.getBytes("password_ciphertext");
                byte[] passwordIv = results.getBytes("password_iv");
                entries.add(new EncryptedPasswordEntry(id, serviceName, username, passwordCiphertext, passwordIv));
            }

        }
        return entries;
    }

    public static boolean deleteEntry(PasswordEntry entry) {
        String sql = "DELETE FROM password_entries WHERE id = ?;";

        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setInt(1, entry.getId());
                int rowsDeleted = statement.executeUpdate();
                return rowsDeleted > 0;
            } catch (SQLException e) {
                System.out.println("DELETE failed.");
                e.printStackTrace();
                return false;
            }
    }

    public static boolean updateEntry(PasswordEntry entry, byte[] ciphertext, byte[] iv) {
        String sql = "UPDATE password_entries SET service_name = ?, username = ?, password_ciphertext = ?, password_iv = ? WHERE id = ?;";

        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, entry.getServiceName());
                statement.setString(2, entry.getUsername());
                statement.setBytes(3, ciphertext);
                statement.setBytes(4, iv);
                statement.setInt(5, entry.getId());
                int rowsUpdated = statement.executeUpdate();
                return rowsUpdated > 0;

            } catch (SQLException e) {
                System.out.println("UPDATE failed.");
                e.printStackTrace();
                return false;
            }
    }
}