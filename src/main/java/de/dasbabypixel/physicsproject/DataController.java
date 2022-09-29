package de.dasbabypixel.physicsproject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class DataController {

	public Collection<Entries> entries = ConcurrentHashMap.newKeySet();

	public DataController() {
	}

	public void save(Path path) {
		try {
			OutputStream out = Files.newOutputStream(path, StandardOpenOption.WRITE,
					StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
			BufferedOutputStream bout = new BufferedOutputStream(out);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(bout, StandardCharsets.UTF_8));
			bw.newLine();
			bw.newLine();
			bw.newLine();
			DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm:ss");
			NumberFormat nformat = new DecimalFormat("#.000#");
			int i = 0;
			StringBuilder times = new StringBuilder();
			for (Entries ee : entries) {
				for (Entry e : ee.entries) {
					times.setLength(0);
					for (double temp : e.temperatures) {
						times.append(nformat.format(temp).replace(',', '.')).append(' ');
					}
					if (times.length() != 0)
						times.setLength(times.length() - 1);
					bw.write(String.format("%s %s %s", i, format.format(e.time), times));
					bw.newLine();
					i++;
				}
			}
			bw.close();
		} catch (Exception ex) {
			showExceptionDialog(ex);
		}
	}

	public Entries load(Path path) {
		try {
			InputStream in = Files.newInputStream(path, StandardOpenOption.READ);
			BufferedInputStream bin = new BufferedInputStream(in);
			BufferedReader br = new BufferedReader(new InputStreamReader(bin, StandardCharsets.UTF_8));
			String line;
			Throwable th = null;
			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.uuuu");
			DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
			Entries entries = new Entries();
			while ((line = br.readLine()) != null) {
				if (line.length() == 0) {
					continue;
				}
				char c0 = line.charAt(0);
				if (!Character.isDigit(c0)) {
					continue;
				}
				int index = 0;
				try {
					index = line.indexOf('.') - 2;
					LocalDate date = LocalDate.parse(line.substring(index, index + 10), dateFormat);
					index = line.indexOf(':') - 2;
					LocalTime time = LocalTime.parse(line.substring(index, index + 8),timeFormat);

					LocalDateTime ldt = LocalDateTime.of(date, time);
					Entry entry = new Entry();
					entry.time = ldt;

					line = line.substring(index + 8);
					List<Double> list = new ArrayList<>();
					while (true) {
						line = line.trim();
						if (line.isEmpty())
							break;
						double temp;
						if (line.startsWith("0.0000")) {
							temp = 0;
							line = line.substring(6);
						} else {
							temp = Double.parseDouble(line.substring(0, line.indexOf('.') + 4));
							line = line.substring(line.indexOf('.') + 4);
						}
						list.add(temp);
					}
					double[] ta = new double[list.size()];
					for (int i = 0; i < ta.length; i++) {
						ta[i] = list.get(i);
					}
					entry.temperatures = ta;
					entries.add(entry);

				} catch (Exception ex) {
					if (th == null)
						th = ex;
					ex.printStackTrace();
					continue;
				}
			}
			br.close();
			if (th != null) {
				showExceptionDialog(th);
			}
			entries.construct();
			this.entries.add(entries);
			return entries;
		} catch (IOException ex) {
			showExceptionDialog(ex);
		}
		return null;
	}

	public static class Entries {

		private static final DateTimeFormatter format = DateTimeFormatter.ISO_LOCAL_TIME;

//		public List<DoubleDataSet> datasets = new ArrayList<>();

		public List<Entry> entries = Collections.synchronizedList(new ArrayList<>());

		public void construct() {
		}

		public void add(Entry entry) {
			entries.add(entry);
			for (int i = 0; i < entry.temperatures.length; i++) {
//				while (datasets.size() <= i) {
//					datasets.add(new DoubleDataSet("SomeSet"));
//				}
//				String fm = format.format(entry.time);
//				double tmp = entry.temperatures[i];
//				datasets.get(i).add(0, tmp, fm);
			}
//				series.getData().add(new XYChart.Data<String, Number>(format.format(entry.time), temp));
		}

	}

	private static class Entry {

		private LocalDateTime time;

		private double[] temperatures;

	}

	public static void showExceptionDialog(Throwable throwable) {
		throwable.printStackTrace();/* w ww. j a va2s. c o m */

		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error while loading file");
		alert.setHeaderText("Thrown Exception");
		alert.setContentText("Loading file has thrown an exception.");

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		throwable.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label("The exception stacktrace was:");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		alert.getDialogPane().setExpandableContent(expContent);

		alert.showAndWait();
	}

}
