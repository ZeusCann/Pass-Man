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
        System.out.println("insertEntry() was called");
        String sql = "INSERT INTO password_entries(service_name, username, password) VALUES (?, ?, ?);";

        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                System.out.println("Database connection opened for INSERT");

                statement.setString(1, entry.getServiceName());
                statement.setString(2, entry.getUsername());
                statement.setString(3, entry.getPassword());
                int rowsInserted = statement.executeUpdate();
                System.out.println("Rows inserted into database: " + rowsInserted);
                //statement.executeUpdate();
                
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        entry.setId(generatedId);
                        System.out.println("Generated ID: " + entry.getId());
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
                // --> WE STOPPED HERE FOR DEBUGGING PURPOSES. <-- !!!!!!!!
                System.out.println("Loaded entry ID: " + id + " - " + serviceName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Entries loaded from database: " + entries.size());
        return entries;
    }
}