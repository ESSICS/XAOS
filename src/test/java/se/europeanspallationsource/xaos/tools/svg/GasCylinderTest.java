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
package se.europeanspallationsource.xaos.tools.svg;


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
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testfx.assertions.api.Assertions.assertThat;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class GasCylinderTest extends ApplicationTest {
	
	private SVGContent svgContent;

	@Override
	public void start( Stage stage ) throws IOException, XMLStreamException {

		svgContent = SVGLoader.load(GasCylinderTest.class.getResource("/svg/gas-cylinder.svg"));

		svgContent.setId("Loaded SVG Image");

		stage.setScene(new Scene(svgContent));
		stage.show();

	}

	@After
	public void tearDown() throws TimeoutException {
		FxToolkit.cleanupStages();
	}

	@Test
	public void testLoadSVG() {

		assertThat(svgContent).isExactlyInstanceOf(SVGContent.class);
		assertThat(svgContent).hasExactlyNumChildren(1);

		Node svgNode = svgContent.getChildren().get(0);

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
