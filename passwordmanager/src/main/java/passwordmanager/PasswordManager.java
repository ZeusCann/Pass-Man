package passwordmanager;

//import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PasswordManager {

    private ObservableList<PasswordEntry> entries;
    
    public PasswordManager() {
        entries = FXCollections.observableArrayList();
    }

    public void addEntry(PasswordEntry entry) {
        entries.add(entry);
    }

    public ObservableList<PasswordEntry> getEntries() {
        return entries;
    }

    public boolean removeEntry(PasswordEntry entry) {
        return entries.remove(entry);
    }

}