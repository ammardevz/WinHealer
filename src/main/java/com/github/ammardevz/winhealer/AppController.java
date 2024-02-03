package com.github.ammardevz.winhealer;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class AppController implements Initializable{

    private Stage consoleLoggerStage;

    private boolean isHealing = false;

    @FXML
    public CheckBox sfc, dism, chkdsk, netReset, flushDns, resetIp, memoryDiag;

    TextArea textArea = null;
    ProgressBar progressBar = null;

    private final List<CommandModule> commands = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            consoleLoggerStage = new Stage();
            consoleLoggerStage.initOwner(App.instance);
            consoleLoggerStage.setTitle("Console logger");
            consoleLoggerStage.initModality(Modality.WINDOW_MODAL);
            consoleLoggerStage.setOnCloseRequest(event -> {
                if (isHealing){
                    event.consume();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("WinHealer - Device Healing");
                    alert.setHeaderText("Healing in Progress");
                    alert.setContentText("WinHealer is currently performing a healing process on the device. Please wait until the process is finished before closing.");
                    alert.show();
                }
            });
            App.instance.setOnCloseRequest(event -> {
                if (isHealing){
                    event.consume();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("WinHealer - Device Healing");
                    alert.setHeaderText("Healing in Progress");
                    alert.setContentText("WinHealer is currently performing a healing process on the device. Please wait until the process is finished before closing.");
                    alert.show();
                }
            });
            try {
                Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("console.fxml")));
                Scene scene = new Scene(parent);
                consoleLoggerStage.setScene(scene);
                textArea = (TextArea) consoleLoggerStage.getScene().lookup("#txtArea");
                progressBar = (ProgressBar) consoleLoggerStage.getScene().lookup("#bar");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }

    public void checkParameters() {
        commands.clear();
        if (sfc.isSelected()) commands.add(new CommandModule("SFC", "sfc /scannow"));
        if (dism.isSelected()) commands.add(new CommandModule("DISM", "dism /online /cleanup-image /restorehealth"));
        if (chkdsk.isSelected()) {
            try {
                File file = File.createTempFile("DriveSearch", ".temp");
                char drive = file.getAbsolutePath().charAt(0);
                commands.add(new CommandModule("CHKDSK", "chkdsk /f " + drive + ":"));
                file.deleteOnExit();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        if (netReset.isSelected()) commands.add(new CommandModule("Reset network", "netsh winsock reset"));
        if (flushDns.isSelected()) commands.add(new CommandModule("Flush dns", "ipconfig /flushdns"));
        if (resetIp.isSelected()) commands.add(new CommandModule("Reset ip", "netsh int ip reset\n"));
        if (memoryDiag.isSelected()) commands.add(new CommandModule("Memory diagnostic", "mdsched.exe"));
    }


    public void startHealing() {
        checkParameters();
        isHealing = true;
        for (CommandModule command : commands) {
            try {
                log("Initializing service " + command.getName(), LogLevel.INFO);

                Process process = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", command.getCmd()});

                // Read the output of the process
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {  // Check if the line is not empty or only whitespace
                        log(line, LogLevel.INFO);
                    }
                }

                process.waitFor();
                log("----------------------------", LogLevel.INFO);
            } catch (IOException | InterruptedException e) {
                log("Service " + command.getName() + " failed", LogLevel.WARN);
            }
        }
        log("WinHealer finished task successfully", LogLevel.INFO);
        progressBar.setProgress(1);
        isHealing = false;
    }


    public void supportLink(){
        openLink("https://www.buymeacoffee.com/ammardev");
    }

    public void officialSite(){
        openLink("https://github.com/ammardevz");
    }

    private void openLink(String link) {
        try {
            Desktop.getDesktop().browse(new URI(link));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void log(String msg, LogLevel logLevel) {
        if (!consoleLoggerStage.isShowing()) {
            textArea.clear();
            consoleLoggerStage.show();
        }
        textArea.appendText("[WinHealer/" + logLevel.name() + "] " + msg + "\n");
    }

}

