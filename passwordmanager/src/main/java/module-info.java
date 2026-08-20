module passwordmanager {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;

    opens passwordmanager to javafx.fxml;
    exports passwordmanager;
}
