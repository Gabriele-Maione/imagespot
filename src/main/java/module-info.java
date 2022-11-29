module com.imagespot {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.imagespot to javafx.fxml;
    exports com.imagespot;
}