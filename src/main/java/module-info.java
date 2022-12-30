module com.imagespot {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.commons.io;
    requires prettytime;
    requires java.desktop;
    requires imgscalr.lib;
    requires org.json;


    opens com.imagespot to javafx.fxml;
    exports com.imagespot;
    exports com.imagespot.controller;
    opens com.imagespot.controller to javafx.fxml;
}