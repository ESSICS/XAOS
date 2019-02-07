/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package chart.data;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import util.DataXComparator;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart.Data;

/**
 * 
 * @author Andreas Schaller
 * @author Grzegorz Kruk
 */
public final class DataReducingObservableList<X extends Number, Y extends Number>
        extends ModifiableObservableListBase<Data<X, Y>> {

    private static final int MIN_POINTS_COUNT = 2;
    private static final int DEFAULT_POINTS_COUNT = 1000;

    private final ValueAxis<X> xAxis;
    private final ObservableList<Data<X, Y>> sourceData;
    private List<Data<X, Y>> reducedData = Collections.emptyList();
    private final ChangeListener<Number> axisRangeChangeListener = new AxisRangeChangeListener();

    private final ObjectProperty<DataReducer<X, Y>> dataReducer = new SimpleObjectProperty<>(this, "dataReducer", new RamerDouglasPeuckerDataReducer<>());
    private final IntegerProperty pointsCount = new SimpleIntegerProperty(this, "pointsCount", DEFAULT_POINTS_COUNT) {
        @Override
        protected void invalidated() {
            if (get() < MIN_POINTS_COUNT) {
                throw new IllegalArgumentException("pointsCount must be grater than 1");
            }
        }
    };

    /**
     * Creates a new instance of DataReducingObservableList initializing the {@link #getSourceData() source data} to an
     * empty list.
     * 
     * @param xAxis X coordinates axis of the chart displaying the data
     */
    public DataReducingObservableList(ValueAxis<X> xAxis) {
        this(xAxis, FXCollections.observableArrayList());
    }

    /**
     * Creates a new instance of DataReducingObservableList.
     * 
     * @param xAxis X coordinates axis of the chart displaying the data
     * @param sourceData list containing data to be reduced
     */
    public DataReducingObservableList(ValueAxis<X> xAxis, ObservableList<Data<X, Y>> sourceData) {
        if (xAxis == null) {
            throw new IllegalArgumentException("X ValueAxis must be specified");
        }
        if (sourceData == null) {
            throw new IllegalArgumentException("Source list of data must not be null");
        }
        this.xAxis = xAxis;
        this.sourceData = sourceData; //NOSONAR

        xAxis.lowerBoundProperty().addListener(axisRangeChangeListener);
        xAxis.upperBoundProperty().addListener(axisRangeChangeListener);
        sourceData.addListener((ListChangeListener<Data<X, Y>>) change -> {
            reduce();
        });
    }

    public ObservableList<Data<X, Y>> getSourceData() {
        return sourceData; //NOSONAR
    }

    public DataReducer<X, Y> getDataReducer() {
        return dataReducer.get();
    }

    public void setDataReducer(DataReducer<X, Y> value) {
        dataReducer.set(value);
    }

    public ObjectProperty<DataReducer<X, Y>> dataReducerProperty() {
        return dataReducer;
    }

    public int getPointsCount() {
        return pointsCount.get();
    }

    public void setPointsCount(int value) {
        pointsCount.set(value);
    }

    public IntegerProperty fixedPointsCountProperty() {
        return pointsCount;
    }

    @Override
    protected void doAdd(int index, Data<X, Y> element) {
        throw new UnsupportedOperationException("Add operation is not allowed. Use #getSourceValues().add(..) instead");
    }

    @Override
    protected Data<X, Y> doSet(int index, Data<X, Y> element) {
        throw new UnsupportedOperationException("Set operation is not allowed. Use #getSourceValues().set(..) instead");
    }

    @Override
    protected Data<X, Y> doRemove(int index) {
        throw new UnsupportedOperationException(
                "Remove operation is not allowed. Use #getSourceValues().remove(..) instead");
    }

    @Override
    public Data<X, Y> get(int index) {
        return reducedData.get(index);
    }

    @Override
    public int size() {
        return reducedData.size();
    }

    private void reduce() {
        beginChange();
        // clear previous reduced data list
        nextRemove(0, reducedData);
        
        DataReducer<X, Y> reducer = getDataReducer();
        if (reducer == null) {
            reducedData = sourceData;
        } else {
            reducedData = reducer.reduce(getXRangeData(), getPointsCount());
        }
        
        // add new reduced data list elements
        nextAdd(0, reducedData.size());
        endChange();
    }

    
    private List<Data<X, Y>> getXRangeData() {
        if (allDataInXBounds()) {
            return sourceData;
        }
        
        DataXComparator comparator = new DataXComparator(xAxis);
        return sourceData.subList(findFromIndex(comparator), findToIndex(comparator));
    }

    private boolean allDataInXBounds() {
        if (sourceData.isEmpty()) {
            return true;
        }
        double firstX = sourceData.get(0).getXValue().doubleValue();
        double lastX = sourceData.get(sourceData.size() - 1).getXValue().doubleValue();
        return firstX >= xAxis.getLowerBound() && lastX <= xAxis.getUpperBound();
    }

    private int findFromIndex(DataXComparator comparator) {
        int fromIndex = Collections.binarySearch(sourceData, DataXComparator.key(xAxis.getLowerBound()), comparator);
        if (fromIndex < 0) {
            fromIndex = -fromIndex - 1;
            if (fromIndex > 0) {
                // Include previous point to draw a line to it
                fromIndex--;
            }
        }
        return fromIndex;
    }

    private int findToIndex(DataXComparator comparator) {
        int toIndex = Collections.binarySearch(sourceData, DataXComparator.key(xAxis.getUpperBound()), comparator);
        if (toIndex < 0) {
            toIndex = -toIndex - 1;
        }
        if (toIndex < sourceData.size() - 1) {
            // Include next point to draw a line to it
            toIndex++;
        }
        return toIndex;
    }

    /**
     * Listens to changes in lower and upper bound of the X axis and runs the reduce() method after EVENT_DELAY_MILLIS
     * to schedule only one reduction in case both lower and upper bound are changed (and therefore two events are
     * fired).
     */
    private class AxisRangeChangeListener implements ChangeListener<Number> {
        private final Timer timer = new Timer();
        private TimerTask task = null;
        private static final int EVENT_DELAY_MILLIS = 50;

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            if (task != null) {
                task.cancel();
            }
            task = new TimerTask() {
                @Override
                public void run() {
                    runReduce();
                }
            };
            timer.schedule(task, EVENT_DELAY_MILLIS);
        }

        private void runReduce() {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    reduce();
                }
            });
        }
    }
}