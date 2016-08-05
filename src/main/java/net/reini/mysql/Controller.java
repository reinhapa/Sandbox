package net.reini.mysql;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;

public class Controller implements Initializable {
  final ImportService importService;

  @FXML
  TextField fileName;
  @FXML
  TextField jdbcUrl;
  @FXML
  TextField jdbcUser;
  @FXML
  PasswordField jdbcPassword;
  @FXML
  StackPane stackPane;
  @FXML
  ProgressIndicator importProgress;
  @FXML
  Button importBtn;
  @FXML
  Button cancelBtn;
  @FXML
  Label statusMessage;

  public Controller() {
    importService = new ImportService();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    importService.fileNameProperty().bind(fileName.textProperty());
    importService.jdbcUrlProperty().bind(jdbcUrl.textProperty());
    importService.jdbcUserProperty().bind(jdbcUser.textProperty());
    importService.jdbcPasswordProperty().bind(jdbcPassword.textProperty());
    ReadOnlyBooleanProperty importRunningProperty = importService.runningProperty();
    statusMessage.textProperty().bind(importService.messageProperty());
    statusMessage.visibleProperty().bind(importRunningProperty);
    importProgress.progressProperty().bind(importService.progressProperty());
    importProgress.visibleProperty().bind(importRunningProperty);
    importBtn.disableProperty().bind(importRunningProperty);
    cancelBtn.disableProperty().bind(importRunningProperty.not());

    // default values
    fileName.setText("/mnt/tmp/bugs.sql.gz");
    jdbcUrl.setText("jdbc:mysql://bisdevsrv378.bisdevdom.ch/bugs");
    jdbcUser.setText("rep");
    jdbcPassword.setText("mirexal");
  }

  @FXML
  public void startImport() {
    importService.restart();
  }

  @FXML
  public void cancelImport() {
    importService.cancel();
  }
}
