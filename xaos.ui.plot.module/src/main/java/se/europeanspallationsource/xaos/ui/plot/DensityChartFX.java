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

package se.europeanspallationsource.xaos.ui.plot;

import se.europeanspallationsource.xaos.ui.plot.Plugin;
import se.europeanspallationsource.xaos.ui.plot.PluginManager;

import static java.util.Objects.requireNonNull;
import static javafx.geometry.Side.LEFT;
import static javafx.geometry.Side.TOP;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.BLUE;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.INDIGO;
import static javafx.scene.paint.Color.ORANGE;
import static javafx.scene.paint.Color.RED;
import static javafx.scene.paint.Color.VIOLET;
import static javafx.scene.paint.Color.WHITE;
import static javafx.scene.paint.Color.YELLOW;
import static javafx.scene.paint.CycleMethod.NO_CYCLE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javafx.css.converter.BooleanConverter;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import static javafx.scene.paint.Color.CYAN;
import static javafx.scene.paint.Color.DARKBLUE;
import static javafx.scene.paint.Color.DARKRED;

import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import se.europeanspallationsource.xaos.ui.plot.Legend.LegendItem;
import se.europeanspallationsource.xaos.ui.plot.plugins.Pluggable;

/**
 * DensityChartFX is a specialized chart that uses colors to represent data values. 
 *
 *
 * The colors to be used for values encoding can be specified via which by default is
 * initialized to.
 *
 * @param <X> type of X values
 * @param <Y> type of Y values
 * @author Grzegorz Kruk
 */

public class DensityChartFX<X,Y> extends Chart implements Pluggable {
    
    //Variables used to include plugIns in teh Chart
    private final Group pluginsNodesGroup = new Group();
    private final PluginManager pluginManager = new PluginManager(this, pluginsNodesGroup);   
    
    private static final int INITIAL_X_AXIS_HEIGHT = 30;
    private static final int LEGEND_IMAGE_SIZE = 20;

    private final ImageView imageView = new ImageView();
    private final NumberAxis zAxis = new NumberAxis(); 
    private final Path projectionX = new Path();
    private final Path projectionY = new Path();
    private ProjData<X,Y>  projX;
    private ProjData<X,Y>  projY;
    private final Legend legend = new Legend(zAxis);
    private final Path verticalGridLines = new Path();
    private final Path horizontalGridLines = new Path();
    private final Axis<X> xAxis;
    private final Axis<Y> yAxis;

    /**
     * Construct a new DensityChartFX with the given axis.
     *
     * @param xAxis The x axis to use
     * @param yAxis The y axis to use
     */
    public DensityChartFX(Axis<X> xAxis, Axis<Y> yAxis) {
        this.xAxis = requireNonNull(xAxis, "X axis is required");
        if (xAxis.getSide() == null || xAxis.getSide().isVertical()) {
            xAxis.setSide(Side.BOTTOM);
        }
        this.yAxis = requireNonNull(yAxis, "Y axis is required");
        if (yAxis.getSide() == null || yAxis.getSide().isHorizontal()) {
            yAxis.setSide(Side.LEFT);
        }
        xAxis.autoRangingProperty().addListener((obs, oldVal, newVal) -> updateXAxisRange());
        yAxis.autoRangingProperty().addListener((obs, oldVal, newVal) -> updateYAxisRange());                
                
        legend.visibleProperty().bind(legendVisibleProperty());
        legendSideProperty().addListener((obs, oldVal, newVal) -> requestChartLayout());

        zAxis.setAutoRanging(true);
        zAxis.setAnimated(false);
        zAxis.sideProperty().bind(legendSideProperty());
        zAxis.setForceZeroInRange(false);
        zAxis.autoRangingProperty().addListener((obs, oldVal, newVal) -> updateZAxisRange());
        
        data.addListener((obs, oldData, newData) -> {
            if (oldData != null) {
                oldData.removeListener(dataListener);
            }
            if (newData != null) {
                newData.addListener(dataListener);
            }
            dataListener.invalidated(newData);
        });

        imageView.setSmooth(false);
        setAnimated(false);

        getChartChildren().addAll(imageView, xAxis, yAxis, verticalGridLines, horizontalGridLines, legend);        

        verticalGridLines.getStyleClass().setAll("chart-vertical-grid-lines");
        horizontalGridLines.getStyleClass().setAll("chart-horizontal-grid-lines");
        
        projectionX.getStyleClass().setAll("chart-projection-lines");
        projectionY.getStyleClass().setAll("chart-projection-lines");

        getStylesheets().add(DensityChartFX.class.getResource("/styles/density-map-chart.css").toExternalForm());
    }

	@Override
	public Chart getChart() {
		return this;
	}

    /**
     * Returns the x axis.
     *
     * @return x axis
     */
    public Axis<X> getXAxis() {
        return xAxis;
    }

    /**
     * Returns the y axis.
     *
     * @return y axis
     */
    public Axis<Y> getYAxis() {
        return yAxis;
    }

    /**
     * Returns the Z axis representing scale of {@link Data#getZValue(int, int) Data Z values}. The axis is used to
     * determine Z value range and render {@link #getLegend() legend}.
     * 
     * By default {@link ValueAxis#autoRangingProperty() auto-ranging} is on so that axis
     * {@link ValueAxis#lowerBoundProperty() lower} and {@link ValueAxis#upperBoundProperty() upper} bounds are updated
     * by the chart according to the {@link #getData() data} min and max value.
     * The user can fix the range by setting the {@link ValueAxis#setAutoRanging(boolean) auto-ranging} to
     * {@code false} and specifying the lower and upper bound.
     * 
     *
     * @return z axis
     */
    public NumberAxis getZAxis() {
        return zAxis;
    }
    
    private final BooleanProperty projectionLinesVisible = new StyleableBooleanProperty(false) {
        @Override
        protected void invalidated() {
            requestChartLayout();
        }

        @Override
        public Object getBean() {
            return DensityChartFX.this;
        }

        @Override
        public String getName() {
            return "projectionLinesVisible";
        }

        @Override
        public CssMetaData<DensityChartFX<?, ?>, Boolean> getCssMetaData() {
            return StyleableProperties.PROJECTION_LINE_VISIBLE;
        }
      
    };

	@Override
	public boolean isNotShowInLegend( String seriesName ) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

    /**
     * Indicates whether projections are visible or not.
     *
     * @return verticalGridLinesVisible property
     */
    public final BooleanProperty projectionLinesVisibleProperty() {
        return projectionLinesVisible;
    }

    /**
     * Indicates whether projection lines are visible.
     *
     * @return {@code true} if vertical grid lines are visible else {@code false}.
     */
    public final boolean isProjectionLinesVisible() {
        return projectionLinesVisibleProperty().get();
    }

	@Override
	public void setNotShowInLegend( String seriesName ) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

    /**
     * Sets the value of the {@link #projectionLinesVisibleProperty()}.
     *
     * @param value {@code true} to make vertical lines visible
     */
    public final void setProjectionLinesVisible(boolean value) {
        projectionLinesVisibleProperty().set(value);
    }
    
    
    private final BooleanProperty logZAxis = new StyleableBooleanProperty(false) {
        @Override
        protected void invalidated() {
            requestChartLayout();
        }

        @Override
        public Object getBean() {
            return DensityChartFX.this;
        }

        @Override
        public String getName() {
            return "logZAxis";
        }       

        @Override
        public CssMetaData<? extends Styleable, Boolean> getCssMetaData() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
      
    };
    
    /**
     * Indicates whether the zAxis values are in logarithmic form.
     *
     * @return verticalGridLinesVisible property
     */
    public final BooleanProperty logZAxisProperty() {
        return logZAxis;
    }

    /**
     * Indicates whether the zAxis values are in logarithmic form.
     *
     * @return {@code true} if vertical grid lines are visible else {@code false}.
     */
    public final boolean isLogZAxis() {
        return logZAxisProperty().get();
    }

    /**
     * Sets the value of the {@link #logZAxisProperty()}.
     *
     * @param value {@code true} to make vertical lines visible
     */
    public final void setLogZAxis(boolean value) {
        logZAxisProperty().set(value);
    }
    
    
	@Override
    public final ObservableList<Plugin> getPlugins() {
        return pluginManager.getPlugins();
    }
    
    /**
     * More robust method for adding plugins to chart.
     * Note: Only necessary if more than one plugin is being added at once. 
     * 
     * @param plugins list of XYChartPlugins to be added. 
     * 
     * 
     */
    public void addChartPlugins(ObservableList<Plugin> plugins){
         plugins.forEach(item->{
         try{
            pluginManager.getPlugins().add(item);
         }
         catch (Exception e) {
             System.out.println("Error occured whilst adding" + item.getClass().toString());
         }
         });
        
    }

    
    /**
     * Modifiable and observable list of all content in the plot. This is where implementations of XYChart should add
     * any nodes they use to draw their plot.
     *
     * @return Observable list of plot children
     */
    public ObservableList<Node> getPlotChildren() {
        return getChartChildren();
    }        

    private void movePluginsNodesToFront() {
        getPlotChildren().remove(pluginsNodesGroup);
        getPlotChildren().add(pluginsNodesGroup);       
    }

    private final BooleanProperty verticalGridLinesVisible = new StyleableBooleanProperty(false) {
        @Override
        protected void invalidated() {
            requestChartLayout();
        }

        @Override
        public Object getBean() {
            return DensityChartFX.this;
        }

        @Override
        public String getName() {
            return "verticalGridLinesVisible";
        }

        @Override
        public CssMetaData<DensityChartFX<?, ?>, Boolean> getCssMetaData() {
            return StyleableProperties.VERTICAL_GRID_LINE_VISIBLE;
        }
      
    };

    /**
     * Indicates whether vertical grid lines are visible or not.
     *
     * @return verticalGridLinesVisible property
     */
    public final BooleanProperty verticalGridLinesVisibleProperty() {
        return verticalGridLinesVisible;
    }

    /**
     * Indicates whether vertical grid lines are visible.
     *
     * @return {@code true} if vertical grid lines are visible else {@code false}.
     */
    public final boolean isVerticalGridLinesVisible() {
        return verticalGridLinesVisibleProperty().get();
    }

    /**
     * Sets the value of the {@link #verticalGridLinesVisibleProperty()}.
     *
     * @param value {@code true} to make vertical lines visible
     */
    public final void setVerticalGridLinesVisible(boolean value) {
        verticalGridLinesVisibleProperty().set(value);
    }

    private final BooleanProperty horizontalGridLinesVisible = new StyleableBooleanProperty(false) {
        @Override
        protected void invalidated() {
            requestChartLayout();
        }

        @Override
        public Object getBean() {
            return DensityChartFX.this;
        }

        @Override
        public String getName() {
            return "horizontalGridLinesVisible";
        }

        @Override
        public CssMetaData<DensityChartFX<?, ?>, Boolean> getCssMetaData() {
            return StyleableProperties.HORIZONTAL_GRID_LINE_VISIBLE;
        }
    };

    /**
     * Indicates whether horizontal grid lines are visible or not.
     *
     * @return horizontalGridLinesVisible property
     */
    public final BooleanProperty horizontalGridLinesVisibleProperty() {
        return horizontalGridLinesVisible;
    }

    /**
     * Indicates whether horizontal grid lines are visible.
     *
     * @return {@code true} if horizontal grid lines are visible else {@code false}.
     */
    public final boolean isHorizontalGridLinesVisible() {
        return horizontalGridLinesVisibleProperty().get();
    }

    /**
     * Sets the value of the {@link #verticalGridLinesVisibleProperty()}.
     *
     * @param value {@code true} to make vertical lines visible
     */
    public final void setHorizontalGridLinesVisible(boolean value) {
        horizontalGridLinesVisibleProperty().set(value);
    }

    private final BooleanProperty smooth = new SimpleBooleanProperty(this, "smooth", false) {
        @Override
        protected void invalidated() {
            requestChartLayout();
        }
    };

    /**
     * Indicates if the chart should smooth colors between data points or render each data point as a rectangle with
     * uniform color.
     * <p>
     * By default smoothing is disabled.
     * </p>
     *
     * @return smooth property
     * @see ImageView#setFitWidth(double)
     * @see ImageView#setFitHeight(double)
     * @see ImageView#setSmooth(boolean)
     */
    public BooleanProperty smoothProperty() {
        return smooth;
    }

    /**
     * Returns the value of the {@link #smoothProperty()}.
     *
     * @return {@code true} if the smoothing should be applied, {@code false} otherwise
     */
    public boolean isSmooth() {
        return smoothProperty().get();
    }

    /**
     * Sets the value of the {@link #smoothProperty()}.
     *
     * @param value {@code true} to enable smoothing
     */
    public void setSmooth(boolean value) {
        smoothProperty().set(value);
    }

    private final ObjectProperty<ColorGradient> colorGradient = new SimpleObjectProperty<ColorGradient>(this,
            "colorGradient", ColorGradient.JET_COLOR) {
        @Override
        protected void invalidated() {
            requestChartLayout();
        }
    };

    /**
     * Color gradient (linear) used to encode data point values.
     *
     * @return gradient property
     */
    public ObjectProperty<ColorGradient> colorGradientProperty() {
        return colorGradient;
    }

    /**
     * Sets the value of the {@link #colorGradientProperty()}.
     *
     * @param value the gradient to be used
     */
    public void setColorGradient(ColorGradient value) {
        colorGradientProperty().set(value);
    }

    /**
     * Returns the value of the {@link #colorGradientProperty()}.
     *
     * @return the color gradient used for encoding data values
     */
    public ColorGradient getColorGradient() {
        return colorGradientProperty().get();
    }

    private boolean isDataEmpty() {
        return getData() == null || getData().getXSize() == 0 || getData().getYSize() == 0;
    }

    private void updateXAxisRange() {
        if (xAxis.isAutoRanging()) {
            List<X> xData = new ArrayList<>();
            Data<X, Y> densityMapData = getData();
            if (densityMapData != null) {
                int xDataCount = densityMapData.getXSize();
                for (int i = 0; i < xDataCount; i++) {
                    xData.add(densityMapData.getXValue(i));                    
                }
            }
            xAxis.invalidateRange(xData);
        }
    }

    private void updateYAxisRange() {
        if (yAxis.isAutoRanging()) {
            List<Y> yData = new ArrayList<>();
            Data<X, Y> densityMapData = getData();
            if (densityMapData != null) {
                int yDataCount = densityMapData.getYSize();
                for (int i = 0; i < yDataCount; i++) {
                    yData.add(densityMapData.getYValue(i));
                }
            }
            yAxis.invalidateRange(yData);
        }
    }

    private void updateZAxisRange() {
        if (zAxis.isAutoRanging()) {
            Data<X, Y> densityMapData = getData();
            if (densityMapData == null) {
                return;
            }
            double lowerBound = Double.MAX_VALUE;
            double upperBound = Double.MIN_VALUE;

            int xDataCount = densityMapData.getXSize();
            int yDataCount = densityMapData.getYSize();
            if (xDataCount == 0 || yDataCount == 0) {
                lowerBound = zAxis.getLowerBound();
                upperBound = zAxis.getUpperBound();
            }

            for (int xIndex = 0; xIndex < xDataCount; xIndex++) {
                for (int yIndex = 0; yIndex < yDataCount; yIndex++) {
                    double zValue = densityMapData.getZValue(xIndex, yIndex);
                    lowerBound = Math.min(lowerBound, zValue);
                    upperBound = Math.max(upperBound, zValue);
                }
            }
                        
            zAxis.setLowerBound(lowerBound);
            zAxis.setUpperBound(upperBound);
                       
            // Necessary to update dataMinValue and dataMaxValue in the ValueAxis
            zAxis.invalidateRange(Collections.emptyList());
        }
    }

    @Override
    protected void layoutChartChildren(double top, double left, double width, double height) {
        if (isDataEmpty()) {
            return;
        }                

        layoutAxes(top, left, width, height);
        layoutLegend();
        layoutImageView();
        layoutGridLines();  
        layoutProjections();         
        
        movePluginsNodesToFront();     
    }

    private void layoutAxes(double top, double left, double width, double height) {
        double legendHeight = legend.getPrefHeight(width);
        double legendWidth = legend.getPrefWidth(height);

        top = snapPositionY(isLegendVisible() && getLegendSide() == TOP ? top + legendHeight : top);
        left = snapPositionX(isLegendVisible() && getLegendSide() == LEFT ? left + legendWidth : left);
        width = isLegendVisible() && getLegendSide().isVertical() ? width - legendWidth : width;
        height = isLegendVisible() && getLegendSide().isHorizontal() ? height - legendHeight : height;

        double xAxisWidth = 0;
        double xAxisHeight = INITIAL_X_AXIS_HEIGHT;
        double yAxisWidth = 0;
        double yAxisHeight = 0;

        // The axis prefWidth(..) calls autoRange(..) which at the end saves the given height as offset
        // (see ValueAxis.calculateNewScale(..). This method should not affect the state of the axis (as JavaDoc states)
        // but it actually does!! so when calling prefWidth/prefHeight we must give at the end the correct length
        // otherwise the calculation of display positions will be wrong. Took this hack from XYChart.
        for (int count = 0; count < 5; count++) {
            yAxisHeight = snapSizeY(height - xAxisHeight);
            if (yAxisHeight < 0) {
                yAxisHeight = 0;
            }
            yAxisWidth = yAxis.prefWidth(yAxisHeight);
            xAxisWidth = snapSizeX(width - yAxisWidth);
            if (xAxisWidth < 0) {
                xAxisWidth = 0;
            }
            double newXAxisHeight = xAxis.prefHeight(xAxisWidth);
            if (newXAxisHeight == xAxisHeight) {
                break;
            }
            xAxisHeight = newXAxisHeight;
        }

        xAxisWidth = Math.ceil(xAxisWidth);
        xAxisHeight = Math.ceil(xAxisHeight);
        yAxisWidth = Math.ceil(yAxisWidth);
        yAxisHeight = Math.ceil(yAxisHeight);

        double xAxisY;
        if (xAxis.getSide() == Side.TOP) {
            xAxis.setVisible(true);
            xAxisY = top;
            top += xAxisHeight;
        } else {
            xAxis.setVisible(true);
            xAxisY = top + yAxisHeight;
        }

        double yAxisX;
        if (yAxis.getSide() == Side.LEFT) {
            yAxis.setVisible(true);
            yAxisX = left;
            left += yAxisWidth;
        } else {
            yAxis.setVisible(true);
            yAxisX = left + xAxisWidth;
        }       
        xAxis.resizeRelocate(left, xAxisY, xAxisWidth, xAxisHeight);
        yAxis.resizeRelocate(yAxisX, top, yAxisWidth, yAxisHeight);
        
        xAxis.requestAxisLayout();
        xAxis.layout();
        yAxis.requestAxisLayout();
        yAxis.layout();
    }

    /**
     * We create an image that corresponds to the display position of min/max values of the X and Y axis and we clip it
     * to the chart area i.e. to the area between X and Y axis. This is to properly display the part of image that
     * corresponds to currently set X/Y bounds (if autoRanging is off).
     */
    private void layoutImageView() {
        Data<X, Y> densityMapData = getData();
        int xSize = densityMapData.getXSize();
        int ySize = densityMapData.getYSize();

        double xStart = Math.ceil(xAxis.getDisplayPosition(densityMapData.getXValue(0)));
        double xEnd = Math.ceil(xAxis.getDisplayPosition(densityMapData.getXValue(xSize-1)));
        double yStart = Math.ceil(yAxis.getDisplayPosition(densityMapData.getYValue(0)));
        double yEnd = Math.ceil(yAxis.getDisplayPosition(densityMapData.getYValue(ySize-1)));               

        double imageWidth = xEnd - xStart;
        double imageHeight = yStart - yEnd;
        int scaleX = 1;
        int scaleY = 1;

        if (!isSmooth()) {
            scaleX = Math.max((int) imageWidth / xSize, 1);
            scaleY = Math.max((int) imageHeight / ySize, 1);
        }

        WritableImage image = new WritableImage(xSize * scaleX, ySize * scaleY);
        PixelWriter pixelWriter = image.getPixelWriter();                        
        
        double zMin = zAxis.getLowerBound();
        double zMax = zAxis.getUpperBound();
      
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                double offset = (densityMapData.getZValue(x, y) - zMin) / (zMax - zMin);                                                    
                Color color = getColor(offset);
                for (int dx = 0; dx < scaleX; dx++) {
                    for (int dy = 0; dy < scaleY; dy++) {
                        pixelWriter.setColor(x * scaleX + dx, y * scaleY + dy, color);
                    }
                }
            }
        }
        imageView.setImage(image);
        imageView.setClip(new Rectangle(-xStart, -yEnd, xAxis.getWidth(), yAxis.getHeight()));        
        imageView.resizeRelocate(xAxis.getLayoutX() + xStart, yAxis.getLayoutY() + yEnd, imageWidth, imageHeight);        
        imageView.setFitWidth(imageWidth);
        imageView.setFitHeight(imageHeight);                   

    }
    
    private static Number[] toNumbers(double[] array) {
        Number[] result = new Number[array.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = array[i];
        }
        return result;
    }

    
    private void layoutProjections(){  
          
        getPlotChildren().remove(projectionX);
        getPlotChildren().remove(projectionY);
        
        projectionX.getElements().clear();
        projectionY.getElements().clear();
        
        Boolean startProjection = false;
        
        if(isProjectionLinesVisible()){
            for (int xIndex = 0; xIndex < getData().getXSize(); xIndex++) {
                double x = xAxis.getLayoutX()+xAxis.getDisplayPosition(projX.getXValue(xIndex));
                double y = yAxis.getLayoutY()+yAxis.getHeight()-projX.getYDoubleValue(xIndex)*yAxis.getHeight()*0.2;                                   
                if (x>xAxis.getLayoutX() && x<(xAxis.getWidth()+xAxis.getLayoutX())) {
                    if(!startProjection) projectionX.getElements().add(new MoveTo(x,y));                    
                    projectionX.getElements().add(new LineTo(x,y));
                    startProjection = true;
                }                
            }   
            
            startProjection = false;
            
            for (int yIndex = 0; yIndex < getData().getYSize(); yIndex++) {
                double x = xAxis.getLayoutX()+projY.getXDoubleValue(yIndex)*xAxis.getWidth()*0.2;
                double y = yAxis.getLayoutY()+yAxis.getDisplayPosition(projY.getYValue(yIndex));                   
                if (y>yAxis.getLayoutY() && y<(yAxis.getHeight()+yAxis.getLayoutY())) {
                    if(!startProjection) projectionY.getElements().add(new MoveTo(x,y));                    
                    projectionY.getElements().add(new LineTo(x,y));
                    startProjection = true;
                }
                
            } 
           
            getPlotChildren().add(projectionX);
            getPlotChildren().add(projectionY);
        }
    }
    
    public final ProjData<Number,Number> getXProjection(DensityChartFX.DefaultData<Number,Number> data){
        double yValues = 0.0;
        double datamin = Double.MAX_VALUE;
        double datamax = Double.MIN_VALUE;
        double ymax = Double.MIN_VALUE;
        double[] seriesX = new double[data.getXSize()];
        double[] seriesY = new double[data.getXSize()];
        
        for (int yIndex = 0; yIndex < data.getYSize(); yIndex++) {                
            ymax = Math.max(ymax,data.getYValue(yIndex).doubleValue());
        }
        
        for (int xIndex = 0; xIndex < data.getXSize(); xIndex++) {
            yValues = 0.0;
            for (int yIndex = 0; yIndex < data.getYSize(); yIndex++) {                
                yValues = yValues+ data.getZValue(xIndex, yIndex);                
            }
            datamin = Math.min(datamin,yValues);
            datamax = Math.max(datamax,yValues);
            seriesX[xIndex]=(double) data.getXValue(xIndex);
            seriesY[xIndex]= yValues;
        }    
        
        for(int xIndex = 0; xIndex < data.getXSize(); xIndex++) {
            seriesY[xIndex] = ((seriesY[xIndex]-datamin)/(datamax-datamin));//*ymax*0.2);
        }
        
        return new ProjData<>(toNumbers(seriesX), toNumbers(seriesY));
        
    }
    
    public final ProjData<Number,Number> getYProjection(DensityChartFX.DefaultData<Number,Number> data){
        double xValues = 0.0;
        double datamin = Double.MAX_VALUE;
        double datamax = Double.MIN_VALUE;
        double xmax = Double.MIN_VALUE;
        double[] seriesX = new double[data.getYSize()];
        double[] seriesY = new double[data.getYSize()];
        
        for (int xIndex = 0; xIndex < data.getXSize(); xIndex++) {                        
            xmax = Math.max(xmax,data.getXValue(xIndex).doubleValue());
        }
        
        for (int yIndex = 0; yIndex < data.getYSize(); yIndex++) {
            xValues = 0.0;
            for (int xIndex = 0; xIndex < data.getXSize(); xIndex++) {
                xValues = xValues+ data.getZValue(xIndex, yIndex);
            }
            datamin = Math.min(datamin,xValues);
            datamax = Math.max(datamax,xValues);
            seriesX[yIndex]= xValues;
            seriesY[yIndex]= (double) data.getYValue(yIndex);
        }     
        
        for (int yIndex = 0; yIndex < data.getYSize(); yIndex++) {
            seriesX[yIndex] = ((seriesX[yIndex]-datamin)/(datamax-datamin));//*xmax*0.2);            
        }
        
        return new ProjData<>(toNumbers(seriesX), toNumbers(seriesY));
        
    }

    private Color getColor(final double offset) {
        double lowerOffset = 0.0;
        double upperOffset = 1.0;
        Color lowerColor = Color.BLACK;
        Color upperColor = Color.WHITE;

        for (Stop stop : getColorGradient().getStops()) {
            double currentOffset = stop.getOffset();
            if(isLogZAxis()){
                if(currentOffset>0){
                    currentOffset = Math.log(stop.getOffset());
                }
            }
            if (currentOffset == offset) {
                return stop.getColor();
            } else if (currentOffset < offset) {
                lowerOffset = currentOffset;
                lowerColor = stop.getColor();
            } else {
                upperOffset = currentOffset;
                upperColor = stop.getColor();
                break;
            }
        }

        double interpolationOffset = (offset - lowerOffset) / (upperOffset - lowerOffset);
        return lowerColor.interpolate(upperColor, interpolationOffset);
    }

    private void layoutGridLines() {
        double xAxisWidth = xAxis.getWidth();
        double yAxisHeight = yAxis.getHeight();
        double left = xAxis.getLayoutX();
        double top = yAxis.getLayoutY();

        verticalGridLines.getElements().clear();
        if (isVerticalGridLinesVisible()) {
            ObservableList<Axis.TickMark<X>> xTickMarks = xAxis.getTickMarks();
            for (int i = 0; i < xTickMarks.size(); i++) {
                Axis.TickMark<X> tick = xTickMarks.get(i);
                final double xPos = xAxis.getDisplayPosition(tick.getValue());
                if (xPos > 0 && xPos <= xAxisWidth) {
                    verticalGridLines.getElements().add(new MoveTo(left + xPos, top));
                    verticalGridLines.getElements().add(new LineTo(left + xPos, top + yAxisHeight));
                }
            }
        }

        horizontalGridLines.getElements().clear();
        if (isHorizontalGridLinesVisible()) {
            ObservableList<Axis.TickMark<Y>> yTickMarks = yAxis.getTickMarks();
            for (int i = 0; i < yTickMarks.size(); i++) {
                Axis.TickMark<Y> tick = yTickMarks.get(i);
                final double yPos = yAxis.getDisplayPosition(tick.getValue());
                if (yPos >= 0 && yPos < yAxisHeight) {
                    horizontalGridLines.getElements().add(new MoveTo(left, top + yPos));
                    horizontalGridLines.getElements().add(new LineTo(left + xAxisWidth, top + yPos));
                }
            }
        }
    }

    private void layoutLegend() {
        if (!isLegendVisible()) {
            return;
        }
        double legendX;
        double legendY;
        double legendWidth;
        double legendHeight;

        if (getLegendSide().isHorizontal()) {
            legendWidth = xAxis.getWidth();
            legendHeight = legend.getPrefHeight(legendWidth);
            legendX = xAxis.getLayoutX();
            if (xAxis.getSide() == TOP) {
                legendY = getLegendSide() == TOP ? xAxis.getLayoutY() - legendHeight
                        : yAxis.getLayoutY() + yAxis.getHeight();
            } else {
                legendY = getLegendSide() == TOP ? yAxis.getLayoutY() - legendHeight
                        : xAxis.getLayoutY() + xAxis.getHeight();
            }
        } else {
            legendHeight = yAxis.getHeight();
            legendWidth = legend.getPrefWidth(legendHeight);
            if (yAxis.getSide() == LEFT) {
                legendX = getLegendSide() == LEFT ? yAxis.getLayoutX() - legendWidth
                        : xAxis.getLayoutX() + xAxis.getWidth();
            } else {
                legendX = getLegendSide() == LEFT ? xAxis.getLayoutX() - legendWidth
                        : yAxis.getLayoutX() + yAxis.getWidth();
            }
            legendY = yAxis.getLayoutY();
        }
        legend.resizeRelocate(Math.ceil(legendX), Math.ceil(legendY), Math.ceil(legendWidth), Math.ceil(legendHeight));
        legend.requestLayout();
        legend.layout();
    }

    private final InvalidationListener dataListener = obs -> {
        updateXAxisRange();
        updateYAxisRange();
        updateZAxisRange();
        projX = (ProjData<X,Y>) getXProjection((DefaultData<Number, Number>) getData());
        projY = (ProjData<X,Y>) getYProjection((DefaultData<Number, Number>) getData());
        requestChartLayout();
    };

    private final ObjectProperty<Data<X, Y>> data = new SimpleObjectProperty<>(this, "data");

    /**
     * DensityChartFX data.
     *
     * @return data property
     */
    public final ObjectProperty<Data<X, Y>> dataProperty() {
        return data;
    }

    /**
     * Returns the value of the {@link #dataProperty()}.
     *
     * @return data property
     */
    public final Data<X, Y> getData() {
        return dataProperty().getValue();
    }

    /**
     * Sets the data.
     *
     * @param value the data to be rendered
     */
    public final void setData(Data<X, Y> value) {
        
        dataProperty().setValue(value);
    }

    /**
     * Heat map data.
     *
     * @param <X> type of X values
     * @param <Y> type of Y values
     */
    public interface Data<X, Y> extends Observable {

        /**
         * Size of the data along X axis.
         *
         * @return number of X coordinates
         */
        int getXSize();

        /**
         * Size of the data along Y axis.
         *
         * @return number of Y coordinates
         */
        int getYSize();

        /**
         * Returns X coordinate at given index.
         *
         * @param xIndex index of the X coordinate
         * @return X coordinate
         * @see #getXSize()
         */
        X getXValue(int xIndex);

        /**
         * Returns Y coordinate at given index.
         *
         * @param yIndex index of the Y coordinate
         * @return Y coordinate
         * @see #getYSize()
         */
        Y getYValue(int yIndex);

        /**
         * Returns the Z value for given X and Y coordinate index.
         *
         * @param xIndex index of the X coordinate
         * @param yIndex index of the Y coordinate
         * @return the value
         */
        double getZValue(int xIndex, int yIndex);
    }

    /**
     * Abstract data implementing {@link Observable} interface.
     *
     * @param <X> type of X values
     * @param <Y> type of Y values
     */
    public abstract static class AbstractData<X, Y> implements Data<X, Y> {
        private final List<InvalidationListener> listeners = new LinkedList<>();

        @Override
        public void addListener(InvalidationListener listener) {
            Objects.requireNonNull(listener, "InvalidationListener must not be null");
            listeners.add(listener);
        }

        @Override
        public void removeListener(InvalidationListener listener) {
            listeners.remove(listener);
        }

        /**
         * Notifies listeners that the data has been invalidated. If the data is added to the chart, it triggers
         * repaint.
         */
        public void fireInvalidated() {
            if (!listeners.isEmpty()) {
                for (InvalidationListener listener : new ArrayList<>(listeners)) {
                    listener.invalidated(this);
                }
            }
        }
    }

    /**
     * {@link Data} implementation based on arrays.
     *
     * @param <X> type of X coordinate
     * @param <Y> type of Y coordinate
     */
    public static class DefaultData<X, Y> extends AbstractData<X, Y> {
        private X[] xValues;
        private Y[] yValues;
        private double[][] zValues;

        /**
         * Creates new instance.
         *
         * @param xValues x coordinates
         * @param yValues y coordinates
         * @param zValues z values
         * @see #set(Object[], Object[], double[][])
         */
        public DefaultData(X[] xValues, Y[] yValues, double[][] zValues) {
            set(xValues, yValues, zValues);
        }

        @Override
        public final int getXSize() {
            return xValues.length;
        }

        @Override
        public final int getYSize() {
            return yValues.length;
        }

        @Override
        public final X getXValue(int xIndex) {
            return xValues[xIndex];
        }

        @Override
        public final Y getYValue(int yIndex) {
            return yValues[yIndex];
        }

        @Override
        public final double getZValue(int xIndex, int yIndex) {
            return zValues[xIndex][yIndex];
        }               

        /**
         * Sets the data.
         *
         * @param xValues x coordinates
         * @param yValues y coordinates
         * @param zValues z values where first dimension should be equal to x coordinates length and second dimension to
         *            the y coordinates length; the value at [0, 0] index corresponds to the bottom-left corner of the
         *            chart
         */
        public final void set(X[] xValues, Y[] yValues, double[][] zValues) {
            Objects.requireNonNull(xValues, "xValues must not be null");
            Objects.requireNonNull(yValues, "yValues must not be null");
            Objects.requireNonNull(zValues, "zValues must not be null");

            this.xValues = xValues;
            this.yValues = yValues;
            this.zValues = zValues;
            fireInvalidated();
        }
    }

    /**
     * {@link Data} implementation based on arrays.
     *
     * @param <X> type of X coordinate
     * @param <Y> type of Y coordinate
     */
    public static class ProjData<X, Y> extends AbstractData<X, Y> {
        private X[] xValues;
        private Y[] yValues;

        /**
         * Creates new instance.
         *
         * @param xValues x coordinates
         * @param yValues y coordinates
         * @see #set(Object[], Object[])
         */
        public ProjData(X[] xValues, Y[] yValues) {
            set(xValues, yValues);
        }

        @Override
        public final int getXSize() {
            return xValues.length;
        }

        @Override
        public final int getYSize() {
            return yValues.length;
        }

        @Override
        public final X getXValue(int xIndex) {
            return xValues[xIndex];
        }
        
        public final double getXDoubleValue(int xIndex) {
            return ((Number) xValues[xIndex]).doubleValue();
        }

        @Override
        public final Y getYValue(int yIndex) {
            return yValues[yIndex];
        }        

        public final double getYDoubleValue(int yIndex) {
            return ((Number) yValues[yIndex]).doubleValue();
        }
        
        /**
         * Sets the data.
         *
         * @param xValues x coordinates
         * @param yValues y coordinates        
         */
        public final void set(X[] xValues, Y[] yValues) {
            Objects.requireNonNull(xValues, "xValues must not be null");
            Objects.requireNonNull(yValues, "yValues must not be null");

            this.xValues = xValues;
            this.yValues = yValues;
            fireInvalidated();
        }

        @Override
        public double getZValue(int xIndex, int yIndex) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    
    /**
     * Color Gradient class provides colors to encode DensityChartFx data values.
     * Color Gradient should contain two or more stops with offsets between
     * 0.0 and 1.0. The  DensityChartFx  will apply a linear interpolation between colors with a fraction calculated
     * from the value and lower/upper bound of the  DensityChartFx ZAxis.
     *
     * @see LinearGradient
     */
    public static final class ColorGradient {
        /**
         * Rainbow colors gradient: violet, indigo, blue, green, yellow, orange and red.
         */
        public static final ColorGradient RAINBOW = new ColorGradient(new Stop(0.0, VIOLET), new Stop(0.17, INDIGO),
                new Stop(0.34, BLUE), new Stop(0.51, GREEN), new Stop(0.68, YELLOW), new Stop(0.85, ORANGE),
                new Stop(1.0, RED));               
        public static final ColorGradient INVERTED_RAINBOW = new ColorGradient(new Stop(1.0, VIOLET), new Stop(0.85, INDIGO),
                new Stop(0.68, BLUE), new Stop(0.51, GREEN), new Stop(0.34, YELLOW), new Stop(0.17, ORANGE),
                new Stop(0.0, RED));
               
        /**
         * Jet color gradient.
         */
        public static final ColorGradient JET_COLOR = new ColorGradient(new Stop(0.0, DARKBLUE), new Stop(0.125, BLUE),
                new Stop(0.375, CYAN), new Stop(0.625, YELLOW), new Stop(0.875, RED), new Stop(1.0, DARKRED));
        
        /**
         * White to black gradient.
         */
        public static final ColorGradient WHITE_BLACK = new ColorGradient(new Stop(0.0, WHITE), new Stop(1.0, BLACK));
        /**
         * Black to white gradient.
         */
        public static final ColorGradient BLACK_WHITE = new ColorGradient(new Stop(0.0, BLACK), new Stop(1.0, WHITE));

        /**
         * Red, yellow, white.
         */
        public static final ColorGradient SUNRISE = new ColorGradient(new Stop(0.0, RED), new Stop(0.66, YELLOW),
                new Stop(1.0, WHITE));

        private final List<Stop> stops;

        /**
         * Creates a new instance of ColorGradient.
         *
         * @param stops the gradient's color specification; should contain at least two stops with offsets between 0.0
         *            and 1.0
         * @see #getStops()
         */
        public ColorGradient(Stop... stops) {
            // Use LinearGradient to normalize stops
            this.stops = new LinearGradient(0, 0, 0, 0, false, CycleMethod.NO_CYCLE, stops).getStops();
            
        }

        /**
         * Returns the gradient stops.
         *        
         * @see LinearGradient#getStops()
         * @return gradient stops
         */
        public List<Stop> getStops() {
            return stops;
        }
    }

    /**
     * We layout the legend as a part of chart-content rather than relying on the Chart.layoutChildren() to make the
     * layout. The reason is that we want the legend width to be equal to the X axis width (for TOP and BOTTOM position)
     * and legend's height to be equal to the Y axis height (for LEFT and RIGHT position), but the calculation of axis
     * size is done after Chart.layoutChildren() makes the layout of the legend.
     */
    private class Legend extends Region {
        private final Rectangle gradientRect = new Rectangle();

        Legend(NumberAxis zAxis) {
            getStyleClass().add("chart-legend");
            getChildren().addAll(gradientRect, zAxis);
        }

        double getPrefWidth(double height) {
            if (getLegendSide().isHorizontal()) {
                return xAxis.getWidth();
            }
            return LEGEND_IMAGE_SIZE + zAxis.prefWidth(height) + getPadding().getLeft() + getPadding().getRight();
        }

        double getPrefHeight(double width) {
            if (getLegendSide().isHorizontal()) {
                return LEGEND_IMAGE_SIZE + zAxis.prefHeight(width) + getPadding().getTop() + getPadding().getBottom();
            }
            return yAxis.getHeight();
        }

        @Override
        protected void layoutChildren() {
            if (getLegendSide().isHorizontal()) {
                layoutLegendHorizontally();
            } else {
                layoutLegendVertically();
            }
            zAxis.requestAxisLayout();
            zAxis.layout();
        }

        private void layoutLegendHorizontally() {
            double legendWidth = getWidth();
            double zAxisHeight = snapSizeX(zAxis.prefHeight(legendWidth));

            gradientRect.setX(0);
            gradientRect.setWidth(legendWidth);
            gradientRect.setHeight(LEGEND_IMAGE_SIZE);
            zAxis.setLayoutX(0);
            zAxis.resize(legendWidth - 1, zAxisHeight);

            if (getLegendSide() == Side.BOTTOM) {
                gradientRect.setY(getPadding().getTop());
                zAxis.setLayoutY(gradientRect.getY() + gradientRect.getHeight());
            } else {
                zAxis.setLayoutY(getPadding().getTop());
                gradientRect.setY(zAxis.getLayoutY() + zAxis.getHeight());
            }
            gradientRect.setFill(new LinearGradient(0, 0, 1, 0, true, NO_CYCLE, getColorGradient().getStops()));
        }

        private void layoutLegendVertically() {
            double legendHeight = getHeight();
            double zAxisWidth = snapSizeY(zAxis.prefWidth(legendHeight));

            gradientRect.setY(0);
            gradientRect.setWidth(LEGEND_IMAGE_SIZE);
            gradientRect.setHeight(legendHeight);
            zAxis.setLayoutY(0);
            zAxis.resize(zAxisWidth, legendHeight - 1);

            if (getLegendSide() == Side.LEFT) {
                zAxis.setLayoutX(getPadding().getLeft());
                gradientRect.setX(zAxis.getLayoutX() + zAxisWidth);
            } else {
                gradientRect.setX(getPadding().getLeft());
                zAxis.setLayoutX(gradientRect.getX() + gradientRect.getWidth());
            }
            gradientRect.setFill(new LinearGradient(0, 1, 0, 0, true, NO_CYCLE, getColorGradient().getStops()));
        }
    }


    private static class StyleableProperties {
        private static final CssMetaData<DensityChartFX<?, ?>, Boolean> HORIZONTAL_GRID_LINE_VISIBLE = new CssMetaData<DensityChartFX<?, ?>, Boolean>(
                "-fx-horizontal-grid-lines-visible", BooleanConverter.getInstance(), Boolean.TRUE) {

            @Override
            public boolean isSettable(DensityChartFX<?, ?> node) {
                return node.horizontalGridLinesVisible == null || !node.horizontalGridLinesVisible.isBound();
            }

            @SuppressWarnings("unchecked")
            @Override
            public StyleableProperty<Boolean> getStyleableProperty(DensityChartFX<?, ?> node) {
                return (StyleableProperty<Boolean>) node.horizontalGridLinesVisibleProperty();
            }
        };

        private static final CssMetaData<DensityChartFX<?, ?>, Boolean> VERTICAL_GRID_LINE_VISIBLE = new CssMetaData<DensityChartFX<?, ?>, Boolean>(
                "-fx-vertical-grid-lines-visible", BooleanConverter.getInstance(), Boolean.TRUE) {

            @Override
            public boolean isSettable(DensityChartFX<?, ?> node) {
                return node.verticalGridLinesVisible == null || !node.verticalGridLinesVisible.isBound();
            }

            @SuppressWarnings("unchecked")
            @Override
            public StyleableProperty<Boolean> getStyleableProperty(DensityChartFX<?, ?> node) {
                return (StyleableProperty<Boolean>) node.verticalGridLinesVisibleProperty();
            }
        };
        
        private static final CssMetaData<DensityChartFX<?, ?>, Boolean> PROJECTION_LINE_VISIBLE = new CssMetaData<DensityChartFX<?, ?>, Boolean>(
                "-fx-projection-lines-visible", BooleanConverter.getInstance(), Boolean.TRUE) {

            @Override
            public boolean isSettable(DensityChartFX<?, ?> node) {
                return node.projectionLinesVisible == null || !node.projectionLinesVisible.isBound();
            }

            @SuppressWarnings("unchecked")
            @Override
            public StyleableProperty<Boolean> getStyleableProperty(DensityChartFX<?, ?> node) {
                return (StyleableProperty<Boolean>) node.projectionLinesVisibleProperty();
            }
        };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Chart.getClassCssMetaData());
            styleables.add(HORIZONTAL_GRID_LINE_VISIBLE);
            styleables.add(VERTICAL_GRID_LINE_VISIBLE);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    /**
     * @return The CssMetaData associated with this class, which may include the CssMetaData of its super classes.
     */
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }    
    
	@Override
	public ObservableList<LegendItem> getLegendItems() {

		Node legend = getLegend();

		if ( legend instanceof se.europeanspallationsource.xaos.ui.plot.Legend ) {
			return ((se.europeanspallationsource.xaos.ui.plot.Legend) legend).getItems();
		} else {
			return FXCollections.emptyObservableList();
		}

	}

}
