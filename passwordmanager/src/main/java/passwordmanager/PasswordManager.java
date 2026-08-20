package passwordmanager;

import java.util.ArrayList;

public class PasswordManager {

    private ArrayList<PasswordEntry> entries;
    
    public PasswordManager() {
        entries = new ArrayList<>();
    }

    public void addEntry(PasswordEntry entry) {
        entries.add(entry);
    }

    public ArrayList<PasswordEntry> getEntries() {
        return entries;
    }

    public boolean removeEntry(PasswordEntry entry) {
        return entries.remove(entry);
    }

}