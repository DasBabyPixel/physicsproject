package de.dasbabypixel.physicsproject;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import de.dasbabypixel.physicsproject.DataController.Entries;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class PhysicsApplication extends Application {

	private DataController data;

	private Preferences preferences;

	private Path lastload = null;

	private Path lastsave = null;

	@Override
	public void start(Stage stage) throws Exception {
		preferences = Preferences.userNodeForPackage(PhysicsApplication.class);
		String lastloadStr = preferences.get("lastload", null);
		if (lastloadStr != null) {
			lastload = Paths.get(lastloadStr);
		}
		String lastsaveStr = preferences.get("lastsave", null);
		if (lastsaveStr != null) {
			lastsave = Paths.get(lastsaveStr);
		}
		this.data = new DataController();
		ButtonBar buttons = new ButtonBar();
		Button button;

//		button = new Button("Clear");
//		buttons.getButtons().add(button);
		button = new Button("Save");
		button.setOnMousePressed(e -> {
			if (e.getButton() != MouseButton.PRIMARY) {
				return;
			}
			FileChooser fc = new FileChooser();
			if (lastsave != null) {
				fc.setInitialDirectory(lastsave.getParent().toFile());
				fc.setInitialFileName(lastsave.getFileName().toString());
			}
			fc.setSelectedExtensionFilter(new ExtensionFilter("All Files", "*.*"));
			File file = fc.showSaveDialog(stage);
			if (file == null)
				return;
			lastsave = file.toPath();
			data.save(lastsave);
		});
		buttons.getButtons().add(button);
		button = new Button("Load");

//		LineChart<String, Number> chart = new LineChart<>(new CategoryAxis(), new NumberAxis());
//		chart.setAnimated(false);
		button.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getButton() != MouseButton.PRIMARY) {
					return;
				}
				FileChooser fc = new FileChooser();
				if (lastload != null) {
					fc.setInitialDirectory(lastload.getParent().toFile());
					fc.setInitialFileName(lastload.getFileName().toString());
				}
				fc.setSelectedExtensionFilter(new ExtensionFilter("All Files", "*.*"));
				File file = fc.showOpenDialog(stage);
				if (file == null)
					return;
				lastload = file.toPath();
				Entries entries = data.load(lastload);
//				for (DataSet data : entries.datasets) {
//					chart.getDatasets().add(data);
//				}
//				Platform.runLater(() -> {
//					DoubleDataSet dds = new DoubleDataSet("test");
//					dds.add(0, 5);
//					dds.add(1, 7);
//					DoubleDataSet dds2 = new DoubleDataSet("test");
//					dds.add(0, 8);
//					dds.add(1, 5);
//					chart.getDatasets().add(dds);
//					chart.getDatasets().add(dds2);
//				});
			}

		});
		buttons.getButtons().add(button);

		buttons.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
//		chart.autosize();
//		chart.setTitle("Temperatures");
		VBox vb = new VBox();
//		VBox.setVgrow(chart, Priority.ALWAYS);
//		vb.getChildren().addAll(buttons, chart);
		vb.getChildren().add(buttons);
		Scene scene = new Scene(vb);
		stage.setScene(scene);

		stage.show();
		stage.setOnCloseRequest(e -> {
			try {
				preferences.clear();
				if (lastload != null)
					preferences.put("lastload", lastload.toAbsolutePath().toString());
				if (lastsave != null)
					preferences.put("lastsave", lastsave.toAbsolutePath().toString());
				preferences.sync();
			} catch (BackingStoreException ex) {
				ex.printStackTrace();
			}
		});
	}

}
