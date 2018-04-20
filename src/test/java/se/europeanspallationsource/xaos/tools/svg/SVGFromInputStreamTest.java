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
@SuppressWarnings( "ClassWithoutLogger" )
public class SVGFromInputStreamTest extends ApplicationTest {
	
	@BeforeClass
	public static void setUpClass() {
		if ( Boolean.getBoolean("headless") ) {
			System.setProperty("testfx.robot", "glass");
			System.setProperty("testfx.headless", "true");
			System.setProperty("prism.order", "sw");
			System.setProperty("prism.text", "t2k");
			System.setProperty("java.awt.headless", "true");
		}
	}

	private SVGContent svgContent;

	@Override
	public void start( Stage stage ) throws IOException, XMLStreamException {

		svgContent = SVGLoader.load(SVGFromInputStreamTest.class.getResourceAsStream("/svg/duke.svg"));

		svgContent.setId("Loaded SVG Image");

		stage.setScene(new Scene(svgContent));
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
		assertThat(svgContent).hasAnyChild();
	}

}
