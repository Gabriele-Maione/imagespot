module com.imagespot {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
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
    exports com.imagespot.Controller;
    opens com.imagespot.Controller to javafx.fxml, javafx.base, javafx.web;
    exports com.imagespot.Controller.center;
    opens com.imagespot.Controller.center to javafx.fxml;
    exports com.imagespot.Controller.center.categories;
    opens com.imagespot.Controller.center.categories to javafx.fxml;
    exports com.imagespot.Controller.center.collections;
    opens com.imagespot.Controller.center.collections to javafx.fxml;

}