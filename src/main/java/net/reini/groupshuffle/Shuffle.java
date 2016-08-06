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
