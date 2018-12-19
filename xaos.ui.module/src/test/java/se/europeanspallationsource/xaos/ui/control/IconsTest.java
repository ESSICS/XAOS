/*
 * Copyright 2018 European Spallation Source ERIC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.europeanspallationsource.xaos.ui.control;


import io.github.classgraph.ClassGraph;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeoutException;
import javafx.embed.swing.SwingNode;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.ParallelCamera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Box;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.VLineTo;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static se.europeanspallationsource.xaos.ui.control.CommonIcons.FILE;
import static se.europeanspallationsource.xaos.ui.control.CommonIcons.FILE_EXECUTABLE;
import static se.europeanspallationsource.xaos.ui.control.CommonIcons.FILE_HIDDEN;
import static se.europeanspallationsource.xaos.ui.control.CommonIcons.FILE_LINK;
import static se.europeanspallationsource.xaos.ui.control.CommonIcons.FOLDER_COLLAPSED;
import static se.europeanspallationsource.xaos.ui.control.CommonIcons.FOLDER_EXPANDED;
import static se.europeanspallationsource.xaos.ui.control.CommonIcons.SQUARE_DOWN;
import static se.europeanspallationsource.xaos.ui.control.CommonIcons.SQUARE_LEFT;
import static se.europeanspallationsource.xaos.ui.control.CommonIcons.SQUARE_RIGHT;
import static se.europeanspallationsource.xaos.ui.control.CommonIcons.SQUARE_UP;
import static se.europeanspallationsource.xaos.ui.control.Icons.DEFAULT_SIZE;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "UseOfSystemOutOrSystemErr", "ClassWithoutLogger" } )
public class IconsTest extends ApplicationTest {

	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- IconsTest -------------------------------------------------");
	}

	@Override
	public void start( Stage stage ) throws IOException {

		stage.setScene(new Scene(new AnchorPane(), 800, 500));
		stage.show();

	}

	@After
	public void tearDown() throws TimeoutException {
		FxToolkit.cleanupStages();
	}

	/**
	 * Test of iconForClass method, of class Icons.
	 */
	@Test
	public void testIconForClass_String() {

		System.out.println("  Testing 'iconForClass'...");

		Set<String> supportedClasses = new HashSet<>(Arrays.asList(
			Accordion.class.getName(),
			AmbientLight.class.getName(),
			AnchorPane.class.getName(),
			Arc.class.getName(),
			ArcTo.class.getName(),
			AreaChart.class.getName(),
			BarChart.class.getName(),
			BorderPane.class.getName(),
			Box.class.getName(),
			BubbleChart.class.getName(),
			Button.class.getName(),
			ButtonBar.class.getName(),
			Canvas.class.getName(),
			CategoryAxis.class.getName(),
			Chart.class.getName(),
			CheckBox.class.getName(),
			CheckMenuItem.class.getName(),
			ChoiceBox.class.getName(),
			Circle.class.getName(),
			ClosePath.class.getName(),
			ColorPicker.class.getName(),
			ComboBox.class.getName(),
			ContextMenu.class.getName(),
			CubicCurve.class.getName(),
			CubicCurveTo.class.getName(),
			CustomMenuItem.class.getName(),
			Cylinder.class.getName(),
			DatePicker.class.getName(),
			DialogPane.class.getName(),
			Ellipse.class.getName(),
			FlowPane.class.getName(),
			GridPane.class.getName(),
			Group.class.getName(),
			HBox.class.getName(),
			HLineTo.class.getName(),
			HTMLEditor.class.getName(),
			Hyperlink.class.getName(),
			ImageView.class.getName(),
			Label.class.getName(),
			Line.class.getName(),
			LineChart.class.getName(),
			LineTo.class.getName(),
			ListView.class.getName(),
			MediaView.class.getName(),
			Menu.class.getName(),
			MenuBar.class.getName(),
			MenuButton.class.getName(),
			MenuItem.class.getName(),
			MeshView.class.getName(),
			MoveTo.class.getName(),
			Node.class.getName(),
			NumberAxis.class.getName(),
			Pagination.class.getName(),
			Pane.class.getName(),
			ParallelCamera.class.getName(),
			PasswordField.class.getName(),
			Path.class.getName(),
			PerspectiveCamera.class.getName(),
			PieChart.class.getName(),
			PointLight.class.getName(),
			Polygon.class.getName(),
			Polyline.class.getName(),
			ProgressBar.class.getName(),
			ProgressIndicator.class.getName(),
			QuadCurve.class.getName(),
			QuadCurveTo.class.getName(),
			RadioButton.class.getName(),
			RadioMenuItem.class.getName(),
			Rectangle.class.getName(),
			Region.class.getName(),
			SVGPath.class.getName(),
			ScatterChart.class.getName(),
			ScrollBar.class.getName(),
			ScrollPane.class.getName(),
			Separator.class.getName(),
			SeparatorMenuItem.class.getName(),
			Slider.class.getName(),
			Sphere.class.getName(),
			Spinner.class.getName(),
			SplitMenuButton.class.getName(),
			SplitPane.class.getName(),
			StackPane.class.getName(),
			StackedAreaChart.class.getName(),
			StackedBarChart.class.getName(),
			SubScene.class.getName(),
			SwingNode.class.getName(),
			Tab.class.getName(),
			TabPane.class.getName(),
			TableColumn.class.getName(),
			TableView.class.getName(),
			Text.class.getName(),
			TextArea.class.getName(),
			TextField.class.getName(),
			TextFlow.class.getName(),
			TilePane.class.getName(),
			TitledPane.class.getName(),
			ToggleButton.class.getName(),
			ToolBar.class.getName(),
			Tooltip.class.getName(),
			TreeTableColumn.class.getName(),
			TreeTableView.class.getName(),
			TreeView.class.getName(),
			VBox.class.getName(),
			VLineTo.class.getName(),
			WebView.class.getName()
		));

		Package[] packages = Package.getPackages();
		Set<String> packageNames = new TreeSet<>();

		for ( Package p : packages ) {
			packageNames.add(p.getName());
		}

		Set<String> classNames = new TreeSet<>();

		packageNames
			.stream()
			.filter(packageName -> packageName.startsWith("java"))
			.forEach(packageName -> {
				System.out.println(MessageFormat.format("    Collecting classes from package {0}...", packageName));
				classNames.addAll(
					new ClassGraph()
						.enableSystemJarsAndModules()
						.whitelistPackages(packageName)
						.scan()
						.getAllClasses()
						.getNames()
				);
			});

		classNames.forEach(className -> {
			if ( supportedClasses.contains(className) ) {
				supportedClasses.remove(className);
				assertThat(Icons.iconForClass(className, DEFAULT_SIZE))
					.overridingErrorMessage("Expecting actual icon not to be null for %1$s", className)
					.isNotNull()
					.isInstanceOf(ImageView.class);
			} else {
				assertNull("Icon for " + className, Icons.iconForClass(className, DEFAULT_SIZE));
			}
		});

		if ( !supportedClasses.isEmpty() ) {
			System.out.println(MessageFormat.format("    {0} unmatched classes:", supportedClasses.size()));
			supportedClasses
				.stream()
				.sorted()
				.forEach(n -> System.out.println(MessageFormat.format("      {0}", n)));
			Assert.fail(MessageFormat.format("{0} unmatched classes:", supportedClasses.size()));
		}

	}

	/**
	 * Test of iconForFileExtension method, of class Icons.
	 */
	@Test
	public void testIconForFileExtension() {

		System.out.println("  Testing 'iconForFileExtension'...");

		//	--------------------------------------------------------------------
		System.out.println("    Archive files...");

		assertThat(Icons.iconForFileExtension("7x",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("a##",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("acb",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ace",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ar7",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("arc",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ari",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("arj",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ark",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("arx",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("b1",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ba",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("bs2",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("bsa",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("bz2",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("dmg",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("dwc",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("gz",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("hbc",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("hbe",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("hpk",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("hqx",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("jar",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("lif",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("lzw",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("lzx",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("maff",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mar",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pka",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pkg",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pma",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ppk",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("rar",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("rpm",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sp",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("tar",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("taz",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("tbz",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("tgz",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("tpz",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("tz",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("tzb",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("uc2",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ucn",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("war",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("x",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("yz",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("z",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("zip",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Audio/Music files...");

		assertThat(Icons.iconForFileExtension("4md",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("668",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("669",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("6cm",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("8cm",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("aac",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ad2",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ad3",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("aif",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("aifc",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("aiff",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("amr",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ams",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ape",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("asf",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("au",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("aud",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("audio",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cda",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cdm",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cfa",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("enc",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("flac",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("m4a",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("m4r",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mp2",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mp3",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mpa",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("nxt",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ogg",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("omg",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("opus",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sf",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sfl",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("smp",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("snd",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("son",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sound",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("voc",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wav",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wave",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wma",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xa",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Code files...");

		assertThat(Icons.iconForFileExtension("4th",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("8",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("a",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("a80",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("act",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ada",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("adb",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ads",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ash",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("asi",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("asm",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("bas",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("bat",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("bi",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("c--",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("c++",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("c",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cas",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cbl",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cc",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cl",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("coffee",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cs",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cxx",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("d",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("di",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("e",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("el",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("erl",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("esh",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("f",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("f4",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("f77",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("f90",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("f95",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("for",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("gc1",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("gc3",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("go",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("h--",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("h++",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("h",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("hh",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("hpp",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("htm",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("html",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("hxx",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ipynb",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("jav",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("java",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("js",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("json",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("kcl",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("l",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("lisp",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("m",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mak",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mm",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("p",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pas",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("py",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("r",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("rb",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("s",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sh",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sm",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("st",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("swift",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("tcl",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xml",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Database files...");

		assertThat(Icons.iconForFileExtension("3dt",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("4db",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("4dindy",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ab6",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ab8",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("accda",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("accdb",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("accde",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("accdt",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("accdu",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("accft",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ap",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("bib",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cac",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cdb",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("crp",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("db",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("db2",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("db3",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("dbf",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("dbk",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("dbx",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("dtf",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("fm",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("fp3",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("fp4",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("fp5",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("fp7",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("fw",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("fw2",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("fw3",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("idb",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ldb",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mat",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mdf",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ndb",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("phf",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("res",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("rpd",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("tdb",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Image/Picture files...");

		assertThat(Icons.iconForFileExtension("555",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("75",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("a11",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("acmb",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ais",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("art",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("b&w",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("b_w",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("b1n",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("b8",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("bga",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("bif",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("bmp",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cbm",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cco",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cdf",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ceg",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cgm",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cr2",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cut",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("dcs",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ddb",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("dem",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("eps",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("gif",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("gry",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("hdw",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("iax",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ica",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("icb",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ico",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("idw",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("j2c",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("jff",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("jfif",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("jif",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("jp2",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("jpc",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("jpeg",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("jpg",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("miff",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("msp",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pcd",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pct",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pcx",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pda",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pdn",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pdd",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pic",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pict",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pix",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("png",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pnt",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ppm",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ras",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("raw",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("rgb",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("rif",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("riff",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("rl4",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("rl8",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("rla",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("rlb",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("rlc",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sg1",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sgi",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("spi",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("spiff",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sun",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("tga",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("tif",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("tiff",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("vda",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("vgr",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("vif",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("viff",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("vpg",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wim",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wpg",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xbm",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xcf",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xif",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xpm",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xwd",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Movie/Video files...");

		assertThat(Icons.iconForFileExtension("byu",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("f4a",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("f4b",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("f4p",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("f4v",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("fmv",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("m2ts",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("m4p",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("m4v",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mov",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mp4",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mpeg",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mpg",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mpx",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mts",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("qt",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("qtvr",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sdc",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("tmf",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("trp",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ts",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ty",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("vob",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("vue",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wmv",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xmv",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    PDF files...");

		assertThat(Icons.iconForFileExtension("pdf",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Presentation/Powerpoint files...");

		assertThat(Icons.iconForFileExtension("ch4",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("key",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("odp",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pcs",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pot",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pps",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ppt",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pptx",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("psx",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("shw",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sxi",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("uop",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Spreadsheet files...");

		assertThat(Icons.iconForFileExtension("123",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("bwb",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cal",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("col",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("fm1",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("fm3",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("lcw",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("lss",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mdl",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ods",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("qbw",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("slk",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sxc",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("uos",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("vc",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wk1",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wk3",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wk4",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wke",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wki",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wkq",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wks",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wkz",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wq1",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wr1",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xla",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xlb",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xlc",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xld",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xlk",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xll",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xlm",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xls",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xlsb",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xlsm",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xlsx",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xlt",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xlv",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xlw",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Text files...");

		assertThat(Icons.iconForFileExtension("1st",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("602",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("asc",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ftxt",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("hex",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("inf",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("log",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("me",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("readme",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("text",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("txt",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Word Processor files...");

		assertThat(Icons.iconForFileExtension("chi",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("doc",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("docm",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("docx",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("dot",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("lwp",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("odm",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("odt",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ott",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pages",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pm3",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pm4",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pm5",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pt3",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pt4",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pt5",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pwp",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sxw",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("uot",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wkb",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wp",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wp5",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wpd",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wri",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xlr",DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Testing non-exiting extensions...");

		assertThat(Icons.iconForFileExtension("qqq",DEFAULT_SIZE)).isNull();
		assertThat(Icons.iconForFileExtension("xxx",DEFAULT_SIZE)).isNull();
		assertThat(Icons.iconForFileExtension("777",DEFAULT_SIZE)).isNull();

	}

	/**
	 * Test of iconForMIMEType method, of class Icons.
	 */
	@Test
	public void testIconForMIMEType() {

		System.out.println("  Testing 'iconForMIMEType'...");

		//	--------------------------------------------------------------------
		System.out.println("    Archive files...");

		assertThat(Icons.iconForMIMEType("application/gzip")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("application/zip")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("application/zlib")).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Audio/Music files...");

		assertThat(Icons.iconForMIMEType("audio/1d-interleaved-parityfec")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/32kadpcm")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/3gpp")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/3gpp2")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/aac")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/ac3")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/AMR")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/AMR-WB")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/amr-wb+")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/aptx")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/asc")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/ATRAC-ADVANCED-LOSSLESS")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/ATRAC-X")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/ATRAC3")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/basic")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/BV16")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/BV32")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/clearmode")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/CN")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/DAT12")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/dls")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/dsr-es201108")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/dsr-es202050")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/dsr-es202211")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/dsr-es202212")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/DV")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/DVI4")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/eac3")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/encaprtp")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/EVRC")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/EVRC-QCP")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/EVRC0")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/EVRC1")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/EVRCB")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/EVRCB0")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/EVRCB1")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/EVRCNW")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/EVRCNW0")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/EVRCNW1")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/EVRCWB")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/EVRCWB0")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/EVRCWB1")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/EVS")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/example")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/fwdred")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/G711-0")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/G719")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/G7221")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/G722")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/G723")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/G726-16")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/G726-24")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/G726-32")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/G726-40")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/G728")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/G729")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/G7291")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/G729D")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/G729E")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/GSM")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/GSM-EFR")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/GSM-HR-08")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/iLBC")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/ip-mr_v2.5")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/L8")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/L16")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/L20")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/L24")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/LPC")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/MELP")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/MELP600")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/MELP1200")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/MELP2400")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/mobile-xmf")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/MPA")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/mp4")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/MP4A-LATM")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/mpa-robust")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/mpeg")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/mpeg4-generic")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/ogg")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/opus")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/parityfec")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/PCMA")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/PCMA-WB")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/PCMU")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/PCMU-WB")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/prs.sid")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/QCELP")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/raptorfec")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/RED")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/rtp-enc-aescm128")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/rtploopback")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/rtp-midi")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/rtx")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/SMV")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/SMV0")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/SMV-QCP")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/sp-midi")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/speex")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/t140c")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/t38")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/telephone-event")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/tone")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/UEMCLIP")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/ulpfec")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/usac")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/VDVI")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/VMR-WB")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.3gpp.iufp")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.4SB")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.audiokoz")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.CELP")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.cisco.nse")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.cmles.radio-events")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.cns.anp1")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.cns.inf1")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.dece.audio")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.digital-winds")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.dlna.adts")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.dolby.heaac.1")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.dolby.heaac.2")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.dolby.mlp")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.dolby.mps")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.dolby.pl2")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.dolby.pl2x")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.dolby.pl2z")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.dolby.pulse.1")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.dra")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.dts")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.dts.hd")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.dvb.file")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.everad.plj")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.hns.audio")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.lucent.voice")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.ms-playready.media.pya")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.nokia.mobile-xmf")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.nortel.vbk")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.nuera.ecelp4800")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.nuera.ecelp7470")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.nuera.ecelp9600")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.octel.sbc")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.presonus.multitrack")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.qcelp")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.rhetorex.32kadpcm")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.rip")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.sealedmedia.softseal.mpeg")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vnd.vmx.cvsd")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vorbis")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("audio/vorbis-config")).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Code files...");

		assertThat(Icons.iconForMIMEType("application/ecmascript")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("application/javascript")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("application/json")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("application/json-patch+json")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("application/json-seq")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("application/xml")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("application/xml-dtd")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("application/xml-external-parsed-entity")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("application/xml-patch+xml")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("text/css")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("text/html")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("text/javascript")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("text/xml")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("text/xml-external-parsed-entity")).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Database files...");

		assertThat(Icons.iconForMIMEType("application/sql")).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Image/Picture files...");

		assertThat(Icons.iconForMIMEType("image/aces")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/avci")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/avcs")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/bmp")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/cgm")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/dicom-rle")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/emf")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/example")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/fits")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/g3fax")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/gif")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/heic")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/heic-sequence")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/heif")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/heif-sequence")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/ief")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/jls")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/jp2")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/jpeg")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/jpm")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/jpx")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/ktx")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/naplps")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/png")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/prs.btif")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/prs.pti")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/pwg-raster")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/svg+xml")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/t38")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/tiff")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/tiff-fx")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.adobe.photoshop")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.airzip.accelerator.azv")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.cns.inf2")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.dece.graphic")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.djvu")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.dwg")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.dxf")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.dvb.subtitle")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.fastbidsheet")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.fpx")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.fst")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.fujixerox.edmics-mmr")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.fujixerox.edmics-rlc")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.globalgraphics.pgb")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.microsoft.icon")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.mix")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.ms-modi")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.mozilla.apng")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.net-fpx")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.radiance")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.sealed.png")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.sealedmedia.softseal.gif")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.sealedmedia.softseal.jpg")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.svf")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.tencent.tap")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.valve.source.texture")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.wap.wbmp")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.xiff")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/vnd.zbrush.pcx")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/wmf")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/x-emf")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("image/x-wmf")).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Movie/Video files...");

		assertThat(Icons.iconForMIMEType("application/mp4")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("application/mpeg4-generic")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("application/mpeg4-iod")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("application/mpeg4-iod-xmt")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("application/ogg")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/1d-interleaved-parityfec")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/3gpp")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/3gpp2")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/3gpp-tt")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/BMPEG")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/BT656")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/CelB")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/DV")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/encaprtp")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/example")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/H261")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/H263")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/H263-1998")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/H263-2000")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/H264")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/H264-RCDO")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/H264-SVC")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/H265")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/iso.segment")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/JPEG")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/jpeg2000")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/mj2")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/MP1S")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/MP2P")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/MP2T")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/mp4")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/MP4V-ES")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/MPV")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/mpeg")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/mpeg4-generic")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/nv")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/ogg")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/parityfec")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/pointer")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/quicktime")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/raptorfec")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/raw")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/rtp-enc-aescm128")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/rtploopback")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/rtx")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/smpte291")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/SMPTE292M")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/ulpfec")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vc1")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.CCTV")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.dece.hd")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.dece.mobile")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.dece.mp4")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.dece.pd")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.dece.sd")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.dece.video")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.directv.mpeg")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.directv.mpeg-tts")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.dlna.mpeg-tts")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.dvb.file")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.fvt")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.hns.video")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.iptvforum.1dparityfec-1010")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.iptvforum.1dparityfec-2005")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.iptvforum.2dparityfec-1010")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.iptvforum.2dparityfec-2005")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.iptvforum.ttsavc")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.iptvforum.ttsmpeg2")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.motorola.video")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.motorola.videop")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.mpegurl")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.ms-playready.media.pyv")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.nokia.interleaved-multimedia")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.nokia.mp4vr")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.nokia.videovoip")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.objectvideo")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.radgamettools.bink")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.radgamettools.smacker")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.sealed.mpeg1")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.sealed.mpeg4")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.sealed.swf")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.sealedmedia.softseal.mov")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.uvvu.mp4")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/vnd.vivo")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("video/VP8")).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    PDF files...");

		assertThat(Icons.iconForMIMEType("application/pdf")).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
//		System.out.println("    Presentation/Powerpoint files...");
//
//		assertThat(Icons.iconForMIMEType("xxx/xxx")).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
//		System.out.println("    Spreadsheet files...");
//
//		assertThat(Icons.iconForMIMEType("xxx/xxx")).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Text files...");

		assertThat(Icons.iconForMIMEType("application/rtf")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("text/csv")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("text/markdown")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("text/plain")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("text/richtext")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("text/rtf")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForMIMEType("text/strings")).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Word Processor files...");

		assertThat(Icons.iconForMIMEType("application/msword")).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Testing non-exiting MIME types...");

		assertThat(Icons.iconForMIMEType("qqq/xxx")).isNull();
		assertThat(Icons.iconForMIMEType("xxxx/ssss")).isNull();
		assertThat(Icons.iconForMIMEType("wwwww/kkkkk")).isNull();

	}

	/**
	 * Test of iconFor method, of class Icons.
	 */
	@Test
	@SuppressWarnings( { "UseSpecificCatch", "BroadCatchBlock", "TooBroadCatch" } )
	public void testIconFor_Class() {

		System.out.println("  Testing 'iconFor(Class)'...");

		Set<Class<?>> supportedClasses = new HashSet<>(Arrays.asList(
			Accordion.class, 
			AmbientLight.class,
			AnchorPane.class,
			Arc.class, 
			ArcTo.class,
			AreaChart.class,
			BarChart.class, 
			BorderPane.class,
			Box.class,
			BubbleChart.class, 
			Button.class,
			ButtonBar.class,
			Canvas.class, 
			CategoryAxis.class,
			Chart.class,
			CheckBox.class, 
			CheckMenuItem.class,
			ChoiceBox.class,
			Circle.class, 
			ClosePath.class,
			ColorPicker.class,
			ComboBox.class, 
			ContextMenu.class,
			CubicCurve.class,
			CubicCurveTo.class, 
			CustomMenuItem.class,
			Cylinder.class,
			DatePicker.class,
			DialogPane.class,
			Ellipse.class,
			FlowPane.class,
			GridPane.class,
			Group.class,
			HBox.class, 
			HLineTo.class,
			HTMLEditor.class,
			Hyperlink.class,
			ImageView.class,
			Label.class, 
			Line.class,
			LineChart.class,
			LineTo.class,
			ListView.class,
			MediaView.class, 
			Menu.class,
			MenuBar.class,
			MenuButton.class, 
			MenuItem.class,
			MeshView.class,
			MoveTo.class,
			Node.class,
			NumberAxis.class,
			Pagination.class, 
			Pane.class,
			ParallelCamera.class,
			PasswordField.class, 
			Path.class,
			PerspectiveCamera.class,
			PieChart.class, 
			PointLight.class,
			Polygon.class,
			Polyline.class, 
			ProgressBar.class,
			ProgressIndicator.class,
			QuadCurve.class,
			QuadCurveTo.class,
			RadioButton.class, 
			RadioMenuItem.class,
			Rectangle.class,
			Region.class,
			SVGPath.class, 
			ScatterChart.class,
			ScrollBar.class,
			ScrollPane.class, 
			Separator.class,
			SeparatorMenuItem.class,
			Slider.class, 
			Sphere.class,
			Spinner.class,
			SplitMenuButton.class, 
			SplitPane.class,
			StackPane.class,
			StackedAreaChart.class, 
			StackedBarChart.class,
			SubScene.class,
			SwingNode.class,
			Tab.class, 
			TabPane.class,
			TableColumn.class,
			TableView.class, 
			Text.class,
			TextArea.class,
			TextField.class, 
			TextFlow.class,
			TitledPane.class,
			ToggleButton.class, 
			ToolBar.class,
			Tooltip.class,
			TreeTableColumn.class, 
			TreeTableView.class,
			TreeView.class,
			VBox.class, 
			VLineTo.class,
			WebView.class
		));

		//	Testing Object.clas...
		assertThat(Icons.iconFor(Object.class, DEFAULT_SIZE)).isNull();

		//	Testing all other classes...
		new HashSet<>(supportedClasses).forEach(type -> {
			if ( supportedClasses.contains(type) ) {
				supportedClasses.remove(type);
				assertThat(Icons.iconFor(type, DEFAULT_SIZE))
					.overridingErrorMessage("Expecting actual icon not to be null for %1$s", type.getName())
					.isNotNull()
					.isInstanceOf(ImageView.class);
			} else {
				assertThat(Icons.iconFor(type, DEFAULT_SIZE)).isNull();
			}
		});

		if ( !supportedClasses.isEmpty() ) {
			System.out.println(MessageFormat.format("    {0} unmatched classes:", supportedClasses.size()));
			supportedClasses
				.stream()
				.map(c -> c.getName())
				.sorted()
				.forEach(n -> System.out.println(MessageFormat.format("      {0}", n)));
			Assert.fail(MessageFormat.format("{0} unmatched classes:", supportedClasses.size()));
		}

	}

	/**
	 * Test of iconFor method, of class Icons.
	 */
	@Test
	public void testIconFor_File() {

		System.out.println("  Testing 'iconFor(File)'...");

		//	--------------------------------------------------------------------
		System.out.println("    Archive files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.7x"), DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.a##"),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Audio/Music files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.4md"),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.668"),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Code files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.4th"),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.8"),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Database files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.3dt"),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.4db"),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Image/Picture files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.555"),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.75"),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Movie/Video files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.byu"),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.f4a"),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    PDF files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.pdf"),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Presentation/Powerpoint files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.ch4"),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.key"),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Spreadsheet files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.123"),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.bwb"),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Text files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.me"),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.txt"),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Word Processor files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.chi"),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.doc"),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Testing non-exiting extensions...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.qqq"),DEFAULT_SIZE)).isNull();
		assertThat(Icons.iconFor(new File("/tmp/test-file.xxx"),DEFAULT_SIZE)).isNull();

	}

	/**
	 * Test of iconFor method, of class Icons.
	 */
	@Test
	public void testIconFor_Object() {

		//	No default provider, so null is the only result possible.
		System.out.println("  Testing 'iconFor(Object)'...");


		assertThat(Icons.iconFor(FILE, DEFAULT_SIZE))
			.isNotNull()
			.isInstanceOf(Text.class)
			.hasFieldOrPropertyWithValue("text", String.valueOf(FontAwesome.FILE_O.getCode()));
		assertThat(Icons.iconFor(FILE_EXECUTABLE, DEFAULT_SIZE))
			.isNotNull()
			.isInstanceOf(Text.class)
			.hasFieldOrPropertyWithValue("text", String.valueOf(FontAwesome.PLAY_CIRCLE_O.getCode()));
		assertThat(Icons.iconFor(FILE_HIDDEN, DEFAULT_SIZE))
			.isNotNull()
			.isInstanceOf(Text.class)
			.hasFieldOrPropertyWithValue("text", String.valueOf(FontAwesome.FILE.getCode()));
		assertThat(Icons.iconFor(FILE_LINK, DEFAULT_SIZE))
			.isNotNull()
			.isInstanceOf(Text.class)
			.hasFieldOrPropertyWithValue("text", String.valueOf(FontAwesome.LINK.getCode()));
		assertThat(Icons.iconFor(FOLDER_COLLAPSED, DEFAULT_SIZE))
			.isNotNull()
			.isInstanceOf(Text.class)
			.hasFieldOrPropertyWithValue("text", String.valueOf(FontAwesome.FOLDER_O.getCode()));
		assertThat(Icons.iconFor(FOLDER_EXPANDED, DEFAULT_SIZE))
			.isNotNull()
			.isInstanceOf(Text.class)
			.hasFieldOrPropertyWithValue("text", String.valueOf(FontAwesome.FOLDER_OPEN_O.getCode()));
		assertThat(Icons.iconFor(SQUARE_DOWN, DEFAULT_SIZE))
			.isNotNull()
			.isInstanceOf(Text.class)
			.hasFieldOrPropertyWithValue("text", String.valueOf(FontAwesome.CARET_SQUARE_O_DOWN.getCode()));
		assertThat(Icons.iconFor(SQUARE_LEFT, DEFAULT_SIZE))
			.isNotNull()
			.isInstanceOf(Text.class)
			.hasFieldOrPropertyWithValue("text", String.valueOf(FontAwesome.CARET_SQUARE_O_LEFT.getCode()));
		assertThat(Icons.iconFor(SQUARE_RIGHT, DEFAULT_SIZE))
			.isNotNull()
			.isInstanceOf(Text.class)
			.hasFieldOrPropertyWithValue("text", String.valueOf(FontAwesome.CARET_SQUARE_O_RIGHT.getCode()));
		assertThat(Icons.iconFor(SQUARE_UP, DEFAULT_SIZE))
			.isNotNull()
			.isInstanceOf(Text.class)
			.hasFieldOrPropertyWithValue("text", String.valueOf(FontAwesome.CARET_SQUARE_O_UP.getCode()));

		assertThat(Icons.iconFor("txt", DEFAULT_SIZE)).isNull();
		assertThat(Icons.iconFor(new Object(), DEFAULT_SIZE)).isNull();
		assertThat(Icons.iconFor(this, DEFAULT_SIZE)).isNull();

	}

	/**
	 * Test of iconFor method, of class Icons.
	 */
	@Test
	public void testIconFor_Path() {

		System.out.println("  Testing 'iconFor(Path)'...");

		//	--------------------------------------------------------------------
		System.out.println("    Archive files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.7x").toPath(),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.a##").toPath(),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Audio/Music files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.4md").toPath(),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.668").toPath(),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Code files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.4th").toPath(),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.8").toPath(),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Database files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.3dt").toPath(),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.4db").toPath(),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Image/Picture files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.555").toPath(),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.75").toPath(),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Movie/Video files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.byu").toPath(),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.f4a").toPath(),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    PDF files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.pdf").toPath(),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Presentation/Powerpoint files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.ch4").toPath(),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.key").toPath(),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Spreadsheet files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.123").toPath(),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.bwb").toPath(),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Text files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.1st").toPath(),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.602").toPath(),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Word Processor files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.chi").toPath(),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.doc").toPath(),DEFAULT_SIZE)).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Testing non-exiting extensions...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.qqq").toPath(),DEFAULT_SIZE)).isNull();
		assertThat(Icons.iconFor(new File("/tmp/test-file.xxx").toPath(),DEFAULT_SIZE)).isNull();

	}

}
