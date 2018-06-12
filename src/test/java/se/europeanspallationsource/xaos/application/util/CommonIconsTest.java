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
package se.europeanspallationsource.xaos.application.util;


import javafx.scene.paint.Color;
import org.junit.Test;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.javafx.FontIcon;
import se.europeanspallationsource.xaos.application.util.CommonIcons;

import static org.junit.Assert.assertEquals;



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

		FontIcon icon;
			
		icon = CommonIcons.FILE.getIcon();			assertEquals(FontAwesomeRegular.FILE.getCode(), icon.getText().charAt(0));
		icon = CommonIcons.FOLDER.getIcon();		assertEquals(FontAwesomeRegular.FOLDER.getCode(), icon.getText().charAt(0));
		icon = CommonIcons.FOLDER_OPEN.getIcon();	assertEquals(FontAwesomeRegular.FOLDER_OPEN.getCode(), icon.getText().charAt(0));
		icon = CommonIcons.SQUARE_DOWN.getIcon();	assertEquals(FontAwesomeRegular.CARET_SQUARE_DOWN.getCode(), icon.getText().charAt(0));
		icon = CommonIcons.SQUARE_LEFT.getIcon();	assertEquals(FontAwesomeRegular.CARET_SQUARE_LEFT.getCode(), icon.getText().charAt(0));
		icon = CommonIcons.SQUARE_RIGHT.getIcon();	assertEquals(FontAwesomeRegular.CARET_SQUARE_RIGHT.getCode(), icon.getText().charAt(0));
		icon = CommonIcons.SQUARE_UP.getIcon();		assertEquals(FontAwesomeRegular.CARET_SQUARE_UP.getCode(), icon.getText().charAt(0));

    }

	/**
	 * Test of get method, of class CommonIcons, when a color is passed as parameter.
	 */
	@Test
	public void testGetWithColor() {

		System.out.println("  Testing ''get(color)''...");

		FontIcon icon = CommonIcons.FILE.getIcon(Color.YELLOW);

		assertEquals(Color.YELLOW, icon.getIconColor());

		icon = CommonIcons.FOLDER.getIcon(Color.ORANGE);

		assertEquals(Color.ORANGE, icon.getIconColor());

	}

	/**
	 * Test of get method, of class CommonIcons, when null is passed as parameter.
	 */
	@Test(expected = NullPointerException.class)
	public void testGetWithNull() {

		System.out.println("  Testing ''get(null)''...");

        CommonIcons.FILE.getIcon(null);

	}

	/**
	 * Test of get method, of class CommonIcons, when a size is passed as parameter.
	 */
	@Test
	public void testGetWithSize() {

		System.out.println("  Testing ''get(size)''...");

		FontIcon icon = CommonIcons.FILE.getIcon(33);

		assertEquals(33L, icon.getIconSize());

		icon = CommonIcons.FOLDER.getIcon(123);

		assertEquals(123L, icon.getIconSize());

	}

	/**
	 * Test of get method, of class CommonIcons, when size and color are passed as parameters.
	 */
	@Test
	public void testGetWithSizeAndColor() {

		System.out.println("  Testing ''get(size, color)''...");

		FontIcon icon = CommonIcons.FILE.getIcon(33, Color.ALICEBLUE);

		assertEquals(33L, icon.getIconSize());
		assertEquals(Color.ALICEBLUE, icon.getIconColor());

		icon = CommonIcons.FOLDER.getIcon(234, Color.AQUA);

		assertEquals(234L, icon.getIconSize());
		assertEquals(Color.AQUA, icon.getIconColor());

	}

}
