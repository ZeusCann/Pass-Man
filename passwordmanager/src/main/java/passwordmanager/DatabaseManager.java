package passwordmanager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:passman.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS password_entries ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "service_name TEXT NOT NULL,"
                + "username TEXT NOT NULL,"
                + "password TEXT NOT NULL"
                + ");";

        try (Connection connection = getConnection();
            Statement statement = connection.createStatement()) {
                statement.execute(sql);

            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public static void insertEntry(PasswordEntry entry) {
        String sql = "INSERT INTO password_entries(service_name, username, password) VALUES (?, ?, ?);";

        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
 
                statement.setString(1, entry.getServiceName());
                statement.setString(2, entry.getUsername());
                statement.setString(3, entry.getPassword());
                statement.executeUpdate();
                
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        entry.setId(generatedId);
                    }
                }

            } catch (SQLException e) {
                System.out.println("INSERT failed:");
                e.printStackTrace();
            }
    }

    public static List<PasswordEntry> loadEntries() {
        String sql = "SELECT id, service_name, username, password FROM password_entries;";

        List<PasswordEntry> entries = new ArrayList<>();

        try (Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(sql)) {

            while(results.next()) {
                int id = results.getInt("id");
                String serviceName = results.getString("service_name");
                String username = results.getString("username");
                String password = results.getString("password");
                entries.add(new PasswordEntry(id,serviceName, username, password));
            }

        } catch (SQLException e) {
            e.printStackTrace();
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

    public static boolean updateEntry(PasswordEntry entry) {
        String sql = "UPDATE password_entries SET service_name = ?, username = ?, password = ? WHERE id = ?;";

        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, entry.getServiceName());
                statement.setString(2, entry.getUsername());
                statement.setString(3, entry.getPassword());
                statement.setInt(4, entry.getId());
                int rowsUpdated = statement.executeUpdate();
                return rowsUpdated > 0;

            } catch (SQLException e) {
                System.out.println("UPDATE failed.");
                e.printStackTrace();
                return false;
            }
    }
}