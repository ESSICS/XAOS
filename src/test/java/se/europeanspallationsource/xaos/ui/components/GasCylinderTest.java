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
package se.europeanspallationsource.xaos.ui.components;


import java.io.IOException;
import java.util.concurrent.TimeoutException;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javax.xml.stream.XMLStreamException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import static org.testfx.assertions.api.Assertions.assertThat;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "ClassWithoutLogger", "UseOfSystemOutOrSystemErr" } )
public class GasCylinderTest extends ApplicationTest {
	
	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- GasCylinderTest -------------------------------------------");
	}

	private SVG svg;

	@Override
	public void start( Stage stage ) throws IOException, XMLStreamException {

		svg = SVG.load(GasCylinderTest.class.getResource("/svg/gas-cylinder.svg"));

		svg.setId("Loaded SVG Image");

		stage.setScene(new Scene(svg));
		stage.show();

	}

	@After
	public void tearDown() throws TimeoutException {
		FxToolkit.cleanupStages();
	}

	@Test
	public void testLoadSVG() {

		System.out.println("  Testing ''load''...");

		assertThat(svg).isExactlyInstanceOf(SVG.class);
		assertThat(svg).hasExactlyNumChildren(1);

		Node svgNode = svg.getChildren().get(0);

		assertThat(svgNode).isExactlyInstanceOf(Group.class);
		assertThat((Parent) svgNode).hasExactlyNumChildren(1);

		Node g0 = ((Group) svgNode).getChildren().get(0);

		assertThat(g0).isExactlyInstanceOf(Group.class);
		assertThat((Parent) g0).hasExactlyNumChildren(2);

		Node p0 = ((Group) g0).getChildren().get(0);

		assertThat(p0).isExactlyInstanceOf(SVGPath.class);

		SVGPath sp0 = (SVGPath) p0;

		assertThat(sp0.getFill()).isEqualTo(Color.TRANSPARENT);
		assertThat(sp0.getStroke()).isEqualTo(Color.web("#000000"));
		assertThat(sp0.getStrokeMiterLimit()).isEqualTo(10);
		assertThat(sp0.getTransforms().size()).isEqualTo(0);

		Node p1 = ((Group) g0).getChildren().get(1);

		assertThat(p1).isExactlyInstanceOf(SVGPath.class);

		SVGPath sp1 = (SVGPath) p1;

		assertThat(sp1.getFill()).isEqualTo(Color.TRANSPARENT);
		assertThat(sp1.getStroke()).isEqualTo(Color.web("#770000"));
		assertThat(sp1.getStrokeMiterLimit()).isEqualTo(10);
		assertThat(sp1.getTransforms().size()).isEqualTo(0);

	}

}
