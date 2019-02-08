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

package chart.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import se.europeanspallationsource.xaos.ui.plot.util.AbscissaDataComparator;
import se.europeanspallationsource.xaos.ui.plot.util.Utils;

/**
 * 
 * @author gkruk 
 * @param <X> x coordinates
 * @param <Y> y coordinates
 */
public final class DataReducingSeries<X extends Number, Y extends Number> {
    private static final int MIN_POINTS_COUNT = 2;
    private static final int DEFAULT_FIXED_POINTS_COUNT = 1000;
    private static final double DEFAULT_CHART_WIDTH_TO_DATA_POINTS_RATIO = 2;

    private final Series<X, Y> target = new Series<X, Y>();

    private final ObjectProperty<DataReducer<X, Y>> dataReducer = new SimpleObjectProperty<DataReducer<X, Y>>(
            DataReducingSeries.this, "dataReducer", new RamerDouglasPeuckerDataReducer<X, Y>());

    public DataReducer<X, Y> getDataReducer() { return dataReducer.get(); }
    public void setDataReducer(DataReducer<X, Y> value) { dataReducer.set(value); }
    public ObjectProperty<DataReducer<X, Y>> dataReducerProperty() { return dataReducer; }
    
    private final ListChangeListener<Data<X, Y>> dataChangeListener = new ListChangeListener<Data<X, Y>>() {
        @Override public void onChanged(Change<? extends Data<X, Y>> change) {
            reduceData();
        }
    };

    private final ObjectProperty<ObservableList<Data<X, Y>>> data = new ObjectPropertyBase<ObservableList<Data<X, Y>>>() {
        private ObservableList<Data<X, Y>> old;
        @Override
        protected void invalidated() {
            ObservableList<Data<X, Y>> current = getValue();
            if (old != null) {
                old.removeListener(dataChangeListener);
            }
            if (current != null) {
                current.addListener(dataChangeListener);
            }
            dataChangeListener.onChanged(null);
            old = current;
        }

        @Override
        public Object getBean() {
            return DataReducingSeries.this;
        }

        @Override
        public String getName() {
            return "data";
        }
    };

    public ObservableList<Data<X, Y>> getData() { return data.getValue(); }
    public void setData(ObservableList<Data<X, Y>> value) { data.setValue(value); }
    public ObjectProperty<ObservableList<Data<X, Y>>> dataProperty() { return data; }

    public String getName() { return target.getName(); }
    public void setName(String value) { target.setName(value); }
    public StringProperty nameProperty() { return target.nameProperty(); }

    private final DoubleProperty chartWidthToDataPointsRatio = 
            new SimpleDoubleProperty(this, "chartWidthToDataPointsRatio", DEFAULT_CHART_WIDTH_TO_DATA_POINTS_RATIO);
    public double getChartWidthToDataPointsRatio() { return chartWidthToDataPointsRatio.get(); }
    public void setChartWidthToDataPointsRatio(double value) { 
        if (!Double.isFinite(value) || value <= 0) {
            throw new IllegalArgumentException("The ratio value must be a finite positive number: " + value);
        }
        chartWidthToDataPointsRatio.set(value); 
    }
    public DoubleProperty chartWidthToDataPointsRatioProperty() { return chartWidthToDataPointsRatio; }
    
    private final IntegerProperty fixedPointsCount = 
            new SimpleIntegerProperty(this, "fixedPointsCount", DEFAULT_FIXED_POINTS_COUNT);
    public int getFixedPointsCount() { return fixedPointsCount.get(); }
    public void setFixedPointsCount(int value) { 
        if (value < MIN_POINTS_COUNT) {
            throw new IllegalArgumentException("The points count must be grater than 1");
        }
        fixedPointsCount.set(value); 
    }
    public IntegerProperty fixedPointsCountProperty() { return fixedPointsCount; }
    
    private final BooleanProperty autoPointsCount = new SimpleBooleanProperty(this, "autoPointsCount", true);
    public boolean isAutoPointsCount() { return autoPointsCount.get(); }
    public void setAutoPointsCount(boolean value) { autoPointsCount.set(value); }
    public BooleanProperty autoPointsCountProperty() { return autoPointsCount; }
    
    /**
     * Construct a empty series
     */
    public DataReducingSeries() {
        this(FXCollections.<Data<X, Y>> observableArrayList());
    }

    /**
     * Constructs a Series and populates it with the given {@link ObservableList} data.
     *
     * @param data ObservableList of XYChart.Data
     */
    public DataReducingSeries(ObservableList<Data<X, Y>> data) {
        this(null, data);
    }
  
    /**
     * Constructs a named Series and populates it with the given {@link ObservableList} data.
     *
     * @param name a name for the series
     * @param data ObservableList of XYChart.Data
     */
    public DataReducingSeries(String name, ObservableList<Data<X, Y>> data) {
        setName(name);
        setData(data);
        
        bindListeners();
    }

    public Series<X, Y> getSeries() {
        return this.target;
    }

    private void bindListeners() {
        target.chartProperty().addListener(new ChangeListener<XYChart<X, Y>>() {
            @Override
            public void changed(ObservableValue<? extends XYChart<X, Y>> observable, XYChart<X, Y> oldChart,
                    XYChart<X, Y> newChart) {
                chartChanged(oldChart, newChart);
            }
        });
        autoPointsCountProperty().addListener((event)-> reduceData());
        chartWidthToDataPointsRatioProperty().addListener((observable, oldValue, newValue)-> {
            if (isAutoPointsCount()) {
                reduceData(); 
            }
        });
        fixedPointsCountProperty().addListener((observable, oldValue, newValue)-> {
            if (!isAutoPointsCount()) {
                reduceData(); 
            }
        });
    }

    private void chartChanged(XYChart<X, Y> oldChart, XYChart<X, Y> newChart) {
        if (oldChart != null) {
            unbindAxis(oldChart);
        }
        if (newChart != null) {
            if (!(newChart.getXAxis() instanceof ValueAxis<?>)) {
                throw new IllegalArgumentException("Only ValueAxis is supported as X axis");
            }
            bindAxis(newChart);
        }
    }

    private void unbindAxis(XYChart<X, Y> chart) {
        ValueAxis<X> xAxis = getXAxis(chart);
        xAxis.lowerBoundProperty().removeListener(axisRangeChangeListener);
        xAxis.upperBoundProperty().removeListener(axisRangeChangeListener);
        xAxis.widthProperty().removeListener(axisWidthChangeListener);
    }

    private void bindAxis(XYChart<X, Y> chart) {
        ValueAxis<X> xAxis = getXAxis(chart);
        xAxis.lowerBoundProperty().addListener(axisRangeChangeListener);
        xAxis.upperBoundProperty().addListener(axisRangeChangeListener);
        xAxis.widthProperty().addListener(axisWidthChangeListener);
    }

    private ValueAxis<X> getXAxis(XYChart<X, Y> chart) {
        return chart == null ? null : (ValueAxis<X>) chart.getXAxis();
    }

    private final ChangeListener<Number> axisRangeChangeListener = new ChangeListener<Number>() {
        private ExecutorService executor = Executors.newSingleThreadExecutor();
        private AtomicBoolean dataReductionNeeded = new AtomicBoolean(false);
        
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            dataReductionNeeded.set(true);
            executor.submit(new Runnable() {
                @Override public void run() {
                    // Temporary hack to avoid reducing data twice (lowerBound and upperBound update)
                    Utils.sleep(5);
                    if (dataReductionNeeded.compareAndSet(true, false)) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                reduceData();
                            }
                        }); 
                    }
                }
            });
        }
    };
    
    private final ChangeListener<Number> axisWidthChangeListener = new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            if (isAutoPointsCount()) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        reduceData();
                    }
                }); 
            }
        }
    };
    private void reduceData() {
        DataReducer<X, Y> reducer = getDataReducer();
        if (reducer == null) {
            target.getData().setAll(recreateData(getXRangeData()));
        } else {
            List<Data<X, Y>> reduced = reducer.reduce(getXRangeData(), getPointsCount());
            target.getData().setAll(recreateData(reduced));
        }
    }

    private List<Data<X, Y>> getXRangeData() {
        ValueAxis<X> xAxis = getXAxis(getSeries().getChart());
        List<Data<X, Y>> originalData = getData();
        if (xAxis == null) {
            return originalData;
        } else {
            return subList(originalData, xAxis.getLowerBound(), xAxis.getUpperBound());
        }
    }

    private List<Data<X, Y>> subList(List<Data<X, Y>> input, double lowerBound, double upperBound) {
        if (allDataInRange(input, lowerBound, upperBound)) {
            return input;
        }
        ValueAxis<X> xAxis = getXAxis(getSeries().getChart());

        AbscissaDataComparator comparator = new AbscissaDataComparator(xAxis);
        int fromIndex = Collections.binarySearch(input, AbscissaDataComparator.key(lowerBound), comparator);
        if (fromIndex < 0) {
            fromIndex = -fromIndex - 1;
            if (fromIndex > 0) {
                fromIndex--;
            }
        }

        int toIndex = Collections.binarySearch(input, AbscissaDataComparator.key(upperBound), comparator);
        if (toIndex < 0) {
            toIndex = -toIndex - 1;
        }
        if (toIndex < input.size() - 1) {
            toIndex++;
        }
        return input.subList(fromIndex, toIndex);
    }

    private boolean allDataInRange(List<Data<X, Y>> input, double lowerBound, double upperBound) {
        if (input.isEmpty()) {
            return true;
        }
        double firstX = input.get(0).getXValue().doubleValue();
        double lastX = input.get(input.size() - 1).getXValue().doubleValue();
        return firstX >= lowerBound && lastX <= upperBound;
    }

    private int getPointsCount() {
        if (isAutoPointsCount()) {
            ValueAxis<X> xAxis = getXAxis(getSeries().getChart());
            if (xAxis == null) {
                return getFixedPointsCount();
            } else {
                return (int) (xAxis.getWidth() / getChartWidthToDataPointsRatio());
            }
        } else {
            return getFixedPointsCount();
        }
    }
    
    private List<Data<X, Y>> recreateData(List<Data<X, Y>> reduced) {
        List<Data<X, Y>> result = new ArrayList<>(reduced.size());
        for (Data<X, Y> d : reduced) {
            Data<X, Y> newData = new Data<X, Y>(d.getXValue(), d.getYValue(), d.getExtraValue());
            newData.setNode(d.getNode());
            result.add(newData);
        }
        return result;
    }
}
