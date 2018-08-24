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

		//	--------------------------------------------------------------------
		System.out.println("    Archive files...");

		assertThat(Icons.iconForFileExtension("7x")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("a##")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("acb")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ace")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ar7")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("arc")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ari")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("arj")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ark")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("arx")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("b1")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ba")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("bs2")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("bsa")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("bz2")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("dmg")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("dwc")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("gz")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("hbc")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("hbe")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("hpk")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("hqx")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("jar")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("lif")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("lzw")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("lzx")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("maff")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mar")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pka")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pkg")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pma")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ppk")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("rar")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("rpm")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sp")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("tar")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("taz")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("tbz")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("tgz")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("tpz")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("tz")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("tzb")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("uc2")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ucn")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("war")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("x")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("yz")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("z")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("zip")).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Audio/Music files...");

		assertThat(Icons.iconForFileExtension("4md")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("668")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("669")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("6cm")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("8cm")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("aac")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ad2")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ad3")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("aif")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("aifc")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("aiff")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("amr")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ams")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ape")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("asf")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("au")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("aud")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("audio")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cda")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cdm")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cfa")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("enc")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("flac")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("m4a")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("m4r")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mp2")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mp3")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mpa")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("nxt")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ogg")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("omg")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("opus")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sf")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sfl")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("smp")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("snd")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("son")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sound")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("voc")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wav")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wave")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wma")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xa")).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Code files...");

		assertThat(Icons.iconForFileExtension("4th")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("8")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("a")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("a80")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("act")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ada")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("adb")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ads")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ash")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("asi")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("asm")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("bas")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("bat")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("bi")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("c--")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("c++")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("c")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cas")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cbl")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cc")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cl")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("coffee")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cs")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cxx")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("d")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("di")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("e")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("el")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("erl")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("esh")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("f")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("f4")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("f77")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("f90")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("f95")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("for")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("gc1")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("gc3")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("go")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("h--")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("h++")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("h")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("hh")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("hpp")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("htm")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("html")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("hxx")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ipynb")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("jav")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("java")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("js")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("json")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("kcl")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("l")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("lisp")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("m")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mak")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mm")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("p")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pas")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("py")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("r")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("rb")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("s")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sh")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sm")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("st")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("swift")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("tcl")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xml")).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Database files...");

		assertThat(Icons.iconForFileExtension("3dt")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("4db")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("4dindy")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ab6")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ab8")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("accda")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("accdb")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("accde")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("accdt")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("accdu")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("accft")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ap")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("bib")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cac")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cdb")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("crp")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("db")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("db2")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("db3")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("dbf")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("dbk")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("dbx")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("dtf")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("fm")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("fp3")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("fp4")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("fp5")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("fp7")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("fw")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("fw2")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("fw3")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("idb")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ldb")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mat")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mdf")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ndb")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("phf")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("res")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("rpd")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("tdb")).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Image/Picture files...");

		assertThat(Icons.iconForFileExtension("555")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("75")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("a11")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("acmb")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ais")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("art")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("b&w")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("b_w")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("b1n")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("b8")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("bga")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("bif")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("bmp")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cbm")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cco")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cdf")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ceg")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cgm")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cr2")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cut")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("dcs")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ddb")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("dem")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("eps")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("gif")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("gry")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("hdw")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("iax")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ica")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("icb")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ico")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("idw")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("j2c")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("jff")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("jfif")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("jif")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("jp2")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("jpc")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("jpeg")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("jpg")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("miff")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("msp")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pcd")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pct")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pcx")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pda")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pdn")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pdd")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pic")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pict")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pix")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("png")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pnt")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ppm")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ras")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("raw")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("rgb")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("rif")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("riff")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("rl4")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("rl8")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("rla")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("rlb")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("rlc")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sg1")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sgi")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("spi")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("spiff")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sun")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("tga")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("tif")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("tiff")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("vda")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("vgr")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("vif")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("viff")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("vpg")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wim")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wpg")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xbm")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xcf")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xif")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xpm")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xwd")).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Movie/Video files...");

		assertThat(Icons.iconForFileExtension("byu")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("f4a")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("f4b")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("f4p")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("f4v")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("fmv")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("m2ts")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("m4p")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("m4v")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mov")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mp4")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mpeg")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mpg")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mpx")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mts")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("qt")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("qtvr")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sdc")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("tmf")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("trp")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ts")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ty")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("vob")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("vue")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wmv")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xmv")).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    PDF files...");

		assertThat(Icons.iconForFileExtension("pdf")).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Presentation/Powerpoint files...");

		assertThat(Icons.iconForFileExtension("ch4")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("key")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("odp")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pcs")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pot")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pps")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ppt")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pptx")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("psx")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("shw")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sxi")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("uop")).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Spreadsheet files...");

		assertThat(Icons.iconForFileExtension("123")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("bwb")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("cal")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("col")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("fm1")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("fm3")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("lcw")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("lss")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("mdl")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ods")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("qbw")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("slk")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sxc")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("uos")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("vc")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wk1")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wk3")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wk4")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wke")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wki")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wkq")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wks")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wkz")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wq1")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wr1")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xla")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xlb")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xlc")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xld")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xlk")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xll")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xlm")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xls")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xlsb")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xlsm")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xlsx")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xlt")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xlv")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xlw")).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Text files...");

		assertThat(Icons.iconForFileExtension("1st")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("602")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("asc")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ftxt")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("hex")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("inf")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("log")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("me")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("readme")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("text")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("txt")).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Word Processor files...");

		assertThat(Icons.iconForFileExtension("chi")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("doc")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("docm")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("docx")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("dot")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("lwp")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("odm")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("odt")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("ott")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pages")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pm3")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pm4")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pm5")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pt3")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pt4")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pt5")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("pwp")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("sxw")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("uot")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wkb")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wp")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wp5")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wpd")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("wri")).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconForFileExtension("xlr")).isNotNull().isInstanceOf(Text.class);

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
		assertThat(Icons.iconForMIMEType("audio/vnd.qcelp - DEPRECATED in favor of audio/qcelp")).isNotNull().isInstanceOf(Text.class);
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
		new HashSet<>(supportedClasses).forEach(type -> {
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

		//	--------------------------------------------------------------------
		System.out.println("    Archive files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.7x"))).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.a##"))).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Audio/Music files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.4md"))).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.668"))).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Code files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.4th"))).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.8"))).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Database files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.3dt"))).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.4db"))).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Image/Picture files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.555"))).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.75"))).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Movie/Video files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.byu"))).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.f4a"))).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    PDF files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.pdf"))).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Presentation/Powerpoint files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.ch4"))).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.key"))).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Spreadsheet files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.123"))).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.bwb"))).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Text files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.me"))).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.txt"))).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Word Processor files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.chi"))).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.doc"))).isNotNull().isInstanceOf(Text.class);

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

		//	--------------------------------------------------------------------
		System.out.println("    Archive files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.7x").toPath())).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.a##").toPath())).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Audio/Music files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.4md").toPath())).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.668").toPath())).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Code files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.4th").toPath())).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.8").toPath())).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Database files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.3dt").toPath())).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.4db").toPath())).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Image/Picture files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.555").toPath())).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.75").toPath())).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Movie/Video files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.byu").toPath())).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.f4a").toPath())).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    PDF files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.pdf").toPath())).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Presentation/Powerpoint files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.ch4").toPath())).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.key").toPath())).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Spreadsheet files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.123").toPath())).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.bwb").toPath())).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Text files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.1st").toPath())).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.602").toPath())).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Word Processor files...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.chi").toPath())).isNotNull().isInstanceOf(Text.class);
		assertThat(Icons.iconFor(new File("/tmp/test-file.doc").toPath())).isNotNull().isInstanceOf(Text.class);

		//	--------------------------------------------------------------------
		System.out.println("    Testing non-exiting extensions...");

		assertThat(Icons.iconFor(new File("/tmp/test-file.qqq").toPath())).isNull();
		assertThat(Icons.iconFor(new File("/tmp/test-file.xxx").toPath())).isNull();

	}

}
