package passwordmanager;

public class PasswordEntry {
    private String serviceName;
    private String username;
    private String password;
    private int id; // Add an ID field to uniquely identify each entry
    
    public PasswordEntry(String serviceName, String username, String password) {
        this.serviceName = serviceName;
        this.username = username;
        this.password = password;
    }

    public PasswordEntry(int id, String serviceName, String username, String password) {
        this.id = id;
        this.serviceName = serviceName;
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Service Name: " + serviceName + "\nUsername: " + username;
    }

}
