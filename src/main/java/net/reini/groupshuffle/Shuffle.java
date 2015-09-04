package net.reini.groupshuffle;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

		HBox buttons = new HBox(5);
		buttons.setPadding(new Insets(5));
		buttons.setAlignment(Pos.CENTER);

		ObservableList<Node> children = buttons.getChildren();
		children.add(editBtn);
		children.add(new Label("Gruppen:"));
		for (int i = 2; i < 6; i++) {
			Button shuffleBtn = new Button(Integer.toString(i));
			shuffleBtn.setMinWidth(50);
			children.add(shuffleBtn);
		}

		BorderPane root = new BorderPane();
		root.setTop(buttons);
		root.setCenter(text);

		editBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				text.textProperty().set(text.getText().concat("Hello World!\n"));
			}
		});

		primaryStage.setTitle("Shuffle");
		primaryStage.setScene(new Scene(root, 300, 250));
		primaryStage.show();
	}
}