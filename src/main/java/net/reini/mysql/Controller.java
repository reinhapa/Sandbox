/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2020 Patrick Reinhart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
