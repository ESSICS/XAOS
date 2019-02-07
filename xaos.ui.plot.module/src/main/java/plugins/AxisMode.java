/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package plugins;

/**
 * Defines mode of axis-related operations such as zoom or pan.
 * 
 * @author Grzegorz Kruk
 */
public enum AxisMode {
    /** 
     * The operation should be allowed only along the X axis.
     */
    X, 
    /**
     * The operation should be allowed only along Y axis.
     */
    Y,
    
    /**
     * The operation can be performed on both X and Y axis.
     */
    XY
}
