/*
 * Copyright 2018 claudiorosati.
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
package se.europeanspallationsource.xaos.ui;


import io.github.classgraph.ClassGraph;
import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import javafx.embed.swing.SwingNode;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.ParallelCamera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
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
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author claudiorosati
 */
@SuppressWarnings( { "UseOfSystemOutOrSystemErr", "ClassWithoutLogger" } )
public class IconsTest {

	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- IconsTest -------------------------------------------------");
	}

	/**
	 * Test of iconForClass method, of class Icons.
	 */
	@Test
	public void testIconForClass_String() {

		System.out.println("  Testing ''iconForClass''...");

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
						.enableSystemPackages()
						.whitelistPackages(packageName)
						.scan()
						.getAllClasses()
						.getNames()
				);
			});

		classNames.forEach(className -> {
			if ( supportedClasses.contains(className) ) {
				supportedClasses.remove(className);
				assertThat(Icons.iconForClass(className)).isNotNull().isInstanceOf(ImageView.class);
			} else {
				assertThat(Icons.iconFor(className)).isNull();
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

		System.out.println("  Testing ''iconForFileExtension''...");

		Node icon;

		//	--------------------------------------------------------------------
		System.out.println("    Archive files...");

		icon = FontIcon.of(FontAwesome.FILE_ARCHIVE_O);

		assertThat(Icons.iconForFileExtension("7x")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("a##")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("acb")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ace")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ar7")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("arc")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ari")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("arj")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ark")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("arx")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("b1")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ba")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("bs2")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("bsa")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("bz2")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("dmg")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("dwc")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("gz")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("hbc")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("hbe")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("hpk")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("hqx")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("jar")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("lif")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("lzw")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("lzx")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("maff")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("mar")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pka")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pkg")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pma")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ppk")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("rar")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("rpm")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("sp")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("tar")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("taz")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("tbz")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("tgz")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("tpz")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("tz")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("tzb")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("uc2")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ucn")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("war")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("x")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("yz")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("z")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("zip")).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Audio/Music files...");

		icon = FontIcon.of(FontAwesome.FILE_AUDIO_O);

		assertThat(Icons.iconForFileExtension("4md")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("668")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("669")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("6cm")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("8cm")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("aac")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ad2")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ad3")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("aif")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("aifc")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("aiff")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("amr")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ams")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ape")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("asf")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("au")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("aud")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("audio")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("cda")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("cdm")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("cfa")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("enc")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("flac")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("m4a")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("m4r")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("mp2")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("mp3")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("mpa")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("nxt")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ogg")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("omg")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("opus")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("sf")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("sfl")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("smp")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("snd")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("son")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("sound")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("voc")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("wav")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("wave")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("wma")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("xa")).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Code files...");

		icon = FontIcon.of(FontAwesome.FILE_CODE_O);

		assertThat(Icons.iconForFileExtension("4th")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("8")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("a")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("a80")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("act")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ada")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("adb")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ads")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ash")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("asi")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("asm")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("bas")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("bat")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("bi")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("c--")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("c++")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("c")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("cas")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("cbl")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("cc")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("cl")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("coffee")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("cs")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("cxx")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("d")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("di")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("e")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("el")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("erl")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("esh")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("f")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("f4")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("f77")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("f90")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("f95")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("for")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("gc1")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("gc3")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("go")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("h--")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("h++")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("h")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("hh")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("hpp")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("htm")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("html")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("hxx")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ipynb")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("jav")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("java")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("js")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("json")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("kcl")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("l")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("lisp")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("m")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("mak")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("mm")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("p")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pas")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("py")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("r")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("rb")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("s")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("sh")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("sm")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("st")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("swift")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("tcl")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("xml")).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Database files...");

		icon = FontIcon.of(FontAwesome.DATABASE);

		assertThat(Icons.iconForFileExtension("3dt")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("4db")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("4dindy")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ab6")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ab8")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("accda")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("accdb")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("accde")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("accdt")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("accdu")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("accft")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ap")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("bib")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("cac")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("cdb")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("crp")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("db")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("db2")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("db3")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("dbf")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("dbk")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("dbx")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("dtf")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("fm")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("fp3")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("fp4")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("fp5")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("fp7")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("fw")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("fw2")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("fw3")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("idb")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ldb")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("mat")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("mdf")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ndb")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("phf")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("res")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("rpd")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("tdb")).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Image/Picture files...");

		icon = FontIcon.of(FontAwesome.FILE_IMAGE_O);

		assertThat(Icons.iconForFileExtension("555")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("75")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("a11")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("acmb")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ais")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("art")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("b&w")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("b_w")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("b1n")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("b8")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("bga")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("bif")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("bmp")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("cbm")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("cco")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("cdf")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ceg")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("cgm")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("cr2")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("cut")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("dcs")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ddb")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("dem")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("eps")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("gif")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("gry")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("hdw")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("iax")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ica")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("icb")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ico")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("idw")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("j2c")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("jff")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("jfif")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("jif")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("jp2")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("jpc")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("jpeg")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("jpg")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("miff")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("msp")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pcd")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pct")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pcx")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pda")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pdn")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pdd")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pic")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pict")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pix")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("png")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pnt")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ppm")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ras")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("raw")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("rgb")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("rif")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("riff")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("rl4")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("rl8")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("rla")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("rlb")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("rlc")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("sg1")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("sgi")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("spi")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("spiff")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("sun")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("tga")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("tif")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("tiff")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("vda")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("vgr")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("vif")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("viff")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("vpg")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("wim")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("wpg")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("xbm")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("xcf")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("xif")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("xpm")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("xwd")).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Movie/Video files...");

		icon = FontIcon.of(FontAwesome.FILE_MOVIE_O);

		assertThat(Icons.iconForFileExtension("byu")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("f4a")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("f4b")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("f4p")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("f4v")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("fmv")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("m2ts")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("m4p")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("m4v")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("mov")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("mp4")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("mpeg")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("mpg")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("mpx")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("mts")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("qt")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("qtvr")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("sdc")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("tmf")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("trp")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ts")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ty")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("vob")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("vue")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("wmv")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("xmv")).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    PDF files...");

		icon = FontIcon.of(FontAwesome.FILE_PDF_O);

		assertThat(Icons.iconForFileExtension("pdf")).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Presentation/Powerpoint files...");

		icon = FontIcon.of(FontAwesome.FILE_POWERPOINT_O);

		assertThat(Icons.iconForFileExtension("ch4")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("key")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("odp")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pcs")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pot")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pps")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ppt")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pptx")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("psx")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("shw")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("sxi")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("uop")).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Spreadsheet files...");

		icon = FontIcon.of(FontAwesome.FILE_EXCEL_O);

		assertThat(Icons.iconForFileExtension("123")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("bwb")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("cal")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("col")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("fm1")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("fm3")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("lcw")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("lss")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("mdl")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ods")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("qbw")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("slk")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("sxc")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("uos")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("vc")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("wk1")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("wk3")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("wk4")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("wke")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("wki")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("wkq")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("wks")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("wkz")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("wq1")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("wr1")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("xla")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("xlb")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("xlc")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("xld")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("xlk")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("xll")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("xlm")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("xls")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("xlsb")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("xlsm")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("xlsx")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("xlt")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("xlv")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("xlw")).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Text files...");

		icon = FontIcon.of(FontAwesome.FILE_TEXT_O);

		assertThat(Icons.iconForFileExtension("1st")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("602")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("asc")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ftxt")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("hex")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("inf")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("log")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("me")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("readme")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("text")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("txt")).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Word Processor files...");

		icon = FontIcon.of(FontAwesome.FILE_WORD_O);

		assertThat(Icons.iconForFileExtension("chi")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("doc")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("docm")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("docx")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("dot")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("lwp")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("odm")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("odt")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("ott")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pages")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pm3")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pm4")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pm5")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pt3")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pt4")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pt5")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("pwp")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("sxw")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("uot")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("wkb")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("wp")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("wp5")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("wpd")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("wri")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForFileExtension("xlr")).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Testing non-exiting extensions...");

		assertThat(Icons.iconForFileExtension("qqq")).isNull();
		assertThat(Icons.iconForFileExtension("xxx")).isNull();
		assertThat(Icons.iconForFileExtension("777")).isNull();

	}

	/**
	 * Test of iconForMIMEType method, of class Icons.
	 */
	@Test
	public void testIconForMIMEType() {

		System.out.println("  Testing ''iconForMIMEType''...");

		Node icon;

		//	--------------------------------------------------------------------
		System.out.println("    Archive files...");

		icon = FontIcon.of(FontAwesome.FILE_ARCHIVE_O);

		assertThat(Icons.iconForMIMEType("application/gzip")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("application/zip")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("application/zlib")).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Audio/Music files...");

		icon = FontIcon.of(FontAwesome.FILE_AUDIO_O);

		assertThat(Icons.iconForMIMEType("audio/1d-interleaved-parityfec")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/32kadpcm")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/3gpp")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/3gpp2")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/aac")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/ac3")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/AMR")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/AMR-WB")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/amr-wb+")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/aptx")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/asc")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/ATRAC-ADVANCED-LOSSLESS")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/ATRAC-X")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/ATRAC3")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/basic")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/BV16")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/BV32")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/clearmode")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/CN")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/DAT12")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/dls")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/dsr-es201108")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/dsr-es202050")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/dsr-es202211")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/dsr-es202212")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/DV")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/DVI4")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/eac3")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/encaprtp")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/EVRC")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/EVRC-QCP")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/EVRC0")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/EVRC1")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/EVRCB")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/EVRCB0")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/EVRCB1")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/EVRCNW")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/EVRCNW0")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/EVRCNW1")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/EVRCWB")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/EVRCWB0")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/EVRCWB1")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/EVS")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/example")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/fwdred")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/G711-0")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/G719")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/G7221")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/G722")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/G723")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/G726-16")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/G726-24")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/G726-32")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/G726-40")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/G728")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/G729")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/G7291")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/G729D")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/G729E")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/GSM")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/GSM-EFR")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/GSM-HR-08")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/iLBC")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/ip-mr_v2.5")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/L8")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/L16")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/L20")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/L24")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/LPC")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/MELP")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/MELP600")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/MELP1200")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/MELP2400")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/mobile-xmf")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/MPA")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/mp4")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/MP4A-LATM")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/mpa-robust")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/mpeg")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/mpeg4-generic")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/ogg")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/opus")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/parityfec")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/PCMA")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/PCMA-WB")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/PCMU")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/PCMU-WB")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/prs.sid")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/QCELP")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/raptorfec")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/RED")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/rtp-enc-aescm128")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/rtploopback")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/rtp-midi")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/rtx")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/SMV")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/SMV0")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/SMV-QCP")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/sp-midi")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/speex")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/t140c")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/t38")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/telephone-event")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/tone")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/UEMCLIP")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/ulpfec")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/usac")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/VDVI")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/VMR-WB")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.3gpp.iufp")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.4SB")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.audiokoz")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.CELP")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.cisco.nse")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.cmles.radio-events")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.cns.anp1")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.cns.inf1")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.dece.audio")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.digital-winds")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.dlna.adts")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.dolby.heaac.1")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.dolby.heaac.2")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.dolby.mlp")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.dolby.mps")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.dolby.pl2")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.dolby.pl2x")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.dolby.pl2z")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.dolby.pulse.1")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.dra")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.dts")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.dts.hd")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.dvb.file")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.everad.plj")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.hns.audio")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.lucent.voice")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.ms-playready.media.pya")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.nokia.mobile-xmf")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.nortel.vbk")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.nuera.ecelp4800")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.nuera.ecelp7470")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.nuera.ecelp9600")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.octel.sbc")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.presonus.multitrack")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.qcelp - DEPRECATED in favor of audio/qcelp")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.rhetorex.32kadpcm")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.rip")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.sealedmedia.softseal.mpeg")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vnd.vmx.cvsd")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vorbis")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("audio/vorbis-config")).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Code files...");

		icon = FontIcon.of(FontAwesome.FILE_CODE_O);

		assertThat(Icons.iconForMIMEType("application/ecmascript")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("application/javascript")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("application/json")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("application/json-patch+json")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("application/json-seq")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("application/xml")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("application/xml-dtd")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("application/xml-external-parsed-entity")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("application/xml-patch+xml")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("text/css")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("text/html")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("text/javascript")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("text/xml")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("text/xml-external-parsed-entity")).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Database files...");

		icon = FontIcon.of(FontAwesome.DATABASE);

		assertThat(Icons.iconForMIMEType("application/sql")).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Image/Picture files...");

		icon = FontIcon.of(FontAwesome.FILE_IMAGE_O);

		assertThat(Icons.iconForMIMEType("image/aces")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/avci")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/avcs")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/bmp")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/cgm")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/dicom-rle")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/emf")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/example")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/fits")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/g3fax")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/gif")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/heic")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/heic-sequence")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/heif")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/heif-sequence")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/ief")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/jls")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/jp2")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/jpeg")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/jpm")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/jpx")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/ktx")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/naplps")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/png")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/prs.btif")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/prs.pti")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/pwg-raster")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/svg+xml")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/t38")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/tiff")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/tiff-fx")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.adobe.photoshop")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.airzip.accelerator.azv")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.cns.inf2")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.dece.graphic")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.djvu")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.dwg")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.dxf")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.dvb.subtitle")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.fastbidsheet")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.fpx")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.fst")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.fujixerox.edmics-mmr")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.fujixerox.edmics-rlc")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.globalgraphics.pgb")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.microsoft.icon")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.mix")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.ms-modi")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.mozilla.apng")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.net-fpx")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.radiance")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.sealed.png")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.sealedmedia.softseal.gif")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.sealedmedia.softseal.jpg")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.svf")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.tencent.tap")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.valve.source.texture")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.wap.wbmp")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.xiff")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/vnd.zbrush.pcx")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/wmf")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/x-emf")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("image/x-wmf")).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Movie/Video files...");

		icon = FontIcon.of(FontAwesome.FILE_MOVIE_O);

		assertThat(Icons.iconForMIMEType("application/mp4")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("application/mpeg4-generic")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("application/mpeg4-iod")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("application/mpeg4-iod-xmt")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("application/ogg")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/1d-interleaved-parityfec")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/3gpp")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/3gpp2")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/3gpp-tt")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/BMPEG")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/BT656")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/CelB")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/DV")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/encaprtp")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/example")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/H261")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/H263")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/H263-1998")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/H263-2000")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/H264")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/H264-RCDO")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/H264-SVC")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/H265")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/iso.segment")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/JPEG")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/jpeg2000")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/mj2")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/MP1S")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/MP2P")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/MP2T")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/mp4")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/MP4V-ES")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/MPV")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/mpeg")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/mpeg4-generic")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/nv")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/ogg")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/parityfec")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/pointer")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/quicktime")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/raptorfec")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/raw")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/rtp-enc-aescm128")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/rtploopback")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/rtx")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/smpte291")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/SMPTE292M")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/ulpfec")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vc1")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.CCTV")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.dece.hd")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.dece.mobile")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.dece.mp4")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.dece.pd")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.dece.sd")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.dece.video")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.directv.mpeg")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.directv.mpeg-tts")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.dlna.mpeg-tts")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.dvb.file")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.fvt")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.hns.video")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.iptvforum.1dparityfec-1010")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.iptvforum.1dparityfec-2005")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.iptvforum.2dparityfec-1010")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.iptvforum.2dparityfec-2005")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.iptvforum.ttsavc")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.iptvforum.ttsmpeg2")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.motorola.video")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.motorola.videop")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.mpegurl")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.ms-playready.media.pyv")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.nokia.interleaved-multimedia")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.nokia.mp4vr")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.nokia.videovoip")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.objectvideo")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.radgamettools.bink")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.radgamettools.smacker")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.sealed.mpeg1")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.sealed.mpeg4")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.sealed.swf")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.sealedmedia.softseal.mov")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.uvvu.mp4")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/vnd.vivo")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("video/VP8")).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    PDF files...");

		icon = FontIcon.of(FontAwesome.FILE_PDF_O);

		assertThat(Icons.iconForMIMEType("application/pdf")).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
//		System.out.println("    Presentation/Powerpoint files...");
//
//		icon = FontIcon.of(FontAwesome.FILE_POWERPOINT_O);
//
//		assertThat(Icons.iconForMIMEType("xxx/xxx")).isEqualToComparingFieldByFieldRecursively(icon);
		//	--------------------------------------------------------------------
//		System.out.println("    Spreadsheet files...");
//
//		icon = FontIcon.of(FontAwesome.FILE_EXCEL_O);
//
//		assertThat(Icons.iconForMIMEType("xxx/xxx")).isEqualToComparingFieldByFieldRecursively(icon);
		//	--------------------------------------------------------------------
		System.out.println("    Text files...");

		icon = FontIcon.of(FontAwesome.FILE_TEXT_O);

		assertThat(Icons.iconForMIMEType("application/rtf")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("text/csv")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("text/markdown")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("text/plain")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("text/richtext")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("text/rtf")).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconForMIMEType("text/strings")).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Word Processor files...");

		icon = FontIcon.of(FontAwesome.FILE_WORD_O);

		assertThat(Icons.iconForMIMEType("application/msword")).isEqualToComparingFieldByFieldRecursively(icon);

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

		System.out.println("  Testing ''iconFor(Class)''...");

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
		assertThat(Icons.iconFor(Object.class)).isNull();

		//	Testing all other classes...
		new HashSet<Class<?>>(supportedClasses).forEach(type -> {
			if ( supportedClasses.contains(type) ) {
				supportedClasses.remove(type);
				assertThat(Icons.iconFor(type)).isNotNull().isInstanceOf(ImageView.class);
			} else {
				assertThat(Icons.iconFor(type)).isNull();
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

		System.out.println("  Testing ''iconFor(File)''...");

		Node icon;

		//	--------------------------------------------------------------------
		System.out.println("    Archive files...");

		icon = FontIcon.of(FontAwesome.FILE_ARCHIVE_O);

		assertThat(Icons.iconFor(new File("/tmp/test-file.7x"))).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconFor(new File("/tmp/test-file.a##"))).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Audio/Music files...");

		icon = FontIcon.of(FontAwesome.FILE_AUDIO_O);

		assertThat(Icons.iconFor(new File("/tmp/test-file.4md"))).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconFor(new File("/tmp/test-file.668"))).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Code files...");

		icon = FontIcon.of(FontAwesome.FILE_CODE_O);

		assertThat(Icons.iconFor(new File("/tmp/test-file.4th"))).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconFor(new File("/tmp/test-file.8"))).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Database files...");

		icon = FontIcon.of(FontAwesome.DATABASE);

		assertThat(Icons.iconFor(new File("/tmp/test-file.3dt"))).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconFor(new File("/tmp/test-file.4db"))).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Image/Picture files...");

		icon = FontIcon.of(FontAwesome.FILE_IMAGE_O);

		assertThat(Icons.iconFor(new File("/tmp/test-file.555"))).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconFor(new File("/tmp/test-file.75"))).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Movie/Video files...");

		icon = FontIcon.of(FontAwesome.FILE_MOVIE_O);

		assertThat(Icons.iconFor(new File("/tmp/test-file.byu"))).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconFor(new File("/tmp/test-file.f4a"))).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    PDF files...");

		icon = FontIcon.of(FontAwesome.FILE_PDF_O);

		assertThat(Icons.iconFor(new File("/tmp/test-file.pdf"))).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Presentation/Powerpoint files...");

		icon = FontIcon.of(FontAwesome.FILE_POWERPOINT_O);

		assertThat(Icons.iconFor(new File("/tmp/test-file.ch4"))).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconFor(new File("/tmp/test-file.key"))).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Spreadsheet files...");

		icon = FontIcon.of(FontAwesome.FILE_EXCEL_O);

		assertThat(Icons.iconFor(new File("/tmp/test-file.123"))).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconFor(new File("/tmp/test-file.bwb"))).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Text files...");

		icon = FontIcon.of(FontAwesome.FILE_TEXT_O);

		assertThat(Icons.iconFor(new File("/tmp/test-file.1st"))).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconFor(new File("/tmp/test-file.602"))).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Word Processor files...");

		icon = FontIcon.of(FontAwesome.FILE_WORD_O);

		assertThat(Icons.iconFor(new File("/tmp/test-file.chi"))).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconFor(new File("/tmp/test-file.doc"))).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Testing non-exiting extensions...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.qqq"))).isNull();
		assertThat(Icons.iconFor(new File("/tmp/test-file.xxx"))).isNull();

	}

	/**
	 * Test of iconFor method, of class Icons.
	 */
	@Test
	public void testIconFor_Object() {

		//	No default provider, so null is the only result possible.
		System.out.println("  Testing ''iconFor(Object)''...");

		assertThat(Icons.iconFor("txt")).isNull();
		assertThat(Icons.iconFor(new Object())).isNull();
		assertThat(Icons.iconFor(this)).isNull();

	}

	/**
	 * Test of iconFor method, of class Icons.
	 */
	@Test
	public void testIconFor_Path() {

		System.out.println("  Testing ''iconFor(Path)''...");

		Node icon;

		//	--------------------------------------------------------------------
		System.out.println("    Archive files...");

		icon = FontIcon.of(FontAwesome.FILE_ARCHIVE_O);

		assertThat(Icons.iconFor(new File("/tmp/test-file.7x").toPath())).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconFor(new File("/tmp/test-file.a##").toPath())).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Audio/Music files...");

		icon = FontIcon.of(FontAwesome.FILE_AUDIO_O);

		assertThat(Icons.iconFor(new File("/tmp/test-file.4md").toPath())).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconFor(new File("/tmp/test-file.668").toPath())).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Code files...");

		icon = FontIcon.of(FontAwesome.FILE_CODE_O);

		assertThat(Icons.iconFor(new File("/tmp/test-file.4th").toPath())).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconFor(new File("/tmp/test-file.8").toPath())).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Database files...");

		icon = FontIcon.of(FontAwesome.DATABASE);

		assertThat(Icons.iconFor(new File("/tmp/test-file.3dt").toPath())).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconFor(new File("/tmp/test-file.4db").toPath())).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Image/Picture files...");

		icon = FontIcon.of(FontAwesome.FILE_IMAGE_O);

		assertThat(Icons.iconFor(new File("/tmp/test-file.555").toPath())).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconFor(new File("/tmp/test-file.75").toPath())).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Movie/Video files...");

		icon = FontIcon.of(FontAwesome.FILE_MOVIE_O);

		assertThat(Icons.iconFor(new File("/tmp/test-file.byu").toPath())).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconFor(new File("/tmp/test-file.f4a").toPath())).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    PDF files...");

		icon = FontIcon.of(FontAwesome.FILE_PDF_O);

		assertThat(Icons.iconFor(new File("/tmp/test-file.pdf").toPath())).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Presentation/Powerpoint files...");

		icon = FontIcon.of(FontAwesome.FILE_POWERPOINT_O);

		assertThat(Icons.iconFor(new File("/tmp/test-file.ch4").toPath())).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconFor(new File("/tmp/test-file.key").toPath())).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Spreadsheet files...");

		icon = FontIcon.of(FontAwesome.FILE_EXCEL_O);

		assertThat(Icons.iconFor(new File("/tmp/test-file.123").toPath())).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconFor(new File("/tmp/test-file.bwb").toPath())).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Text files...");

		icon = FontIcon.of(FontAwesome.FILE_TEXT_O);

		assertThat(Icons.iconFor(new File("/tmp/test-file.1st").toPath())).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconFor(new File("/tmp/test-file.602").toPath())).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Word Processor files...");

		icon = FontIcon.of(FontAwesome.FILE_WORD_O);

		assertThat(Icons.iconFor(new File("/tmp/test-file.chi").toPath())).isEqualToComparingFieldByFieldRecursively(icon);
		assertThat(Icons.iconFor(new File("/tmp/test-file.doc").toPath())).isEqualToComparingFieldByFieldRecursively(icon);

		//	--------------------------------------------------------------------
		System.out.println("    Testing non-exiting extensions...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.qqq").toPath())).isNull();
		assertThat(Icons.iconFor(new File("/tmp/test-file.xxx").toPath())).isNull();

	}

}
