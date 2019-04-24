package se.europeanspallationsource.xaos.core.util;

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2018-2019 by European Spallation Source ERIC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Miscellaneous mathematical utility functions.
 *
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( "ClassWithoutLogger" )
public class MathUtils {

	/**
	 * Returns {@code value} if it included inside the range made of the two
	 * {@code extrema} parameters, otherwise returns the range's minimum if
	 * {@code value} is lower than it, or the range's maximum.
	 *
	 * @param value    The value to be returned if inside the given range, or
	 *                 clamped.
	 * @param extrema1 One of the two extrema defining the value's validity
	 *                 range.
	 * @param extrema2 The other extrema defining the value's validity range.
	 * @return {@code value} if inside the specified range, or one of the
	 *         range's extrema.
	 */
	public static int clamp( int value, int extrema1, int extrema2 ) {
		if ( extrema1 < extrema2 ) {
			return Math.max(extrema1, Math.min(extrema2, value));
		} else {
			return Math.max(extrema2, Math.min(extrema1, value));
		}
	}

	/**
	 * Returns {@code value} if it included inside the range made of the two
	 * {@code extrema} parameters, otherwise returns the range's minimum if
	 * {@code value} is lower than it, or the range's maximum.
	 *
	 * @param value    The value to be returned if inside the given range, or
	 *                 clamped.
	 * @param extrema1 One of the two extrema defining the value's validity
	 *                 range.
	 * @param extrema2 The other extrema defining the value's validity range.
	 * @return {@code value} if inside the specified range, or one of the
	 *         range's extrema.
	 */
	public static long clamp( long value, long extrema1, long extrema2 ) {
		if ( extrema1 < extrema2 ) {
			return Math.max(extrema1, Math.min(extrema2, value));
		} else {
			return Math.max(extrema2, Math.min(extrema1, value));
		}
	}

	/**
	 * Returns {@code value} if it included inside the range made of the two
	 * {@code extrema} parameters, otherwise returns the range's minimum if
	 * {@code value} is lower than it, or the range's maximum.
	 *
	 * @param value    The value to be returned if inside the given range, or
	 *                 clamped.
	 * @param extrema1 One of the two extrema defining the value's validity
	 *                 range.
	 * @param extrema2 The other extrema defining the value's validity range.
	 * @return {@code value} if inside the specified range, or one of the
	 *         range's extrema.
	 */
	public static float clamp( float value, float extrema1, float extrema2 ) {
		if ( extrema1 < extrema2 ) {
			return Math.max(extrema1, Math.min(extrema2, value));
		} else {
			return Math.max(extrema2, Math.min(extrema1, value));
		}
	}

	/**
	 * Returns {@code value} if it included inside the range made of the two
	 * {@code extrema} parameters, otherwise returns the range's minimum if
	 * {@code value} is lower than it, or the range's maximum.
	 *
	 * @param value    The value to be returned if inside the given range, or
	 *                 clamped.
	 * @param extrema1 One of the two extrema defining the value's validity
	 *                 range.
	 * @param extrema2 The other extrema defining the value's validity range.
	 * @return {@code value} if inside the specified range, or one of the
	 *         range's extrema.
	 */
	public static double clamp( double value, double extrema1, double extrema2 ) {
		if ( extrema1 < extrema2 ) {
			return Math.max(extrema1, Math.min(extrema2, value));
		} else {
			return Math.max(extrema2, Math.min(extrema1, value));
		}
	}

	private MathUtils() {
		//	Nothing to do.
	}

}
