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

/**
 * JavaFX App
 */
public class App extends Application {

    private PasswordManager passwordManager = new PasswordManager();

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

    // Create the random password generator method below...

    private void updateSearchFilter(TextField searchField, FilteredList<PasswordEntry> filteredEntries) {
        String searchText = searchField.getText().toLowerCase();

            filteredEntries.setPredicate(entry -> {
                if (searchText.isBlank()) {
                    return true;
                }
                return entry.getServiceName().toLowerCase().contains(searchText) || entry.getUsername().toLowerCase().contains(searchText);
            });
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
                boolean removed = passwordManager.removeEntry(selectedEntry);

                if (removed) {
                    actiontarget.setFill(Color.BLUE);
                    actiontarget.setText("Entry Deleted!");
                    System.out.println("Current Entries: \n" + passwordManager.getEntries());

                    // Review the two lines of code tomorrow/later today to make sure its correct.
                    if (selectedEntry == viewedEntry) {
                        viewedEntry = null;
                        passwordVisible = false;
                        showPasswordBtn.setText("Show Password");
                        serviceDetail.setText("");
                        usernameDetail.setText("");
                        passwordDetail.setText("");

                    }
                } 
            }
             else {
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

                        selectedEntry.setServiceName(editServiceField.getText());
                        selectedEntry.setUsername(editUsernameField.getText());
                        selectedEntry.setPassword(editPasswordField.getText());

                        updateSearchFilter(searchField, filteredEntries);
                    
                    passwordVisible = false;
                    showPasswordBtn.setText("Show Password");
                    actiontarget.setFill(Color.BLUE);
                    actiontarget.setText("Entry Updated!");
                    entryListView.refresh();
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
                    passwordManager.addEntry(entry);
                    actiontarget.setFill(Color.BLUE);
                    actiontarget.setText("Entry Added!");
                    System.out.println("Current Entry: \n" + passwordManager.getEntries());

                    serviceNameField.clear();
                    usernameField.clear();
                    passwordBox.clear();
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
                    System.out.println("Generated Password: " + generatedPasswordText.getText());
                }
                
            } catch (NumberFormatException e) {
                actiontarget.setFill(Color.RED);
                actiontarget.setText("Invalid input. Enter a number 12 - 32.");
            }
        });

        Scene scene = new Scene(grid, 600, 700);
        primaryStage.setScene(scene);

       primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}