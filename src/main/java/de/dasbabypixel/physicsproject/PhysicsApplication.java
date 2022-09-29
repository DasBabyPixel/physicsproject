package de.dasbabypixel.physicsproject;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class PhysicsApplication extends Application {

	private DataController data;

	private Preferences preferences;

	private Path lastload = null;

	@Override
	public void start(Stage stage) throws Exception {
		preferences = Preferences.userNodeForPackage(PhysicsApplication.class);
		String lastloadStr = preferences.get("lastload", null);
		if (lastloadStr != null) {
			lastload = Paths.get(lastloadStr);
			System.out.println("Last loaded path: " + lastload.toString());
		}
		this.data = new DataController();
		ButtonBar buttons = new ButtonBar();
		Button button;

		button = new Button("Clear");
		buttons.getButtons().add(button);
		button = new Button("Save");
		buttons.getButtons().add(button);
		button = new Button("Load");
		button.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getButton() != MouseButton.PRIMARY) {
					return;
				}
				FileChooser fc = new FileChooser();
				File file = fc.showOpenDialog(stage);
				if (file == null)
					return;
				lastload = file.toPath();
				data.load(lastload);
			}

		});
		buttons.getButtons().add(button);

		buttons.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		NumberAxis xaxis = new NumberAxis();
		NumberAxis yaxis = new NumberAxis();
		LineChart<Number, Number> chart = new LineChart<>(xaxis, yaxis);
		chart.autosize();
		chart.setTitle("Temperatures");
		VBox vb = new VBox();
		VBox.setVgrow(chart, Priority.ALWAYS);
		vb.getChildren().addAll(buttons, chart);
		Scene scene = new Scene(vb);
		stage.setScene(scene);

		stage.show();
		stage.setOnCloseRequest(e -> {
			try {
				preferences.put("lastload", lastload.toAbsolutePath().toString());
				preferences.sync();
			} catch (BackingStoreException ex) {
				ex.printStackTrace();
			}
		});
	}

}
