package de.dasbabypixel.physicsproject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class DataController {

	public DataController() {
	}

	public void load(Path path) {
		try {
			InputStream in = Files.newInputStream(path, StandardOpenOption.READ);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
