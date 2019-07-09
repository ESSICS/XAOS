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

	/**
	 * Check if the given {@code value} is inside the range made of the two
	 * {@code extrema} parameters,
	 *
	 * @param value    The number to be checked.
	 * @param extrema1 One of the two extrema defining the value's validity
	 *                 range.
	 * @param extrema2 The other extrema defining the value's validity range.
	 * @return {@code value >= min(extrema1, extrema2) && value <= max(extrema1, extrema2)},
	 */
	public static boolean inside( int value, int extrema1, int extrema2 ) {
		if ( extrema1 < extrema2 ) {
			return ( value >= extrema1 && value <= extrema2 );
		} else {
			return ( value >= extrema2 && value <= extrema1 );
		}
	}

	/**
	 * Check if the given {@code value} is inside the range made of the two
	 * {@code extrema} parameters,
	 *
	 * @param value    The number to be checked.
	 * @param extrema1 One of the two extrema defining the value's validity
	 *                 range.
	 * @param extrema2 The other extrema defining the value's validity range.
	 * @return {@code value >= min(extrema1, extrema2) && value <= max(extrema1, extrema2)},
	 */
	public static boolean inside( long value, long extrema1, long extrema2 ) {
		if ( extrema1 < extrema2 ) {
			return ( value >= extrema1 && value <= extrema2 );
		} else {
			return ( value >= extrema2 && value <= extrema1 );
		}
	}

	/**
	 * Check if the given {@code value} is inside the range made of the two
	 * {@code extrema} parameters,
	 *
	 * @param value    The number to be checked.
	 * @param extrema1 One of the two extrema defining the value's validity
	 *                 range.
	 * @param extrema2 The other extrema defining the value's validity range.
	 * @return {@code value >= min(extrema1, extrema2) && value <= max(extrema1, extrema2)},
	 */
	public static boolean inside( float value, float extrema1, float extrema2 ) {
		if ( extrema1 < extrema2 ) {
			return ( value >= extrema1 && value <= extrema2 );
		} else {
			return ( value >= extrema2 && value <= extrema1 );
		}
	}

	/**
	 * Check if the given {@code value} is inside the range made of the two
	 * {@code extrema} parameters,
	 *
	 * @param value    The number to be checked.
	 * @param extrema1 One of the two extrema defining the value's validity
	 *                 range.
	 * @param extrema2 The other extrema defining the value's validity range.
	 * @return {@code value >= min(extrema1, extrema2) && value <= max(extrema1, extrema2)},
	 */
	public static boolean inside( double value, double extrema1, double extrema2 ) {
		if ( extrema1 < extrema2 ) {
			return ( value >= extrema1 && value <= extrema2 );
		} else {
			return ( value >= extrema2 && value <= extrema1 );
		}
	}

	/**
	 * Check if the given {@code value} is outside the range made of the two
	 * {@code extrema} parameters,
	 *
	 * @param value    The number to be checked.
	 * @param extrema1 One of the two extrema defining the value's validity
	 *                 range.
	 * @param extrema2 The other extrema defining the value's validity range.
	 * @return {@code value < min(extrema1, extrema2) || value > max(extrema1, extrema2)},
	 */
	public static boolean outside( int value, int extrema1, int extrema2 ) {
		if ( extrema1 < extrema2 ) {
			return ( value < extrema1 || value > extrema2 );
		} else {
			return ( value < extrema2 || value > extrema1 );
		}
	}

	/**
	 * Check if the given {@code value} is outside the range made of the two
	 * {@code extrema} parameters,
	 *
	 * @param value    The number to be checked.
	 * @param extrema1 One of the two extrema defining the value's validity
	 *                 range.
	 * @param extrema2 The other extrema defining the value's validity range.
	 * @return {@code value < min(extrema1, extrema2) || value > max(extrema1, extrema2)},
	 */
	public static boolean outside( long value, long extrema1, long extrema2 ) {
		if ( extrema1 < extrema2 ) {
			return ( value < extrema1 || value > extrema2 );
		} else {
			return ( value < extrema2 || value > extrema1 );
		}
	}

	/**
	 * Check if the given {@code value} is outside the range made of the two
	 * {@code extrema} parameters,
	 *
	 * @param value    The number to be checked.
	 * @param extrema1 One of the two extrema defining the value's validity
	 *                 range.
	 * @param extrema2 The other extrema defining the value's validity range.
	 * @return {@code value < min(extrema1, extrema2) || value > max(extrema1, extrema2)},
	 */
	public static boolean outside( float value, float extrema1, float extrema2 ) {
		if ( extrema1 < extrema2 ) {
			return ( value < extrema1 || value > extrema2 );
		} else {
			return ( value < extrema2 || value > extrema1 );
		}
	}

	/**
	 * Check if the given {@code value} is outside the range made of the two
	 * {@code extrema} parameters,
	 *
	 * @param value    The number to be checked.
	 * @param extrema1 One of the two extrema defining the value's validity
	 *                 range.
	 * @param extrema2 The other extrema defining the value's validity range.
	 * @return {@code value < min(extrema1, extrema2) || value > max(extrema1, extrema2)},
	 */
	public static boolean outside( double value, double extrema1, double extrema2 ) {
		if ( extrema1 < extrema2 ) {
			return ( value < extrema1 || value > extrema2 );
		} else {
			return ( value < extrema2 || value > extrema1 );
		}
	}

	/**
	 * Computes
	 * <pre>
	 *                y1 - y0
	 * y = (x - x0) * ------- + y0
	 *                x1 - x0
	 * </pre>
	 * <p>
	 * If {@code x1 == x0} then {@code (y1 + y0) / 2} will be returned.</p>
	 *
	 * @param x  The value for which a proportion must be computed.
	 * @param x0 The first sample of x.
	 * @param x1 The second sample of x.
	 * @param y0 The first sample of y.
	 * @param y1 The second sample of y.
	 * @return The computed proportion.
	 */
	public static float proportion( float x, float x0, float x1, float y0, float y1 ) {
		if ( x1 == x0 ) {
			return (y1 + y0) / 2F;
		} else {
			return (x - x0) * (y1 - y0) / (x1 - x0) + y0;
		}
	}

	/**
	 * Computes
	 * <pre>
	 *                y1 - y0
	 * y = (x - x0) * ------- + y0
	 *                x1 - x0
	 * </pre>
	 * <p>
	 * If {@code x1 == x0} then {@code (y1 + y0) / 2} will be returned.</p>
	 *
	 * @param x  The value for which a proportion must be computed.
	 * @param x0 The first sample of x.
	 * @param x1 The second sample of x.
	 * @param y0 The first sample of y.
	 * @param y1 The second sample of y.
	 * @return The computed proportion.
	 */
	public static double proportion( double x, double x0, double x1, double y0, double y1 ) {
		if ( x1 == x0 ) {
			return (y1 + y0) / 2.0;
		} else {
			return (x - x0) * (y1 - y0) / (x1 - x0) + y0;
		}
	}

	/**
	 * Check if the given {@code value} is strictly inside the range made of the
	 * two {@code extrema} parameters,
	 *
	 * @param value    The number to be checked.
	 * @param extrema1 One of the two extrema defining the value's validity
	 *                 range.
	 * @param extrema2 The other extrema defining the value's validity range.
	 * @return {@code value > min(extrema1, extrema2) && value < max(extrema1, extrema2)},
	 */
	public static boolean strictlyInside( int value, int extrema1, int extrema2 ) {
		if ( extrema1 < extrema2 ) {
			return ( value > extrema1 && value < extrema2 );
		} else {
			return ( value > extrema2 && value < extrema1 );
		}
	}

	/**
	 * Check if the given {@code value} is strictly inside the range made of the
	 * two {@code extrema} parameters,
	 *
	 * @param value    The number to be checked.
	 * @param extrema1 One of the two extrema defining the value's validity
	 *                 range.
	 * @param extrema2 The other extrema defining the value's validity range.
	 * @return {@code value > min(extrema1, extrema2) && value < max(extrema1, extrema2)},
	 */
	public static boolean strictlyInside( long value, long extrema1, long extrema2 ) {
		if ( extrema1 < extrema2 ) {
			return ( value > extrema1 && value < extrema2 );
		} else {
			return ( value > extrema2 && value < extrema1 );
		}
	}

	/**
	 * Check if the given {@code value} is strictly inside the range made of the
	 * two {@code extrema} parameters,
	 *
	 * @param value    The number to be checked.
	 * @param extrema1 One of the two extrema defining the value's validity
	 *                 range.
	 * @param extrema2 The other extrema defining the value's validity range.
	 * @return {@code value > min(extrema1, extrema2) && value < max(extrema1, extrema2)},
	 */
	public static boolean strictlyInside( float value, float extrema1, float extrema2 ) {
		if ( extrema1 < extrema2 ) {
			return ( value > extrema1 && value < extrema2 );
		} else {
			return ( value > extrema2 && value < extrema1 );
		}
	}

	/**
	 * Check if the given {@code value} is strictly inside the range made of the
	 * two {@code extrema} parameters,
	 *
	 * @param value    The number to be checked.
	 * @param extrema1 One of the two extrema defining the value's validity
	 *                 range.
	 * @param extrema2 The other extrema defining the value's validity range.
	 * @return {@code value > min(extrema1, extrema2) && value < max(extrema1, extrema2)},
	 */
	public static boolean strictlyInside( double value, double extrema1, double extrema2 ) {
		if ( extrema1 < extrema2 ) {
			return ( value > extrema1 && value < extrema2 );
		} else {
			return ( value > extrema2 && value < extrema1 );
		}
	}

	private MathUtils() {
		//	Nothing to do.
	}

}
