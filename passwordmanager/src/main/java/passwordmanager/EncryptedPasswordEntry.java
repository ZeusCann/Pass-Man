package passwordmanager;

public class EncryptedPasswordEntry {

    private final int id;
    private final String serviceName;
    private final String username;
    private final byte[] passwordCiphertext;
    private final byte[] passwordIv;
    
    public EncryptedPasswordEntry(int id, String serviceName, String username, byte[] passwordCiphertext, byte[] passwordIv) {
        this.id = id;
        this.serviceName = serviceName;
        this.username = username;
        this.passwordCiphertext = passwordCiphertext.clone();
        this.passwordIv = passwordIv.clone();
    }

    public int getId() {
        return id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getUsername() {
        return username;
    }

    public byte[] getPasswordCiphertext() {
        return passwordCiphertext.clone();
    }

    public byte[] getPasswordIv() {
        return passwordIv.clone();
    }
}
