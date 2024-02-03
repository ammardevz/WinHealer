package com.github.ammardevz.winhealer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class App extends Application {
    public static Stage instance;
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("WinHealer " + Config.version);

        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Main.fxml")));
        Scene scene = new Scene(parent);
        stage.setScene(scene);

        stage.show();
        stage.setMinHeight(stage.getHeight());
        stage.setMinWidth(stage.getWidth());
        instance = stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
