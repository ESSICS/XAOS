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
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.transform.MatrixType;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javax.xml.stream.XMLStreamException;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import static org.testfx.assertions.api.Assertions.assertThat;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class AtomTest extends ApplicationTest {
	
	private SVGContent svgContent;

	@Override
	public void start( Stage stage ) throws IOException, XMLStreamException {

		svgContent = SVGLoader.load(AtomTest.class.getResource("/svg/atom.svg"));

		svgContent.setId("Loaded SVG Image");

		Bounds layoutBounds = svgContent.getLayoutBounds();

		svgContent.setTranslateX(- layoutBounds.getMinX());
		svgContent.setTranslateY(- layoutBounds.getMinY());

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
		assertThat((Parent) svgNode).hasExactlyNumChildren(2);

		Node g0 = ((Group) svgNode).getChildren().get(0);

		assertThat(g0).isExactlyInstanceOf(Group.class);
		assertThat((Parent) g0).hasExactlyNumChildren(4);

		Node e0 = ((Group) g0).getChildren().get(0);

		assertThat(e0).isExactlyInstanceOf(Ellipse.class);

		Ellipse el0 = (Ellipse) e0;

		assertThat(el0.getFill()).isEqualTo(Color.TRANSPARENT);
		assertThat(el0.getStroke()).isEqualTo(Color.web("#66899a"));
		assertThat(el0.getStrokeWidth()).isEqualTo(2);
		assertThat(el0.getCenterX()).isEqualTo(0.0);
		assertThat(el0.getCenterY()).isEqualTo(0.0);
		assertThat(el0.getRadiusX()).isEqualTo(6.0);
		assertThat(el0.getRadiusY()).isEqualTo(44.0);
		assertThat(el0.getTransforms().size()).isEqualTo(0);

		Node e1 = ((Group) g0).getChildren().get(1);

		assertThat(e1).isExactlyInstanceOf(Ellipse.class);

		Ellipse el1 = (Ellipse) e1;

		assertThat(el1.getFill()).isEqualTo(Color.TRANSPARENT);
		assertThat(el1.getStroke()).isEqualTo(Color.web("#e1d85d"));
		assertThat(el1.getStrokeWidth()).isEqualTo(2);
		assertThat(el1.getCenterX()).isEqualTo(0.0);
		assertThat(el1.getCenterY()).isEqualTo(0.0);
		assertThat(el1.getRadiusX()).isEqualTo(6.0);
		assertThat(el1.getRadiusY()).isEqualTo(44.0);
		assertThat(el1.getTransforms().size()).isEqualTo(1);
		assertThat(el1.getTransforms().get(0).toArray(MatrixType.MT_2D_3x3)).isEqualTo(new Rotate(-66).toArray(MatrixType.MT_2D_3x3));

		Node e2 = ((Group) g0).getChildren().get(2);

		assertThat(e2).isExactlyInstanceOf(Ellipse.class);

		Ellipse el2 = (Ellipse) e2;

		assertThat(el2.getFill()).isEqualTo(Color.TRANSPARENT);
		assertThat(el2.getStroke()).isEqualTo(Color.web("#80a3cf"));
		assertThat(el2.getStrokeWidth()).isEqualTo(2);
		assertThat(el2.getCenterX()).isEqualTo(0.0);
		assertThat(el2.getCenterY()).isEqualTo(0.0);
		assertThat(el2.getRadiusX()).isEqualTo(6.0);
		assertThat(el2.getRadiusY()).isEqualTo(44.0);
		assertThat(el2.getTransforms().size()).isEqualTo(1);
		assertThat(el2.getTransforms().get(0).toArray(MatrixType.MT_2D_3x3)).isEqualTo(new Rotate(66).toArray(MatrixType.MT_2D_3x3));

		Node c0 = ((Group) g0).getChildren().get(3);

		assertThat(c0).isExactlyInstanceOf(Circle.class);

		Circle ci0 = (Circle) c0;

		assertThat(ci0.getFill()).isEqualTo(Color.TRANSPARENT);
		assertThat(ci0.getStroke()).isEqualTo(Color.web("#4b541f"));
		assertThat(ci0.getStrokeWidth()).isEqualTo(2);
		assertThat(ci0.getCenterX()).isEqualTo(0.0);
		assertThat(ci0.getCenterY()).isEqualTo(0.0);
		assertThat(ci0.getRadius()).isEqualTo(44.0);
		assertThat(ci0.getTransforms().size()).isEqualTo(0);

		Node g1 = ((Group) svgNode).getChildren().get(1);

		assertThat(g1).isExactlyInstanceOf(Group.class);
		assertThat((Parent) g1).hasExactlyNumChildren(4);

		Node c10 = ((Group) g1).getChildren().get(0);

		assertThat(c10).isExactlyInstanceOf(Circle.class);

		Circle ci10 = (Circle) c10;

		assertThat(ci10.getFill()).isEqualTo(Color.web("#80a3cf"));
		assertThat(ci10.getStroke()).isEqualTo(Color.web("white"));
		assertThat(ci10.getStrokeWidth()).isEqualTo(2);
		assertThat(ci10.getCenterX()).isEqualTo(0.0);
		assertThat(ci10.getCenterY()).isEqualTo(0.0);
		assertThat(ci10.getRadius()).isEqualTo(13.0);
		assertThat(ci10.getTransforms().size()).isEqualTo(0);

		Node c11 = ((Group) g1).getChildren().get(1);

		assertThat(c11).isExactlyInstanceOf(Circle.class);

		Circle ci11 = (Circle) c11;

		assertThat(ci11.getFill()).isEqualTo(Color.web("#66899a"));
		assertThat(ci11.getStroke()).isEqualTo(Color.web("white"));
		assertThat(ci11.getStrokeWidth()).isEqualTo(2);
		assertThat(ci11.getCenterX()).isEqualTo(0.0);
		assertThat(ci11.getCenterY()).isEqualTo(-44.0);
		assertThat(ci11.getRadius()).isEqualTo(9.0);
		assertThat(ci11.getTransforms().size()).isEqualTo(0);

		Node c12 = ((Group) g1).getChildren().get(2);

		assertThat(c12).isExactlyInstanceOf(Circle.class);

		Circle ci12 = (Circle) c12;

		assertThat(ci12.getFill()).isEqualTo(Color.web("#66899a"));
		assertThat(ci12.getStroke()).isEqualTo(Color.web("white"));
		assertThat(ci12.getStrokeWidth()).isEqualTo(2);
		assertThat(ci12.getCenterX()).isEqualTo(-40.0);
		assertThat(ci12.getCenterY()).isEqualTo(18.0);
		assertThat(ci12.getRadius()).isEqualTo(9.0);
		assertThat(ci12.getTransforms().size()).isEqualTo(0);

		Node c13 = ((Group) g1).getChildren().get(3);

		assertThat(c13).isExactlyInstanceOf(Circle.class);

		Circle ci13 = (Circle) c13;

		assertThat(ci13.getFill()).isEqualTo(Color.web("#66899a"));
		assertThat(ci13.getStroke()).isEqualTo(Color.web("white"));
		assertThat(ci13.getStrokeWidth()).isEqualTo(2);
		assertThat(ci13.getCenterX()).isEqualTo(40.0);
		assertThat(ci13.getCenterY()).isEqualTo(18.0);
		assertThat(ci13.getRadius()).isEqualTo(9.0);
		assertThat(ci13.getTransforms().size()).isEqualTo(0);

	}

}
