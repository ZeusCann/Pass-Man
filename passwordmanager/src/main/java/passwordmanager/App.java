package passwordmanager;

//import javafx.beans.value.ChangeListener;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.scene.control.ListView;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Dialog;
//import javafx.scene.control.DialogPane;
import java.util.Optional;

import javafx.collections.transformation.FilteredList;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.crypto.AEADBadTagException;
import java.security.GeneralSecurityException;

/**
 * JavaFX App
 */
public class App extends Application {

    private PasswordManager passwordManager = new PasswordManager();

    private EncryptionManager encryptionManager = new EncryptionManager();
    private SecretKey vaultKey;

    // Java assigns class instances like viewedEntry to null by default, so we don't have to explicitly state that its null.
    private PasswordEntry viewedEntry;
    private boolean passwordVisible = false;

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+[]{}|;:'\",.<>?/`~";

    private final SecureRandom random = new SecureRandom();

    String allCharacters = LOWERCASE + UPPERCASE + NUMBERS + SYMBOLS;

    private String generateRandomPassword(int length, String availableCharacters) {

        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(availableCharacters.length());
            password.append(availableCharacters.charAt(randomIndex));
        }
        return password.toString();
    }

    private void updateSearchFilter(TextField searchField, FilteredList<PasswordEntry> filteredEntries) {
        String searchText = searchField.getText().toLowerCase();

            filteredEntries.setPredicate(entry -> {
                if (searchText.isBlank()) {
                    return true;
                }
                return entry.getServiceName().toLowerCase().contains(searchText) || entry.getUsername().toLowerCase().contains(searchText);
            });
    }

    public void loadDecryptedEntries() throws SQLException, GeneralSecurityException {

        List<EncryptedPasswordEntry> encryptedEntries = DatabaseManager.loadEntries();

        // Temporary list, only after every entry is decrypted successfully, should they be transferred into PasswordManager.
        List<PasswordEntry> decryptedEntries = new ArrayList<>();

        for (EncryptedPasswordEntry entry : encryptedEntries) {
            String decryptedPassword = encryptionManager.decrypt(entry.getPasswordCiphertext(), vaultKey, entry.getPasswordIv());
            PasswordEntry decryptedEntry = new PasswordEntry(entry.getId(), entry.getServiceName(), entry.getUsername(), decryptedPassword);
            decryptedEntries.add(decryptedEntry);
        }

        /* If an entry fails to decrypt, decrypt() throws a GeneralSecurityException.
           Java immediately stops executing the method and propogates that exception to the caller. 
           It never reaches the second loop. This loop executes only after the first loop finishes without throwing an exception. 
           Then adds the entries to passwordManager. */
        for (PasswordEntry tempEntry : decryptedEntries) {
            passwordManager.addEntry(tempEntry);
        }
    }

    @Override
        public void start(Stage primaryStage) {
        
       primaryStage.setTitle("Password Manager");

       GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Welcome to Pass Man");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label serviceName = new Label("Service Name:");
        grid.add(serviceName, 0, 1);

        TextField serviceNameField = new TextField();
        grid.add(serviceNameField, 1, 1);

        Label username = new Label("Username:");
        grid.add(username, 0, 2);

        TextField usernameField = new TextField();
        grid.add(usernameField, 1, 2);


        Label password = new Label("Password:");
        grid.add(password, 0, 3);

        PasswordField passwordBox = new PasswordField();
        grid.add(passwordBox, 1, 3);

        Button deleteBtn = new Button("Delete Entry");
        HBox hbDeleteBtn = new HBox(10);
        hbDeleteBtn.setAlignment(Pos.BOTTOM_LEFT);
        hbDeleteBtn.getChildren().add(deleteBtn);
        grid.add(hbDeleteBtn, 0, 4);

        Button viewBtn = new Button("View Entry");
        HBox hbViewBtn = new HBox(10);
        hbViewBtn.setAlignment(Pos.BOTTOM_LEFT);
        hbViewBtn.getChildren().add(viewBtn);
        grid.add(hbViewBtn, 0, 5);

        Button showPasswordBtn = new Button("Show Password");
        HBox hbShowPasswordBtn = new HBox(10);
        hbShowPasswordBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbShowPasswordBtn.getChildren().add(showPasswordBtn);
        grid.add(hbShowPasswordBtn, 1, 5);


        Button addEntryBtn = new Button("Add Entry");
        HBox hbAddEntryBtn = new HBox(10);
        hbAddEntryBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbAddEntryBtn.getChildren().add(addEntryBtn);
        grid.add(hbAddEntryBtn, 1, 4);

        Button editEntryBtn = new Button("Edit Entry");
        HBox hbEditEntryBtn = new HBox(10);
        hbEditEntryBtn.setAlignment(Pos.BOTTOM_LEFT);
        hbEditEntryBtn.getChildren().add(editEntryBtn);
        grid.add(hbEditEntryBtn, 0, 6);

        Label passwordLengthLabel = new Label("Password Length:");
        grid.add(passwordLengthLabel, 0, 14);
        TextField passwordLengthField = new TextField("16");
        passwordLengthField.setMaxWidth(35);
        grid.add(passwordLengthField, 1, 14);

        CheckBox lowercaseCheckBox = new CheckBox("Lowercase");
        lowercaseCheckBox.setSelected(true);
        grid.add(lowercaseCheckBox, 0, 17);
        CheckBox uppercaseCheckBox = new CheckBox("Uppercase");
        uppercaseCheckBox.setSelected(true);
        grid.add(uppercaseCheckBox, 0, 18);
        CheckBox numbersCheckBox = new CheckBox("Numbers");
        numbersCheckBox.setSelected(true);
        grid.add(numbersCheckBox, 1, 17);
        CheckBox symbolsCheckBox = new CheckBox("Symbols");
        symbolsCheckBox.setSelected(true);
        grid.add(symbolsCheckBox, 1, 18);

        Button generatePasswordBtn = new Button("Generate Password");
        HBox hbGeneratePasswordBtn = new HBox(10);
        hbGeneratePasswordBtn.setAlignment(Pos.BOTTOM_LEFT);
        hbGeneratePasswordBtn.getChildren().add(generatePasswordBtn);
        grid.add(hbGeneratePasswordBtn, 0, 15);

        TextField generatedPasswordText = new TextField();
        grid.add(generatedPasswordText, 1, 15);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        // ListView to display entries
        ListView<PasswordEntry> entryListView = new ListView<>();
        grid.add(entryListView, 0, 8, 2, 2);

        Text serviceDetail = new Text();
        Text usernameDetail = new Text();
        Text passwordDetail = new Text();

        grid.add(serviceDetail, 0, 11, 2, 1);
        grid.add(usernameDetail, 0, 12, 2, 1);
        grid.add(passwordDetail, 0, 13, 2, 1);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by Service Name or Username...");
        grid.add(searchField, 0, 7, 2, 1);

       GridPane vaultPane = new GridPane();
            vaultPane.setAlignment(Pos.CENTER);
            vaultPane.setHgap(10);
            vaultPane.setVgap(10);
            vaultPane.setPadding(new Insets(25, 25, 25, 25));

            // Pass Man - First time password setup screen
            Label masterPasswordField = new Label("Master Password: ");
            vaultPane.add(masterPasswordField, 0, 1);

            PasswordField masterPassword = new PasswordField();
            vaultPane.add(masterPassword, 1, 1);

            Label confirmMasterPasswordField = new Label("Confirm Master Password");
            vaultPane.add(confirmMasterPasswordField, 0, 2);

            PasswordField confirmMasterPassword = new PasswordField();
            vaultPane.add(confirmMasterPassword, 1, 2);

            Button createVaultBtn = new Button("Create Vault");
            vaultPane.add(createVaultBtn, 1, 3);

            Text validation = new Text();
            vaultPane.add(validation, 1, 4);

            createVaultBtn.setOnAction(event -> {
            if (masterPassword.getText().isBlank() || confirmMasterPassword.getText().isBlank()) {
                validation.setFill(Color.RED);
                validation.setText("Please fill out both fields.");
            } else if (!confirmMasterPassword.getText().equals(masterPassword.getText())) {
                validation.setFill(Color.RED);
                validation.setText("Master passwords do not match.");
            } else {

                char[] masterpass = masterPassword.getText().toCharArray();

                try {
                    byte[] salt = encryptionManager.generateSalt();
                    SecretKey key = encryptionManager.deriveKey(masterpass, salt);

                    String verificationMessage = "PASSMAN_VAULT_VERIFIER";
                    byte[] verifierIv = encryptionManager.generateIv();
                    byte[] verifierCiphertext = encryptionManager.encrypt(verificationMessage, key, verifierIv);
                    DatabaseManager.saveVaultMetadata(salt, verifierCiphertext, verifierIv);
                    vaultKey = key;
                    masterPassword.clear();
                    confirmMasterPassword.clear();
                    // If success messages do not appear, then delete the next two lines below.
                    validation.setFill(Color.BLUE);
                    validation.setText("Vault configuration saved successfully!");
                    primaryStage.setScene(new Scene(grid, 600, 700));
                    
                } catch (GeneralSecurityException | SQLException e) {
                    validation.setFill(Color.RED);
                    validation.setText("Unable to initialize vault encryption.");
                } finally {
                    Arrays.fill(masterpass, '\0');
                }
            }
        });

        GridPane unlockPane = new GridPane();
            unlockPane.setAlignment(Pos.CENTER);
            unlockPane.setHgap(10);
            unlockPane.setVgap(10);
            unlockPane.setPadding(new Insets(25, 25, 25, 25));

            Label unlockMasterPasswordLabel = new Label("Master Password: ");
            unlockPane.add(unlockMasterPasswordLabel, 0, 1);

            PasswordField unlockMasterPasswordField = new PasswordField();
            unlockPane.add(unlockMasterPasswordField, 1, 1);

            Button unlockButton = new Button("Unlock Vault");
            unlockPane.add(unlockButton, 1, 2);

            Text unlockText = new Text();
            unlockPane.add(unlockText, 1, 3);

            unlockButton.setOnAction(event -> {

                if (unlockMasterPasswordField.getText().isBlank()) {
                    unlockText.setFill(Color.RED);
                    unlockText.setText("Please enter your master password.");
                } else {

                    char[] masterpass = unlockMasterPasswordField.getText().toCharArray();

                    try {
                        VaultMetadata metadata = DatabaseManager.loadVaultMetadata();

                        if (metadata == null) {
                            unlockText.setFill(Color.RED);
                            unlockText.setText("Vault configuration not found.");
                            return;
                        } else {
                            
                            byte[] salt = metadata.getSalt();
                            SecretKey key = encryptionManager.deriveKey(masterpass, salt);
                            byte[] verifierCiphertext = metadata.getVerifierCiphertext();
                            byte[] verifierIv = metadata.getVerifierIv();
                            String decryptedVerifier = encryptionManager.decrypt(verifierCiphertext, key, verifierIv);

                            if (decryptedVerifier.equals("PASSMAN_VAULT_VERIFIER")) {
                                vaultKey = key;
                                // try/catch, additionally ensures that the application doesn't remain in an unlocked state if decryption fails midway through.
                                try {
                                    loadDecryptedEntries();
                                    unlockMasterPasswordField.clear();
                                    unlockText.setFill(Color.BLUE);
                                    // If the success message does not appear before the scene changes to the main vault screen, 
                                    // we can remove the message below.
                                    unlockText.setText("Vault unlocked successfully!");
                                    primaryStage.setScene(new Scene(grid, 600, 700));
                                } catch (GeneralSecurityException | SQLException e) {
                                    vaultKey = null;
                                    unlockText.setFill(Color.RED);
                                    unlockText.setText("VAULT-DATA LOADING ERROR: Unable to load saved password entries.");
                                }
                            } else {
                                unlockText.setFill(Color.RED);
                                unlockText.setText("Vault verification failed.");
                            }
                        }
                    } catch (AEADBadTagException e) {

                        unlockText.setFill(Color.RED);
                        unlockText.setText("Unable to verify master password.");

                    } catch (SQLException e) {
                        e.printStackTrace();
            
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Database ERROR");
                        alert.setHeaderText("Unable to unlock the vault.");
                        alert.setContentText("Please check the database and try again..");
                        alert.showAndWait();

                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();

                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Encryption Error");
                        alert.setHeaderText("Unable to unlock the vault.");
                        alert.setContentText("An unexpected cryptographic error occured.");
                        alert.showAndWait();

                    } finally {
                        Arrays.fill(masterpass, '\0');
                    }
                }
            });

        // FilteredList and entryListView.setItems() is taken out of searchField.textProperty() so that it doesn't create a new FilteredList object with every keystroke.
        FilteredList<PasswordEntry> filteredEntries = new FilteredList<>(passwordManager.getEntries(), entry -> true);
        entryListView.setItems(filteredEntries);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateSearchFilter(searchField, filteredEntries);
        });

        entryListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            actiontarget.setText("");

            if (viewedEntry != null && newValue != viewedEntry) {
                viewedEntry = null;
                passwordVisible = false;

                showPasswordBtn.setText("Show Password");
                serviceDetail.setText("");
                usernameDetail.setText("");
                passwordDetail.setText("");
            }
        });
        
        deleteBtn.setOnAction(event -> {
            PasswordEntry selectedEntry = entryListView.getSelectionModel().getSelectedItem();
            if (selectedEntry != null) {
                boolean deletedEntry = DatabaseManager.deleteEntry(selectedEntry);

                if (deletedEntry) {
                    boolean removed = passwordManager.removeEntry(selectedEntry);

                    if (removed) {
                        actiontarget.setFill(Color.BLUE);
                        actiontarget.setText("Entry Deleted!");

                        if (selectedEntry == viewedEntry) {
                            viewedEntry = null;
                            passwordVisible = false;
                            showPasswordBtn.setText("Show Password");
                            serviceDetail.setText("");
                            usernameDetail.setText("");
                            passwordDetail.setText("");

                        }
                    } 
                } else {
                    actiontarget.setFill(Color.RED);
                    actiontarget.setText("Failed to delete entry from database.");
                }

                
            } else {
                actiontarget.setFill(Color.RED);
                actiontarget.setText("Please select an entry to delete.");
            }
        });

        viewBtn.setOnAction(event -> {
            PasswordEntry selectedEntry = entryListView.getSelectionModel().getSelectedItem();

            if (selectedEntry != null) {
                viewedEntry = selectedEntry;
                // Lines 137 & 138 revert the show/hide password toggle button back to the "Show Password" position when a new entry is being viewed.
                passwordVisible = false;
                showPasswordBtn.setText("Show Password");

                serviceDetail.setText("Service Name: " + selectedEntry.getServiceName());
                usernameDetail.setText("Username: " + selectedEntry.getUsername());
                passwordDetail.setText("Password: " + "********");
                actiontarget.setText("");
            }
            else {
                actiontarget.setFill(Color.RED);
                actiontarget.setText("Please select an entry to view.");
            }
        });

        editEntryBtn.setOnAction(event -> {
            PasswordEntry selectedEntry = entryListView.getSelectionModel().getSelectedItem();
            if (selectedEntry != null) {
                actiontarget.setText("");

                GridPane editGrid = new GridPane();
                TextField editServiceField = new TextField();
                TextField editUsernameField = new TextField();
                PasswordField editPasswordField = new PasswordField();
                
                editGrid.add(new Label("Service Name: "), 0, 0);
                editGrid.add(editServiceField, 1, 0);

                editGrid.add(new Label("Username: "), 0, 1);
                editGrid.add(editUsernameField, 1, 1);

                editGrid.add(new Label("Password: "), 0, 2);
                editGrid.add(editPasswordField, 1, 2);

                Text actiontargetdialog = new Text();
                editGrid.add(actiontargetdialog, 1, 3);

                editServiceField.setText(selectedEntry.getServiceName());
                editUsernameField.setText(selectedEntry.getUsername());
                editPasswordField.setText(selectedEntry.getPassword());


                ButtonType saveButtonType = new ButtonType("Save", ButtonData.OTHER);
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Edit Entry");
                dialog.getDialogPane().setContent(editGrid);
                dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
                
                Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
                saveButton.addEventFilter(ActionEvent.ACTION, actionEvt -> {
                    if (editServiceField.getText().isBlank() || editUsernameField.getText().isBlank() || editPasswordField.getText().isBlank()) {
                        actiontargetdialog.setFill(Color.RED);
                        actiontargetdialog.setText("Please fill out all fields.");
                        actionEvt.consume(); // Prevents the dialog from closing
                    }
                });
                // result.isPresent: Did the dialog actually give me a result?
                // result.get(): Give me the ButtonType stored inside Optional.
                Optional<ButtonType> result = dialog.showAndWait();
                if (result.isPresent() && result.get() == saveButtonType) {
                        String oldServiceName = selectedEntry.getServiceName();
                        String oldUsername = selectedEntry.getUsername();
                        String oldPassword = selectedEntry.getPassword();

                        selectedEntry.setServiceName(editServiceField.getText());
                        selectedEntry.setUsername(editUsernameField.getText());
                        selectedEntry.setPassword(editPasswordField.getText());

                        boolean updated = DatabaseManager.updateEntry(selectedEntry);

                        if (updated) {
                            updateSearchFilter(searchField, filteredEntries);

                            passwordVisible = false;
                            showPasswordBtn.setText("Show Password");
                            actiontarget.setFill(Color.BLUE);
                            actiontarget.setText("Entry Updated!");
                            entryListView.refresh();

                        } else {
                            actiontarget.setFill(Color.RED);
                            actiontarget.setText("Failed to update entry in database.");
                            // Revert changes if database update fails
                            selectedEntry.setServiceName(oldServiceName);
                            selectedEntry.setUsername(oldUsername);
                            selectedEntry.setPassword(oldPassword);
                        }
                }

            } else {
                actiontarget.setFill(Color.RED);
                actiontarget.setText("Please select an entry to edit.");
            }
        });

        showPasswordBtn.setOnAction(event -> {
            PasswordEntry selectedEntry = entryListView.getSelectionModel().getSelectedItem();
            if (selectedEntry == null) {
                actiontarget.setFill(Color.RED);
                actiontarget.setText("Please select and view an entry to show password.");
                }
            
            else if (selectedEntry == viewedEntry) {

                actiontarget.setText("");

                if (!passwordVisible) {
                    passwordVisible = true;
                    showPasswordBtn.setText("Hide Password");
                    passwordDetail.setText("Password: " + selectedEntry.getPassword());
                        
                } else {
                    passwordVisible = false;
                    showPasswordBtn.setText("Show Password");
                    passwordDetail.setText("Password: ********");
                }
            }
            else {

                viewedEntry = null;
                passwordVisible = false;

                showPasswordBtn.setText("Show Password");
                serviceDetail.setText("");
                usernameDetail.setText("");
                passwordDetail.setText("");

                actiontarget.setFill(Color.RED);
                actiontarget.setText("Please view the entry first to show the password.");
            }
        });

          addEntryBtn.setOnAction(event -> {
                if (serviceNameField.getText().isBlank() || usernameField.getText().isBlank() || passwordBox.getText().isBlank()) {
                    actiontarget.setFill(Color.RED);
                    actiontarget.setText("Please fill out all fields.");
                } else {
                    PasswordEntry entry = new PasswordEntry(serviceNameField.getText(), usernameField.getText(), passwordBox.getText());
                    byte[] iv = encryptionManager.generateIv();
                    try {
                        byte[] ciphertext = encryptionManager.encrypt(entry.getPassword(), vaultKey, iv);
                        DatabaseManager.insertEntry(entry, ciphertext, iv);

                        // Only executes these operations if the database insertion is successful.
                        passwordManager.addEntry(entry);
                        // Insert the entry into the SQLite database using DatabaseManager
                        actiontarget.setFill(Color.BLUE);
                        actiontarget.setText("Entry Added!");

                        serviceNameField.clear();
                        usernameField.clear();
                        passwordBox.clear();
                    } catch (GeneralSecurityException | SQLException e) {
                        actiontarget.setFill(Color.RED);
                        actiontarget.setText("Failed to insert entry into database.");
                        e.printStackTrace();
                    }
                }
        });

        generatePasswordBtn.setOnAction(event -> {

            StringBuilder availableCharacters = new StringBuilder();

            if (lowercaseCheckBox.isSelected()) {
                availableCharacters.append(LOWERCASE);
            }
            if (uppercaseCheckBox.isSelected()) {
                availableCharacters.append(UPPERCASE);
            }
            if (numbersCheckBox.isSelected()) {
                availableCharacters.append(NUMBERS);
            }
            if (symbolsCheckBox.isSelected()) {
                availableCharacters.append(SYMBOLS);
            }
            if (availableCharacters.length() == 0) {
                actiontarget.setFill(Color.RED);
                actiontarget.setText("Please select at least one character type.");

                return;
            }

            try {
                int length = Integer.parseInt(passwordLengthField.getText().trim());
                
                if (length < 12 || length > 32) {
                    actiontarget.setFill(Color.RED);
                    actiontarget.setText("Please enter a valid number (12 - 32) for password length.");
                } else {
                    actiontarget.setText("");
                    String generatedPassword = generateRandomPassword(length, availableCharacters.toString());
                    generatedPasswordText.setText(generatedPassword);
                }
                
            } catch (NumberFormatException e) {
                actiontarget.setFill(Color.RED);
                actiontarget.setText("Invalid input. Enter a number 12 - 32.");
            }
        });

        try {
            DatabaseManager.createTable();
            DatabaseManager.createVaultMetadataTable();
            VaultMetadata metadata = DatabaseManager.loadVaultMetadata();

            if (metadata != null) {
                Scene unlockScene = new Scene(unlockPane, 600, 700);
                primaryStage.setScene(unlockScene);
            } else {
                Scene createVault = new Scene(vaultPane, 600, 700);
                primaryStage.setScene(createVault);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database ERROR");
            alert.setHeaderText("Unable to initialize the vault.");
            alert.setContentText("Please check the database and try again..");
            alert.showAndWait();

            return;
        }

        /*Scene scene = new Scene(grid, 600, 700);
        primaryStage.setScene(scene);
        */

       primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}