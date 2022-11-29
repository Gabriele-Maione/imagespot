module com.imagespot {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.imagespot to javafx.fxml;
    exports com.imagespot;
}