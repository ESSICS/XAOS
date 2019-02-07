/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package util;

import chart.DateAxis;
import javafx.scene.chart.Axis;
import javafx.scene.chart.ValueAxis;

/**
 * Set of assertion methods useful for checking method arguments.
 * 
 * @author Grzegorz Kruk
 */
public class Assert {

    /**
     * Asserts that a condition is {@code true}.
     * 
     * @param condition the condition to be checked
     * @param message message of the {@link IllegalArgumentException}
     * @throws IllegalArgumentException if the condition is {@code false}
     */
    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Asserts that given object (method argument) is not {@code null}.
     * 
     * @param arg the object to check
     * @param argName name of the argument used to construct message of {@link IllegalArgumentException}
     * @throws IllegalArgumentException if the object is {@code null}
     */
    public static void assertArgNotNull(Object arg, String argName) {
        if (arg == null) {
            throw new IllegalArgumentException("The '" + argName + "' must not be null");
        }
    }

    /**
     * Asserts that given axis is an instance of {@link ValueAxis}. 
     * 
     * @param axis the axis to check
     * @param axisName name of the axis used to construct message of {@link IllegalArgumentException}
     * @throws IllegalArgumentException if the axis is not an instance of <code>ValueAxis</code>
     */
    public static void assertValueAxis(Axis<?> axis, String axisName) {
        if (!(axis instanceof ValueAxis<?>)) {
            throw new IllegalArgumentException(axisName + " axis must be an instance of ValueAxis");
        }//}
    }
}
