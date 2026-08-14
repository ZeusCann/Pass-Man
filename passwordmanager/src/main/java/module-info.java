module passwordmanager {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens passwordmanager to javafx.fxml;
    exports passwordmanager;
}
