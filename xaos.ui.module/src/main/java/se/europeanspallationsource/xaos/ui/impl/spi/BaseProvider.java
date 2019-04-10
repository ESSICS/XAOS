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
package se.europeanspallationsource.xaos.ui.impl.spi;


import java.util.Map;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.text.TextStringBuilder;


/**
 * The base class of all providers.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
class BaseProvider {

	/**
	 * Print on {@link System#out} the keys of the given {@code map} with a
	 * preceding {@code message}.
	 *
	 * @param <K>     The type of the keys in the given {@code map}.
	 * @param <V>     The type of the values in the given {@code map}.
	 * @param map     The {@link Map} whose keys must be printed.
	 * @param message The header message.
	 */
	@SuppressWarnings( "UseOfSystemOutOrSystemErr" )
	protected static <K, V> void verbosePrintout( Map<K, V> map, String message ) {

		if ( Boolean.getBoolean("xaos.test.verbose") ) {

			final int initialPad = 8;
			final TextStringBuilder builder = new TextStringBuilder();

			builder.appendPadding(initialPad, ' ');
			builder.appendPadding(4, '-');
			builder.appendPadding(1, ' ');
			builder.append(message);
			builder.appendPadding(1, ' ');

			int currentPosition = initialPad + 4 + 1 + message.length() + 1;

			if ( 120 - currentPosition > 0 ) {
				builder.appendPadding(120 - currentPosition, '-');
			}

			builder.appendNewLine();

			OptionalInt max = map.keySet().stream().mapToInt(k -> k.toString().length()).max();

			if ( max.isPresent() ) {

				int width = 4 + max.getAsInt();
				int columns = ( 120 - initialPad ) / width;
				AtomicInteger column = new AtomicInteger(0);


				map.keySet().stream().sorted().forEach(k -> {

					if ( column.get() == 0 ) {
						builder.appendPadding(initialPad, ' ');
					}

					builder.appendFixedWidthPadRight(k, width, ' ');

					if ( column.incrementAndGet() >= columns ) {
						column.set(0);
						builder.appendNewLine();
					}

				});

				if ( column.get() > 0 ) {
					builder.appendNewLine();
				}

			}

			builder.appendPadding(initialPad, ' ');
			builder.appendPadding(120 - initialPad, '-');
			builder.appendNewLine();

			System.out.print(builder.toString());

		}

	}

	protected BaseProvider() {
	}

}
