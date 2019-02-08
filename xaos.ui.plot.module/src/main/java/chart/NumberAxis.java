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

package chart;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.Dimension2D;
import javafx.scene.chart.ValueAxis;
import javafx.util.Duration;
import javafx.util.StringConverter;

import javafx.css.converter.SizeConverter;

/**
* A axis class that plots a range of numbers with major tick marks every "tickUnit". You can use any Number type with
* this axis, Long, Double, BigDecimal etc.
* @since JavaFX 2.0
*/
public class NumberAxis extends ValueAxis<Number> {

   private static final int MAX_TICK_COUNT = 20;
   private final Timeline animator = new Timeline();
   private final StringProperty currentTickFormat = new SimpleStringProperty(this, "currentFormatter", "");
   private final DefaultFormatter defaultFormatter = new DefaultFormatter(this);

   /** When true zero is always included in the visible range. This only has effect if auto-ranging is on. */
   private BooleanProperty forceZeroInRange = new BooleanPropertyBase(true) {
       @Override protected void invalidated() {
           if(isAutoRanging()) {
               requestAxisLayout();
               invalidateRange();
           }
       }
       @Override public Object getBean() { return NumberAxis.this; }
       @Override public String getName() { return "forceZeroInRange"; }
   };
   public final boolean isForceZeroInRange() { return forceZeroInRange.getValue(); }
   public final void setForceZeroInRange(boolean value) { forceZeroInRange.setValue(value); }
   public final BooleanProperty forceZeroInRangeProperty() { return forceZeroInRange; }

   private  DoubleProperty autoRangePadding = new SimpleDoubleProperty(0.01);
   public double getAutoRangePadding() { return autoRangePadding.get(); }
   public void setAutoRangePadding(double value) { autoRangePadding.set(value); }
   public DoubleProperty autoRangePaddingProperty() {return autoRangePadding; }

   
   /**  The value between each major tick mark in data units. This is automatically set if we are auto-ranging. */
   private DoubleProperty tickUnit = new StyleableDoubleProperty(5) {
       @Override protected void invalidated() {
           if(!isAutoRanging()) {
               invalidateRange();
               requestAxisLayout();
           }
       }
       @Override public CssMetaData<NumberAxis,Number> getCssMetaData() { return StyleableProperties.TICK_UNIT; }
       @Override public Object getBean() { return NumberAxis.this; }
       @Override public String getName() { return "tickUnit"; }
   };
   
   public final double getTickUnit() { return tickUnit.get(); }
   public final void setTickUnit(double value) { tickUnit.set(value); }
   public final DoubleProperty tickUnitProperty() { return tickUnit; }
   
   /** Used to update scale property in ValueAxis (that is read-only)*/
   private final DoubleProperty scaleBinding = new DoublePropertyBase(getScale()) {
       @Override protected void invalidated() { setScale(get()); }
       @Override public Object getBean() { return NumberAxis.this; }
       @Override public String getName() { return "scaleBinding"; }
   };      

   /**
    * Create a auto-ranging NumberAxis
    */
   public NumberAxis() {
       bindToBounds();
   }

   /**
    * Create a non-auto-ranging NumberAxis with the given upper bound, lower bound and tick unit
    *
    * @param lowerBound The lower bound for this axis, ie min plottable value
    * @param upperBound The upper bound for this axis, ie max plottable value
    * @param tickUnit The tick unit, ie space between tickmarks
    */

   public NumberAxis(double lowerBound, double upperBound, double tickUnit) {
       super(lowerBound, upperBound);
       setTickUnit(tickUnit); 
       bindToBounds();
   }
  
    /**
     * Create a non-auto-ranging NumberAxis with the given upper bound, lower bound and tick unit
     *
     * @param axisLabel The name to display for this axis
     * @param lowerBound The lower bound for this axis, ie min plottable value
     * @param upperBound The upper bound for this axis, ie max plottable value
     * @param tickUnit The tick unit, ie space between tickmarks
     */
    public NumberAxis(String axisLabel, double lowerBound, double upperBound, double tickUnit) {
        super(lowerBound, upperBound);
        setTickUnit(tickUnit);
        setLabel(axisLabel);
        bindToBounds();
    }
   
    private void bindToBounds() {
        ChangeListener<Number> rangeUpdater = (obj, oldValue, newValue) -> {
            if (!isAutoRanging()) {
                setRange(computeRange(), false);
            }
        };
        lowerBoundProperty().addListener(rangeUpdater);
        upperBoundProperty().addListener(rangeUpdater);
    }
    
   /**
    * Get the string label name for a tick mark with the given value
    *
    * @param value The value to format into a tick label string
    * @return A formatted string for the given value
    */
   @Override protected String getTickMarkLabel(Number value) {
       StringConverter<Number> formatter = getTickLabelFormatter();
       String tickLabel;
       if (formatter == null) {
           formatter = defaultFormatter;
       }
       tickLabel = formatter.toString(value);
       return tickLabel;
   }

   /**
    * Called to get the current axis range.
    *
    * @return A range object that can be passed to setRange() and calculateTickValues()
    */
   @Override protected Object getRange() {
        return new Range(getLowerBound(), getUpperBound(), getTickUnit(), getScale(), currentTickFormat.get());
   }

   /**
    * Called to set the current axis range to the given range. If isAnimating() is true then this method should
    * animate the range to the new range.
    *
    * @param rangeObj A range object returned from autoRange()
    * @param animate If true animate the change in range
    */
   @Override protected void setRange(Object rangeObj, boolean animate) {
       Range range = (Range) rangeObj;
       currentTickFormat.set(range.tickFormat);
       final double oldLowerBound = getLowerBound();
       setLowerBound(range.lowerBound);
       setUpperBound(range.upperBound);
       setTickUnit(range.tickUnit);
       
       if(animate) {
           animator.stop();
           animator.getKeyFrames().setAll(
               new KeyFrame(Duration.ZERO,
                       new KeyValue(currentLowerBound, oldLowerBound),
                       new KeyValue(scaleBinding, getScale())
               ),
               new KeyFrame(Duration.millis(700),
                       new KeyValue(currentLowerBound, range.lowerBound),
                       new KeyValue(scaleBinding, range.scale)
               )
           );
           animator.play();
       } else {
           currentLowerBound.set(range.lowerBound);
           setScale(range.scale);
       }
   }

   /**
    * Calculate a list of all the data values for each tick mark in range
    *
    * @param length The length of the axis in display units
    * @param rangeObj A range object returned from autoRange()
    * @return A list of tick marks that fit along the axis if it was the given length
    */
   @Override protected List<Number> calculateTickValues(double length, Object rangeObj) {
       Range range = (Range) rangeObj;
       List<Number> tickValues = new ArrayList<>();
       if (range.lowerBound == range.upperBound) {
           tickValues.add(range.lowerBound);
       } else if (range.tickUnit <= 0) {
           tickValues.add(range.lowerBound);
           tickValues.add(range.upperBound);
       } else if (range.tickUnit > 0) {
           tickValues.add(range.lowerBound);
           if (((range.upperBound - range.lowerBound) / range.tickUnit) > 2000) {
               // This is a ridiculous amount of major tick marks, something has probably gone wrong
               System.err.println("Warning we tried to create more than 2000 major tick marks on a NumberAxis. " +
                       "Lower Bound=" + range.lowerBound + ", Upper Bound=" + range.upperBound + ", Tick Unit=" + tickUnit);
           } else {
               if (range.lowerBound + range.tickUnit < range.upperBound) {
                   // If tickUnit is integer, start with the nearest integer
                   double first = Math.rint(range.tickUnit) == range.tickUnit ? Math.ceil(range.lowerBound) : range.lowerBound + range.tickUnit;
                   for (double major = first; major < range.upperBound; major += range.tickUnit) {
                       if (!tickValues.contains(major)) {
                           tickValues.add(major);
                       }
                   }
               }
           }
           tickValues.add(range.upperBound);
       }
       return tickValues;
   }

   /**
    * Calculate a list of the data values for every minor tick mark
    *
    * @return List of data values where to draw minor tick marks
    */
   protected List<Number> calculateMinorTickMarks() {
       final List<Number> minorTickMarks = new ArrayList<>();
       final double lowerBound = getLowerBound();
       final double upperBound = getUpperBound();
       final double tickUnit = getTickUnit();
       final double minorUnit = tickUnit/Math.max(1, getMinorTickCount());
       if (tickUnit > 0) {
           if(((upperBound - lowerBound) / minorUnit) > 10000) {
               // This is a ridiculous amount of major tick marks, something has probably gone wrong
               System.err.println("Warning we tried to create more than 10000 minor tick marks on a NumberAxis. " +
                       "Lower Bound=" + getLowerBound() + ", Upper Bound=" + getUpperBound() + ", Tick Unit=" + tickUnit);
               return minorTickMarks;
           }
           final boolean tickUnitIsInteger = Math.rint(tickUnit) == tickUnit;
           if (tickUnitIsInteger) {
               for (double minor = Math.floor(lowerBound) + minorUnit; minor < Math.ceil(lowerBound); minor += minorUnit) {
                   if (minor > lowerBound) {
                       minorTickMarks.add(minor);
                   }
               }
           }
           double major = tickUnitIsInteger ? Math.ceil(lowerBound) : lowerBound;
           for (; major < upperBound; major += tickUnit)  {
               final double next = Math.min(major + tickUnit, upperBound);
               for (double minor = major + minorUnit; minor < next; minor += minorUnit) {
                   minorTickMarks.add(minor);
               }
           }
       }
       return minorTickMarks;
   }

   /**
    * Measure the size of the label for given tick mark value. This uses the font that is set for the tick marks
    *
    * @param value tick mark value
    * @param rangeObj range to use during calculations
    * @return size of tick mark label for given value
    */
   @Override protected Dimension2D measureTickMarkSize(Number value, Object rangeObj) {
       Range range = (Range) rangeObj;
       return measureTickMarkSize(value, getTickLabelRotation(), range.tickFormat);
   }

   /**
    * Measure the size of the label for given tick mark value. This uses the font that is set for the tick marks
    *
    * @param value     tick mark value
    * @param rotation  The text rotation
    * @param numFormatter The number formatter
    * @return size of tick mark label for given value
    */
   private Dimension2D measureTickMarkSize(Number value, double rotation, String numFormatter) {
       String labelText;
       StringConverter<Number> formatter = getTickLabelFormatter();
       if (formatter == null) formatter = defaultFormatter;
       if(formatter instanceof DefaultFormatter) {
           labelText = ((DefaultFormatter)formatter).toString(value, numFormatter);
       } else {
           labelText = formatter.toString(value);
       }
       return measureTickMarkLabelSize(labelText, rotation);
   }

   /**
    * Called to set the upper and lower bound and anything else that needs to be auto-ranged
    *
    * @param minValue The min data value that needs to be plotted on this axis
    * @param maxValue The max data value that needs to be plotted on this axis
    * @param length The length of the axis in display coordinates
    * @param labelSize The approximate average size a label takes along the axis
    * @return The calculated range
    */
   @Override protected Object autoRange(double minValue, double maxValue, double length, double labelSize) {
       // check if we need to force zero into range
       double min = isForceZeroInRange() && minValue > 0 ? 0 : minValue;
       double max = isForceZeroInRange() && maxValue < 0 ? 0 : maxValue;
       double range = max - min;
       double padding = range * getAutoRangePadding();
       double paddedMin = min - padding;
       double paddedMax = max + padding;
       if ((paddedMin < 0 && min >= 0) || (paddedMin > 0 && min <= 0)) {
           // padding pushed min above or below zero so clamp to 0
           paddedMin = 0;
       }
       if ((paddedMax < 0 && max >= 0) || (paddedMax > 0 && max <= 0)) {
           // padding pushed min above or below zero so clamp to 0
           paddedMax = 0;
       }
              
       return computeRange(paddedMin, paddedMax, length, labelSize);
   }

   private Object computeRange() {
       double length = getSide().isVertical() ? getHeight() : getWidth();
       double labelSize = getTickLabelFont().getSize() * 2;
       return computeRange(getLowerBound(), getUpperBound(), length, labelSize);
   }

  /** private Range computeRange(double min, double max, double length, double labelSize) {
       
       int numOfTickMarks = (int) Math.floor(length / labelSize);
       numOfTickMarks = Math.max(numOfTickMarks, 2);
       double tickUnit = (max - min) / numOfTickMarks;
       double tickUnitRounded = 0;
       double minRounded = 0;
       double maxRounded = 0;
       int count = 0;
       double reqLength = Double.MAX_VALUE;
       String formatter = "0.00000000";
       // loop till we find a set of ticks that fit length and result in a total of less than 20 tick marks
       while (reqLength > length || count > MAX_TICK_COUNT) {
           int exp = (int)Math.floor(Math.log10(tickUnit));
           final double mant = tickUnit / Math.pow(10, exp);
           double ratio = mant;
           if (mant > 5d) {
               exp++;
               ratio = 1;
           } 
           //@natalia.milas removed this bit to avoid werid windowing of the data
           else if (mant > 1d) {
               //ratio = mant > 2.5 ? 5 : 2.5;
               ratio = Math.ceil(mant);
           }
           if (exp > 1) {
               formatter = "#,##0";
           } else if (exp == 1) {
               formatter = "0";
           } else {
               final boolean ratioHasFrac = Math.rint(ratio) != ratio;
               final StringBuilder formatterB = new StringBuilder("0");
               int n = ratioHasFrac ? Math.abs(exp) + 1 : Math.abs(exp);
               if (n > 0) formatterB.append(".");
               for (int i = 0; i < n; ++i) {
                   formatterB.append("0");
               }
               formatter = formatterB.toString();

           }
           tickUnitRounded = ratio * Math.pow(10, exp);
           minRounded = Math.floor(min / tickUnitRounded) * tickUnitRounded;
           maxRounded = Math.ceil(max / tickUnitRounded) * tickUnitRounded;
           // calculate the required length to display the chosen tick marks for real, this will handle if there are
           // huge numbers involved etc or special formatting of the tick mark label text
           double maxReqTickGap = 0;
           double last = 0;
           count = 0;
           for (double major = minRounded; major <= maxRounded; major += tickUnitRounded, count ++)  {
               double size = getSide().isVertical() ? measureTickMarkSize(major, getTickLabelRotation(), formatter).getHeight() :
                                           measureTickMarkSize(major, getTickLabelRotation(), formatter).getWidth();
               if (major == minRounded) { // first
                   last = size/2;
               } else {
                   maxReqTickGap = Math.max(maxReqTickGap, last + 6 + (size/2) );
               }
           }
           reqLength = (count-1) * maxReqTickGap;
           tickUnit = tickUnitRounded;

           // fix for RT-35600 where a massive tick unit was being selected
           // unnecessarily. There is probably a better solution, but this works
           // well enough for now.
           if (numOfTickMarks == 2 && reqLength > length) {
               break;
           }
           if (reqLength > length || count > 20) tickUnit *= 2; // This is just for the while loop, if there are still too many ticks
       }

       // calculate new scale       
       final double newScale = calculateNewScale(length, minRounded, maxRounded);       
       return new Range(minRounded, maxRounded, tickUnitRounded, newScale, formatter);   
   } **/
   
    private Range computeRange(double min, double max, double length, double labelSize) {
       
        int numOfTickMarks = (int) Math.floor(length / labelSize);
        numOfTickMarks = Math.max(numOfTickMarks, 2);
        double tickUnit = (max - min) / numOfTickMarks;
        double tickUnitRounded = 0;
        double minRounded = 0;
        double maxRounded = 0;
        int count = 0;
        double reqLength = Double.MAX_VALUE;
        String formatter = "0.00000000";
        // loop till we find a set of ticks that fit length and result in a total of less than 20 tick marks
        while (reqLength > length || count > MAX_TICK_COUNT) {
            if(isAutoRanging()){
                int exp = (int)Math.floor(Math.log10(tickUnit));
                final double mant = tickUnit / Math.pow(10, exp);
                double ratio = mant;
                //if (mant > 5d) {
                //    exp++;
                //    ratio = 1;
                //} 
                //@natalia.milas removed this bit to avoid werid windowing of the data
                //else if (mant > 1d) {
                    //ratio = mant > 2.5 ? 5 : 2.5;
                //    ratio = Math.ceil(mant);
                //}
                if (exp > 1) {
                    formatter = "#,##0";
                } else if (exp == 1) {
                    formatter = "0";
                } else {
                    final boolean ratioHasFrac = Math.rint(ratio) != ratio;
                    final StringBuilder formatterB = new StringBuilder("0");
                    int n = ratioHasFrac ? Math.abs(exp) + 1 : Math.abs(exp);
                    if (n > 0) formatterB.append(".");
                    for (int i = 0; i < n; ++i) {
                        formatterB.append("0");
                    }
                    formatter = formatterB.toString();

                }
                tickUnitRounded = ratio * Math.pow(10, exp);
                minRounded = Math.floor(min/ tickUnitRounded) * tickUnitRounded;
                maxRounded = Math.ceil(max/ tickUnitRounded) * tickUnitRounded;                
                // calculate the required length to display the chosen tick marks for real, this will handle if there are
                // huge numbers involved etc or special formatting of the tick mark label text
                double maxReqTickGap = 0;
                double last = 0;
                count = 0;
                for (double major = minRounded; major <= maxRounded; major += tickUnitRounded, count ++)  {
                    double size = getSide().isVertical() ? measureTickMarkSize(major, getTickLabelRotation(), formatter).getHeight() :
                                                measureTickMarkSize(major, getTickLabelRotation(), formatter).getWidth();
                    if (major == minRounded) { // first
                        last = size/2;
                    } else {
                        maxReqTickGap = Math.max(maxReqTickGap, last + 6 + (size/2) );
                    }
                }
                reqLength = (count-1) * maxReqTickGap;
                tickUnit = tickUnitRounded;

                // fix for RT-35600 where a massive tick unit was being selected
                // unnecessarily. There is probably a better solution, but this works
                // well enough for now.
                if (numOfTickMarks == 2 && reqLength > length) {
                    break;
                }
                if (reqLength > length || count > 20) tickUnit *= 2; // This is just for the while loop, if there are still too many ticks
            } else {
                //when Autoscale is off there should be no rouding of the limits
                int exp = (int)Math.floor(Math.log10(tickUnit));
                final double mant = tickUnit / Math.pow(10, exp);                
                if (exp > 1) {
                    formatter = "#,##0";
                } else if (exp == 1) {
                    formatter = "0";
                } else {
                    final boolean ratioHasFrac = Math.rint(mant) != mant;
                    final StringBuilder formatterB = new StringBuilder("0");
                    int n = ratioHasFrac ? Math.abs(exp) + 1 : Math.abs(exp);
                    if (n > 0) formatterB.append(".");
                    for (int i = 0; i < n; ++i) {
                        formatterB.append("0");
                    }
                    formatter = formatterB.toString();

                }
                tickUnitRounded = Math.ceil(mant)* Math.pow(10, exp);
                minRounded = min;
                maxRounded = max;
                // calculate the required length to display the chosen tick marks for real, this will handle if there are
                // huge numbers involved etc or special formatting of the tick mark label text
                double maxReqTickGap = 0;
                double last = 0;
                count = 0;
                for (double major = minRounded; major <= maxRounded; major += tickUnitRounded, count ++)  {
                    double size = getSide().isVertical() ? measureTickMarkSize(major, getTickLabelRotation(), formatter).getHeight() :
                                                measureTickMarkSize(major, getTickLabelRotation(), formatter).getWidth();
                    if (major == minRounded) { // first
                        last = size/2;
                    } else {
                        maxReqTickGap = Math.max(maxReqTickGap, last + 6 + (size/2) );
                    }
                }
                reqLength = (count-1) * maxReqTickGap;
                tickUnit = tickUnitRounded;

                // fix for RT-35600 where a massive tick unit was being selected
                // unnecessarily. There is probably a better solution, but this works
                // well enough for now.
                if (numOfTickMarks == 2 && reqLength > length) {
                    break;
                }
                if (reqLength > length || count > 20) tickUnit *= 2; // This is just for the while loop, if there are still too many ticks                
            }
        }

       // calculate new scale       
       final double newScale = calculateNewScale(length, minRounded, maxRounded);       
       return new Range(minRounded, maxRounded, tickUnitRounded, newScale, formatter);   
    }
   
   
   // -------------- STYLESHEET HANDLING ------------------------------------------------------------------------------

    /** @treatAsPrivate implementation detail */
   private static class StyleableProperties {
       private static final CssMetaData<NumberAxis,Number> TICK_UNIT =
           new CssMetaData<NumberAxis,Number>("-fx-tick-unit",
               SizeConverter.getInstance(), 5.0) {

           @Override
           public boolean isSettable(NumberAxis n) {
               return n.tickUnit == null || !n.tickUnit.isBound();
           }

           @Override
           public StyleableProperty<Number> getStyleableProperty(NumberAxis n) {
               return (StyleableProperty<Number>) n.tickUnitProperty();
           }
       };

       private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
       static {
          final List<CssMetaData<? extends Styleable, ?>> styleables = 
              new ArrayList<CssMetaData<? extends Styleable, ?>>(ValueAxis.getClassCssMetaData());
          styleables.add(TICK_UNIT);
          STYLEABLES = Collections.unmodifiableList(styleables);
       }
   }

   /**
    * @return The CssMetaData associated with this class, which may include the
    * CssMetaData of its super classes.
    * @since JavaFX 8.0
    */
   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
       return StyleableProperties.STYLEABLES;
   }

   /**
    * {@inheritDoc}
    * @since JavaFX 8.0
    */
   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
       return getClassCssMetaData();
   }

   // -------------- INNER CLASSES ------------------------------------------------------------------------------------

   /**
    * Default number formatter for NumberAxis, this stays in sync with auto-ranging and formats values appropriately.
    * You can wrap this formatter to add prefixes or suffixes;
    * @since JavaFX 2.0
    */
   public static class DefaultFormatter extends StringConverter<Number> {
       private DecimalFormat formatter;
       private String prefix = null;
       private String suffix = null;

       /**
        * Construct a DefaultFormatter for the given NumberAxis
        *
        * @param axis The axis to format tick marks for
        */
       public DefaultFormatter(final NumberAxis axis) {
           formatter = axis.isAutoRanging()? new DecimalFormat(axis.currentTickFormat.get()) : new DecimalFormat();
           axis.currentTickFormat.addListener((observable, oldValue, newValue) -> {
               formatter = new DecimalFormat(axis.currentTickFormat.get());
           });
       }

       /**
        * Construct a DefaultFormatter for the given NumberAxis with a prefix and/or suffix.
        *
        * @param axis The axis to format tick marks for
        * @param prefix The prefix to append to the start of formatted number, can be null if not needed
        * @param suffix The suffix to append to the end of formatted number, can be null if not needed
        */
       public DefaultFormatter(NumberAxis axis, String prefix, String suffix) {
           this(axis);
           this.prefix = prefix;
           this.suffix = suffix;
       }

       /**
       * Converts the object provided into its string form.
       * Format of the returned string is defined by this converter.
       * @return a string representation of the object passed in.
       * @see StringConverter#toString
       */
       @Override public String toString(Number object) {
           return toString(object, formatter);
       }

       private String toString(Number object, String numFormatter) {
           if (numFormatter == null || numFormatter.isEmpty()) {
               return toString(object, formatter);
           } else {
               return toString(object, new DecimalFormat(numFormatter));
           }
       }

       private String toString(Number object, DecimalFormat formatter) {
           if (prefix != null && suffix != null) {
               return prefix + formatter.format(object) + suffix;
           } else if (prefix != null) {
               return prefix + formatter.format(object);
           } else if (suffix != null) {
               return formatter.format(object) + suffix;
           } else {
               return formatter.format(object);
           }
       }

        /**
         * Converts the string provided into a Number defined by the this converter. Format of the string and type of
         * the resulting object is defined by this converter.
         * 
         * @return a Number representation of the string passed in.
         * @see StringConverter#toString
         */
        @Override
        public Number fromString(String string) {
            try {
                int prefixLength = (prefix == null) ? 0 : prefix.length();
                int suffixLength = (suffix == null) ? 0 : suffix.length();
                return formatter.parse(string.substring(prefixLength, string.length() - suffixLength));
            } catch (ParseException e) {
                return null;
            }
        }
   }

    /**
     * Sets axis as time axis with major tick marks as (Default) HOUR:MINUTE:SECOND:MILLISECOND.
     * 
     */
   
   private static class Range {
       final double lowerBound;
       final double upperBound;
       final double tickUnit;
       final double scale;
       final String tickFormat;

    Range(double lowerBound, double upperBound, double tickUnit, double scale, String tickFormat) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.tickUnit = tickUnit;
        this.scale = scale;
        this.tickFormat = tickFormat;
    }
   }
}
