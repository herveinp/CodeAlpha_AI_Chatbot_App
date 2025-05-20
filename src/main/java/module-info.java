module com.herveinp.aichatbotapp {
	requires javafx.base;
	requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome6;
    requires org.apache.commons.codec;
    requires com.fasterxml.jackson.databind;
    requires okhttp3;

    opens com.herveinp.aichatbotapp to javafx.fxml;
    opens com.herveinp.aichatbotapp.controller to javafx.fxml;
    exports com.herveinp.aichatbotapp;
    exports com.herveinp.aichatbotapp.model;
}
