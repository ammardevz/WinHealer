module com.github.ammardevz.winhealer {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;


    requires java.desktop;

    exports com.github.ammardevz.winhealer;
    opens com.github.ammardevz.winhealer to javafx.fxml;
}
