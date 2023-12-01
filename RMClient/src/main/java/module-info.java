module com.example.remotedesktop {
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires Common;
    requires zip4j;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;

    exports com.RMClient.GUI;
    opens com.RMClient.GUI to javafx.fxml;

}