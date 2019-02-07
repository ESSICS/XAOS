/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chart;

/**
 * A logarithmic axis implementation for JavaFX 2 charts<br>
 * <br>
 * 
 * @author Kevin Senechal
 * 
 */
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;


import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.ValueAxis;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;


public class LogAxis extends ValueAxis<Number> {

	/**
	 * The time of animation in ms
	 */
	private static final double ANIMATION_TIME = 2000;
	private final Timeline lowerRangeTimeline = new Timeline();
	private final Timeline upperRangeTimeline = new Timeline();

	private final DoubleProperty logUpperBound = new SimpleDoubleProperty();
	private final DoubleProperty logLowerBound = new SimpleDoubleProperty();
        
        private double dataMinValue;
        private double dataMaxValue;
                
	public LogAxis() {
		super();
		bindLogBoundsToDefaultBounds();                
	}

	public LogAxis(double lowerBound, double upperBound) {
		super(lowerBound, upperBound);
		try {
			validateBounds(lowerBound, upperBound);
			bindLogBoundsToDefaultBounds();
		} catch (IllegalLogarithmicRangeException e) {
			e.printStackTrace();
		}                
	}

        public LogAxis(String axisLabel,double lowerBound, double upperBound) {
		super(lowerBound, upperBound);
                setLabel(axisLabel);
		try {
			validateBounds(lowerBound, upperBound);
			bindLogBoundsToDefaultBounds();
		} catch (IllegalLogarithmicRangeException e) {
			e.printStackTrace();
		}
	} 
        
	/**
	 * Bind our logarithmic bounds with the super class bounds, consider the base 10 logarithmic scale.
	 */
	private void bindLogBoundsToDefaultBounds() {	  
                
            logLowerBound.bind(new DoubleBinding() {

                    {
                            super.bind(lowerBoundProperty());
                    }

                    @Override
                    protected double computeValue() {
                            //test boundaries
                            if(lowerBoundProperty().get() <= 0 ){
                                setAutoRanging(false);
                                lowerBoundProperty().set(1);  
                            }

                            return Math.log10(lowerBoundProperty().get());
                    }
            });
            logUpperBound.bind(new DoubleBinding() {

                    {
                            super.bind(upperBoundProperty());
                    }

                    @Override
                    protected double computeValue() {
                            if(upperBoundProperty().get() <= 0 || upperBoundProperty().get()<=lowerBoundProperty().get()){
                                setAutoRanging(false);
                                upperBoundProperty().set(lowerBoundProperty().get()+10);  
                            }                            
                            return Math.log10(upperBoundProperty().get());
                    }
            });
        }

	/**
	 * Validate the bounds by throwing an exception if the values are not conform to the mathematics log interval:
	 * [0,Double.MAX_VALUE]
	 * 
	 * @param lowerBound
	 * @param upperBound
	 * @throws IllegalLogarithmicRangeException
	 */
	private Boolean validateBounds(double lowerBound, double upperBound) throws IllegalLogarithmicRangeException {		
                if (lowerBound <= 0 || upperBound <= 0 || lowerBound >= upperBound) {
			//throw new IllegalLogarithmicRangeException(
                        System.out.print("WARNING: The logarithmic data range should be within (0,Double.MAX_VALUE] and the lowerBound should be less than the upperBound.\n");  
                        return false;
		} else {
                    return true;
                }                
                
	}
             
	@Override
	protected List<Number> calculateMinorTickMarks() {
		Number[] range = getRange();
		List<Number> minorTickMarksPositions = new ArrayList<Number>();
		if (range != null) {
			Number lowerBound = ((Number[]) range)[0];
			Number upperBound = ((Number[]) range)[1];
                        double logLowerBoundNow = Math.log10(lowerBound.doubleValue());                        
			double logUpperBoundNow = Math.log10(upperBound.doubleValue());
			int minorTickMarkCount = getMinorTickCount();                                                
                        
			for (double i = logLowerBoundNow; i <= logUpperBoundNow; i += 1) {
				for (double j = 0; j <= 9; j += (1. / minorTickMarkCount)) {
					double value = j * Math.pow(10, i);
					minorTickMarksPositions.add(value);
				}
			}
		}
		return minorTickMarksPositions;
	}

	
	@Override
	protected List<Number> calculateTickValues(double length, Object range) {
		List<Number> tickPositions = new ArrayList<Number>();
		if (range != null) {
			Number lowerBound = ((Number[]) range)[0];
			Number upperBound = ((Number[]) range)[1];
                        double logLowerBoundNow = Math.log10(lowerBound.doubleValue());                        
			double logUpperBoundNow = Math.log10(upperBound.doubleValue());                        
	
			for (double i = logLowerBoundNow; i <= logUpperBoundNow; i += 1) {
				for (double j = 1; j <= 9; j++) {
					double value = j * Math.pow(10, i);
					tickPositions.add(value);
				}
			}
		}
		return tickPositions;
	}

	@Override
	protected Number[] getRange() {
		return new Number[] { lowerBoundProperty().get(), upperBoundProperty().get() };
	}

	@Override
	protected String getTickMarkLabel(Number value) {
		NumberFormat formatter = NumberFormat.getInstance();
		formatter.setMaximumIntegerDigits(6);
		formatter.setMinimumIntegerDigits(1);
		return formatter.format(value);
	}

	
	@Override
	protected void setRange(Object range, boolean animate) {
		if (range != null) {
			Number lowerBound = ((Number[]) range)[0];
			Number upperBound = ((Number[]) range)[1];
                                                                        			
			if (animate) {
				try {
					lowerRangeTimeline.getKeyFrames().clear();
					upperRangeTimeline.getKeyFrames().clear();

					lowerRangeTimeline.getKeyFrames()
							.addAll(new KeyFrame(Duration.ZERO, new KeyValue(lowerBoundProperty(), lowerBoundProperty()
									.get())),
									new KeyFrame(new Duration(ANIMATION_TIME), new KeyValue(lowerBoundProperty(),
											lowerBound.doubleValue())));

					upperRangeTimeline.getKeyFrames()
							.addAll(new KeyFrame(Duration.ZERO, new KeyValue(upperBoundProperty(), upperBoundProperty()
									.get())),
									new KeyFrame(new Duration(ANIMATION_TIME), new KeyValue(upperBoundProperty(),
											upperBound.doubleValue())));
					lowerRangeTimeline.play();
					upperRangeTimeline.play();
				} catch (Exception e) {
					lowerBoundProperty().set(lowerBound.doubleValue());
					upperBoundProperty().set(upperBound.doubleValue());
				}
			}
			lowerBoundProperty().set(lowerBound.doubleValue());
			upperBoundProperty().set(upperBound.doubleValue());
		}
	}

	@Override
	public Number getValueForDisplay(double displayPosition) {
		double delta = logUpperBound.get() - logLowerBound.get();
		if (getSide().isVertical()) {
			return Math.pow(10, (((displayPosition - getHeight()) / -getHeight()) * delta) + logLowerBound.get());
		} else {
			return Math.pow(10, (((displayPosition / getWidth()) * delta) + logLowerBound.get()));
		}
	}

	@Override
	public double getDisplayPosition(Number value) {
		double delta = logUpperBound.get() - logLowerBound.get();
		double deltaV = Math.log10(value.doubleValue()) - logLowerBound.get();
		if (getSide().isVertical()) {
			return (1. - ((deltaV) / delta)) * getHeight();
		} else {
			return ((deltaV) / delta) * getWidth();
		}
	}
        
        @Override
        protected Object autoRange(double minValue, double maxValue, double length, double labelSize) {                               
                if (!validateBounds(minValue, maxValue)){
                    minValue = dataMinValue;
                    maxValue = dataMaxValue;
                }               
                Double[] range = new Double[] { minValue, maxValue };
                return range;
        }
        
        /**
        * Called when data has changed and the range may not be valid any more. This is only called by the chart if
        * isAutoRanging() returns true. If we are auto ranging it will cause layout to be requested and auto ranging to
        * happen on next layout pass.
        *
        * @param data The current set of all data that needs to be plotted on this axis
        */
        public void invalidateRange(List<Number> data) {
           if (data.isEmpty()) {
                dataMaxValue = getUpperBound();
                dataMinValue = getLowerBound();
            } else {
                dataMinValue = Double.MAX_VALUE;               
                dataMaxValue = -Double.MAX_VALUE;
            }
            for(Number dataValue: data) {
                if (dataValue.doubleValue() > 0){
                    dataMinValue = Math.min(dataMinValue, dataValue.doubleValue());
                    dataMaxValue = Math.max(dataMaxValue, dataValue.doubleValue());
                }
            }
           super.invalidateRange(data);
        }
                      
        /**
        * Exception to be thrown when a bound value isn't supported by the
        * logarithmic axis<br>
        *
        *
        * @author Kevin Senechal mailto: kevin.senechal@dooapp.com
        *
        */
        public class IllegalLogarithmicRangeException extends RuntimeException {
            /**
             * @param message String with exception message
             */
            public IllegalLogarithmicRangeException(String message) {
                super(message);
            }
        }
}
