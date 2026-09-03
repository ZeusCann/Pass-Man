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
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.scene.control.ListView;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import java.util.Optional;

/**
 * JavaFX App
 */
public class App extends Application {

    private PasswordManager passwordManager = new PasswordManager();

    // Java assigns class instances like viewedEntry to null by default, so we don't have to explicitly state that its null.
    private PasswordEntry viewedEntry;
    private boolean passwordVisible = false;

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

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        // ListView to display entries
        ListView<PasswordEntry> entryListView = new ListView<>();
        grid.add(entryListView, 0, 7, 2, 1);

        Text serviceDetail = new Text();
        Text usernameDetail = new Text();
        Text passwordDetail = new Text();

        grid.add(serviceDetail, 0, 8, 2, 1);
        grid.add(usernameDetail, 0, 9, 2, 1);
        grid.add(passwordDetail, 0, 10, 2, 1);

        entryListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            actiontarget.setText("");

            /* Stopping point...tweaked line 110 to add viewedEntry != null &&..
               so it doesn't run the reset block out the gate when the app starts.*/
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
                System.out.println("Deleting entry: " + selectedEntry.toString());
                boolean removed = passwordManager.removeEntry(selectedEntry);

                if (removed) {
                    entryListView.getItems().remove(selectedEntry);
                    actiontarget.setFill(Color.BLUE);
                    actiontarget.setText("Entry Deleted!");
                    System.out.println("Current Entries: \n" + passwordManager.getEntries());

                    // Review the two lines of code tomorrow/later today to make sure its correct.
                    if (selectedEntry == viewedEntry) {
                        viewedEntry = null;
                        showPasswordBtn.setText("Show Password");
                        serviceDetail.setText("");
                        usernameDetail.setText("");
                        passwordDetail.setText("");

                    }
                } 
                // The below else block is for debugging purposes. If bugs arrise from UI and PasswordManager failing to sync.
                /*else {
                    actiontarget.setFill(Color.RED);
                    actiontarget.setText("Failed to delete entry.");
                }*/
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

                    if (selectedEntry == viewedEntry) {
                        serviceDetail.setText("Service Name: " + selectedEntry.getServiceName());
                        usernameDetail.setText("Username: " + selectedEntry.getUsername());
                        passwordDetail.setText("Password: " + "********");

                        passwordVisible = false;
                        showPasswordBtn.setText("Show Password");
                        actiontarget.setFill(Color.BLUE);
                        actiontarget.setText("Entry Updated!");
                        entryListView.refresh();

                    }
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

        /*entryListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PasswordEntry>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends PasswordEntry> observable, PasswordEntry oldValue, PasswordEntry newValue) {
                if (newValue != null) {
                    actiontarget.setFill(Color.BLACK);
                    actiontarget.setText("Selected Entry: \n" + newValue.toString() + "\nPassword: " + newValue.getPassword());
                }
            }
            
        } );
        */

        /*Event handler for the button, can be rewritten as a lambda expression as s:
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
        */

        addEntryBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                if (serviceNameField.getText().isBlank() || usernameField.getText().isBlank() || passwordBox.getText().isBlank()) {
                    actiontarget.setFill(Color.RED);
                    actiontarget.setText("Please fill out all fields.");
                } else {
                    PasswordEntry entry = new PasswordEntry(serviceNameField.getText(), usernameField.getText(), passwordBox.getText());
                    passwordManager.addEntry(entry);
                    entryListView.getItems().add(entry);
                    actiontarget.setFill(Color.BLUE);
                    actiontarget.setText("Entry Added!");
                    System.out.println("Current Entries: \n" + passwordManager.getEntries());

                    serviceNameField.clear();
                    usernameField.clear();
                    passwordBox.clear();


                }
            }
        });

        //Line 254 is for debugging purposes only. Its used to see the grid layout of the gui elements.
        //grid.setGridLinesVisible(true);

        Scene scene = new Scene(grid, 600, 475);
        primaryStage.setScene(scene);


       primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}