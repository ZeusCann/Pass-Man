module passwordmanager {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens passwordmanager to javafx.fxml;
    exports passwordmanager;
}
