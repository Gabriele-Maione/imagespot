module com.imagespot {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.commons.io;
    requires java.desktop;
    requires imgscalr.lib;
    requires org.json;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome;
    requires org.kordamp.ikonli.fontawesome5;

    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpmime;


    opens com.imagespot to javafx.fxml;
    exports com.imagespot;
    exports com.imagespot.controller;
    opens com.imagespot.controller to javafx.fxml;
    exports com.imagespot.controller.center;
    opens com.imagespot.controller.center to javafx.fxml;
    exports com.imagespot.controller.center.categories;
    opens com.imagespot.controller.center.categories to javafx.fxml;
    exports com.imagespot.controller.center.collections;
    opens com.imagespot.controller.center.collections to javafx.fxml;
}