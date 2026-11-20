package passwordmanager;

public class VaultMetadata {
    private final byte[] salt;
    private final byte[] verifierCiphertext;
    private final byte[] verifierIv;

    // .clone() is used to create a copy of an array, preventing changes to the original array from affecting the stored copy.
    public VaultMetadata (byte[] salt, byte[] verifierCiphertext, byte[] verifierIv) {
        this.salt = salt.clone();
        this.verifierCiphertext = verifierCiphertext.clone();
        this.verifierIv = verifierIv.clone();
    }

    public byte[] getSalt() {
        return salt.clone();
    }

    public byte[] getVerifierCiphertext() {
        return verifierCiphertext.clone();
    }

    public byte[] getVerifierIv() {
        return verifierIv.clone();
    }
}
