/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chart;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javafx.beans.value.ChangeListener;
import javafx.scene.chart.XYChart;
import util.Timestamp;
/**
 *
 * @author reubenlindroos
 */
public class TimeAxis extends NumberAxis {
    
    private TimeUnit maxTimeUnit;
    private TimeUnit minTimeUnit;
    
    @Override protected String getTickMarkLabel(Number value) {
           String tickLabel;
           if ((maxTimeUnit==null)&&(minTimeUnit==null)){
           String strTimeValue = Long.toString(value.longValue());
           tickLabel = new java.sql.Timestamp(value.longValue()).toString().substring(11,23);
           } else {
               tickLabel = timeStringParser(value);
           }
        
        
        return tickLabel;
 }
    
    /**
     * Create a NumberAxis with time vals as major tickmark label in default format HOURS:MINUTES:SECONDS.MILLISECONDS.
     * Will default to MILLISECONDS from epoch if TIMEFROMEPOCH values are not given for axis. (type long or BigDecimal). 
     */
    public TimeAxis() {
        super();
        this.parentProperty().addListener(listener);
        setForceZeroInRange(false);
        
    }
    private final ChangeListener listener = (obs, oldval, newVal) ->{
        if (newVal!=null) {
        XYChart chart = (XYChart) newVal;
        Collections.min(chart.getData());
        }};
                                                                    
    
   public TimeAxis(double lowerBound, double upperBound, double tickUnit) {
       super(lowerBound, upperBound,tickUnit); 
       setForceZeroInRange(false);
   }
  
    /**
     * Create a non-auto-ranging NumberAxis with the given upper bound, lower bound and tick unit
     *
     * @param axisLabel The name to display for this axis
     * @param lowerBound The lower bound for this axis, ie min plottable value
     * @param upperBound The upper bound for this axis, ie max plottable value
     * @param tickUnit The tick unit, ie space between tickmarks
     */
    public TimeAxis(String axisLabel, double lowerBound, double upperBound, double tickUnit) {
        super(axisLabel, lowerBound, upperBound,tickUnit);
        setForceZeroInRange(false);
        
    }
    
    /**
     *
     * @param maxUnit Maximum unit to be represented on the axis tick labels.
     * @param minUnit Minimum unit to be represented on the axis tick labels.
     */
    public TimeAxis(TimeUnit maxUnit, TimeUnit minUnit) {
        super();
        if (maxUnit.compareTo(minUnit)<0) {
            throw new IllegalArgumentException("maxUnit should be greater than minUnit");
        }
        maxTimeUnit = maxUnit;
        minTimeUnit = minUnit;
        if (minTimeUnit.compareTo(TimeUnit.MILLISECONDS)<0) {
            System.out.println("WARNING: TimeUnits less than MILLISECONDS have been disabled for "+this.getClass());};
        setForceZeroInRange(false);
        
        
    }
    
    private String timeStringParser(Number value) {
    String tickLabel = new java.sql.Timestamp(value.longValue()).toString();
    Integer upperStringLimit = 0;
    Integer lowerStringLimit = tickLabel.length();
    
    if (maxTimeUnit == TimeUnit.HOURS) {
        upperStringLimit = 11;
        
    }
    if (maxTimeUnit == TimeUnit.MINUTES) {
        upperStringLimit = 14;
        
    } 
    if (maxTimeUnit == TimeUnit.SECONDS) {
        upperStringLimit = 17;
    }
    if (maxTimeUnit.compareTo(TimeUnit.MILLISECONDS)<=0) {
        upperStringLimit = 19;
    }
     if (minTimeUnit == TimeUnit.DAYS) {
        lowerStringLimit = 11;
        
     }
     
    if (minTimeUnit == TimeUnit.HOURS) {
        lowerStringLimit = 13;
        
    }
    if (minTimeUnit == TimeUnit.MINUTES) {
        lowerStringLimit = 16;
        
    } 
    if (minTimeUnit == TimeUnit.SECONDS) {
        lowerStringLimit = 19;
    } 
    if (minTimeUnit == TimeUnit.MILLISECONDS) {
        lowerStringLimit = tickLabel.length();
        
    }
    
    return tickLabel.substring(upperStringLimit,lowerStringLimit);
    }
    
}
