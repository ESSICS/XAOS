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
package se.europeanspallationsource.xaos.application.utilities;


import javafx.scene.text.Text;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static se.europeanspallationsource.xaos.application.utilities.CommonIcons.Glyph.FILE;
import static se.europeanspallationsource.xaos.application.utilities.CommonIcons.Glyph.FOLDER;
import static se.europeanspallationsource.xaos.application.utilities.CommonIcons.Glyph.SQUARE_DOWN;
import static se.europeanspallationsource.xaos.application.utilities.CommonIcons.Glyph.SQUARE_LEFT;
import static se.europeanspallationsource.xaos.application.utilities.CommonIcons.Glyph.SQUARE_RIGHT;
import static se.europeanspallationsource.xaos.application.utilities.CommonIcons.Glyph.SQUARE_UP;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "UseOfSystemOutOrSystemErr", "ClassWithoutLogger" } )
public class CommonIconsTest {

	/**
	 * Test of get method, of class CommonIcons.
	 */
	@Test
	public void testGet() {

		System.out.println("  Testing ''get''...");

		Text icon;
			
		icon = CommonIcons.get(FILE);			assertEquals(FILE.unicode(), icon.getText());
		icon = CommonIcons.get(FOLDER);			assertEquals(FOLDER.unicode(), icon.getText());
		icon = CommonIcons.get(SQUARE_DOWN);	assertEquals(SQUARE_DOWN.unicode(), icon.getText());
		icon = CommonIcons.get(SQUARE_LEFT);	assertEquals(SQUARE_LEFT.unicode(), icon.getText());
		icon = CommonIcons.get(SQUARE_RIGHT);	assertEquals(SQUARE_RIGHT.unicode(), icon.getText());
		icon = CommonIcons.get(SQUARE_UP);		assertEquals(SQUARE_UP.unicode(), icon.getText());

    }

	/**
	 * Test of get method, of class CommonIcons, when null is passed as parameter.
	 */
	@Test(expected = NullPointerException.class)
	public void testGetWithNull() {

		System.out.println("  Testing ''get(null)''...");

        CommonIcons.get(null);

	}

}
