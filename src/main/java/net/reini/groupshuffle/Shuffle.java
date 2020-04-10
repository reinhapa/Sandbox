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

package net.reini.groupshuffle;

import java.util.function.IntConsumer;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Shuffle extends Application {
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    Button editBtn = new Button("Edit");
    editBtn.setMinWidth(50);
    TextArea text = new TextArea();

    HBox hbox = new HBox(5);
    hbox.setPadding(new Insets(5));
    hbox.setAlignment(Pos.CENTER_LEFT);
    ScrollPane buttons = new ScrollPane(hbox);
    buttons.setVbarPolicy(ScrollBarPolicy.NEVER);
    buttons.setHbarPolicy(ScrollBarPolicy.NEVER);
    ObservableList<Node> children = hbox.getChildren();
    children.add(editBtn);
    children.add(new Label("Gruppen:"));
    for (int i = 2; i < 6; i++) {
      Button shuffleBtn = new ShuffleButton(i, value -> text.textProperty().set("groups" + value));
      shuffleBtn.setMinWidth(50);
      children.add(shuffleBtn);
    }

    BorderPane root = new BorderPane();
    root.setTop(buttons);
    root.setCenter(text);
    editBtn.setOnAction(event -> text.textProperty().set(text.getText().concat("Hello World!\n")));
    primaryStage.setTitle("Shuffle");
    primaryStage.setScene(new Scene(root, 300, 250));
    primaryStage.show();
  }

  class ShuffleButton extends Button {
    ShuffleButton(int groups, IntConsumer shuffleAction) {
      super(String.valueOf(groups));
      setOnAction(event -> shuffleAction.accept(groups));
    }
  }
}
