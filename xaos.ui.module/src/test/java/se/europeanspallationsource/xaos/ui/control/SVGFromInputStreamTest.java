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
package se.europeanspallationsource.xaos.ui.control;


import se.europeanspallationsource.xaos.ui.control.SVG;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import javafx.scene.Scene;
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
public class SVGFromInputStreamTest extends ApplicationTest {
	
	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- SVGFromInputStreamTest ------------------------------------");
	}

	private SVG svg;

	@Override
	public void start( Stage stage ) throws IOException, XMLStreamException {

		svg = SVG.load(SVGFromInputStreamTest.class.getResourceAsStream("/svg/duke.svg"));

		svg.setId("Loaded SVG Image");

		stage.setScene(new Scene(svg));
		stage.show();

	}

	@Override
	public void stop() throws Exception {
		super.stop(); //To change body of generated methods, choose Tools | Templates.
	}

	@After
	public void tearDown() throws TimeoutException {
		FxToolkit.cleanupStages();
	}

	@Test
	public void testLoadSVG() {
		System.out.println("  Testing ''load''...");
		assertThat(svg).hasAnyChild();
	}

}
