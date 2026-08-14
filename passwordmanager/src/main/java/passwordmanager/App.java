package passwordmanager;

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

/**
 * JavaFX App
 */
public class App extends Application {

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

        Button btn = new Button("Add Entry");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        /*Event handler for the button, can be rewritten as a lambda expression as s:
          btn.setOnAction(event -> {
                if (serviceNameField.getText().isBlank() || usernameField.getText().isBlank() || passwordBox.getText().isBlank()) {
                    actiontarget.setFill(Color.RED);
                    actiontarget.setText("Please fill out all fields.");
                } else {
                    actiontarget.setFill(Color.BLUE);
                    actiontarget.setText("Entry Added!");
                }
        });
        */

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                if (serviceNameField.getText().isBlank() || usernameField.getText().isBlank() || passwordBox.getText().isBlank()) {
                    actiontarget.setFill(Color.RED);
                    actiontarget.setText("Please fill out all fields.");
                } else {
                    actiontarget.setFill(Color.BLUE);
                    actiontarget.setText("Entry Added!");
                }
            }
        });

        //Line 84 is for debugging purposes only. Its used to see the grid layout of the gui elements.
        //grid.setGridLinesVisible(true);

        Scene scene = new Scene(grid, 600, 475);
        primaryStage.setScene(scene);


       primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}