/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2018-2019 by European Spallation Source ERIC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.europeanspallationsource.xaos.ui.plot.plugins.impl;


import chart.Plugin;
import java.text.DecimalFormat;
import java.text.Format;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;


/**
 * A {@link Plugin} displaying a {@link Label} next to the mouse cursor, showing
 * some text obtaining formatting an object. The default formatter used is a
 * {@link DecimalFormat} falling back to {@link Object#toString()} if the object
 * to be formatted is not a number.
 * <p>
 * The display's relative position to the mouse cursor can be adjusted using the
 * {@link #positionProperty() position} property.</p>
 * <p>
 * The formatter used can be adjusted by the
 * {@link #formatterProperty() formatter} property.</p>
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public abstract class FormattedCursorDisplay extends CursorDisplay {

	/* *********************************************************************** *
	 * START OF JAVAFX PROPERTIES                                              *
	 * *********************************************************************** */

	/*
	 * ---- formatter ----------------------------------------------------------
	 */
	private final ObjectProperty<Format> formatter = new SimpleObjectProperty<>(this, "formatter", new DecimalFormat("0.000"));

	/**
	 * @return The {@link Format} of the cursor display.
	 */
	public final ObjectProperty<Format> formatterProperty() {
		return formatter;
	}

	public final Format getFormatter() {
		return formatterProperty().get();
	}

	public final void setFormatter( Format value ) {
		formatterProperty().set(value);
	}

	/* *********************************************************************** *
	 * END OF JAVAFX PROPERTIES                                                *
	 * *********************************************************************** */

	public FormattedCursorDisplay() {
		super();
	}

	public FormattedCursorDisplay( Position position ) {
		super(position);
	}

	public FormattedCursorDisplay( Position position, Format formatter ) {
		this(position);
		setFormatter(formatter);
	}

	protected String formatValue( Object value ) {
		if ( value == null ) {
			return "—";
		} else if ( value instanceof Number ) {
			return getFormatter().format(( (Number) value ).doubleValue());
		} else {
			return String.valueOf(value);
		}
	}

	@Override
	protected String textAtPosition( Point2D mouseLocation ) {
		return formatValue(valueAtPosition(mouseLocation));
	}

	/**
	 * Returns the value to be displayed at the given mouse cursor location.
	 *
	 * @param mouseLocation The current mouse cursor location where some text
	 *                      must be displayed.
	 * @return A value object or {@code null}.
	 */
	protected abstract Object valueAtPosition( Point2D mouseLocation );

}
