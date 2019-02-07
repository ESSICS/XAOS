/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package plugins;

import chart.DensityChartFX;
import static javafx.scene.input.MouseButton.PRIMARY;
import static javafx.scene.input.MouseButton.SECONDARY;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Predicate;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import chart.XYChartPlugin;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.Chart;
import static util.Assert.assertValueAxis;

/**
 * Zoom capabilities along X, Y or both axis. For very zoom-in operation the current X and Y range is remembered 
 * and restored upon following zoom-out operation.  
 * <ul>
 * <li>zoom-in - triggered on {@link MouseEvent#DRAG_DETECTED DRAG_DETECTED} event that is accepted by
 * {@link #getZoomInMouseFilter() zoom-in filter}. It shows a zooming rectangle determining the zoom window once mouse
 * button is released.</li>
 * <li>zoom-out - triggered on {@link MouseEvent#MOUSE_CLICKED MOUSE_CLICKED} event that is accepted by
 * {@link #getZoomOutMouseFilter() zoom-out filter}. It restores the previous ranges on both axis.</li>
 * <li>zoom-origin - triggered on {@link MouseEvent#MOUSE_CLICKED MOUSE_CLICKED} event that is accepted by
 * {@link #getZoomOriginMouseFilter() zoom-origin filter}. It restores the initial ranges on both axis as it was at the moment
 * of the first zoom-in operation.</li>
 * </ul>
 * 
 * @author Grzegorz Kruk
 */
public final class Zoom extends XYChartPlugin {
    private static final int ZOOM_RECT_MIN_SIZE = 5;
    private static final Duration DEFAULT_ZOOM_DURATION = Duration.millis(500);

    /**
     * Default zoom-in mouse filter passing on left mouse button (only).
     */
    public static final Predicate<MouseEvent> DEFAULT_ZOOM_IN_MOUSE_FILTER = new Predicate<MouseEvent>() {
        @Override
        public boolean test(MouseEvent event) {
            return event.getButton() == PRIMARY && !event.isMiddleButtonDown() && !event.isSecondaryButtonDown()
                    && metaKeysUp(event);
        }
    };

    /**
     * Default zoom-out mouse filter passing on right mouse button (only).
     */
    public static final Predicate<MouseEvent> DEFAULT_ZOOM_OUT_MOUSE_FILTER = new Predicate<MouseEvent>() {
        @Override
        public boolean test(MouseEvent event) {
            return event.getButton() == SECONDARY && !event.isPrimaryButtonDown() && !event.isMiddleButtonDown()
                    && metaKeysUp(event);
        }
    };

    /**
     * Default zoom-origin mouse filter passing on right mouse button with {@link MouseEvent#isControlDown() control key
     * down}.
     */
    public static final Predicate<MouseEvent> DEFAULT_ZOOM_ORIGIN_MOUSE_FILTER = new Predicate<MouseEvent>() {
        @Override public boolean test(MouseEvent event) {
            return event.getButton() == SECONDARY && !event.isPrimaryButtonDown() && !event.isMiddleButtonDown()
                    && event.isControlDown() && !event.isAltDown() && !event.isMetaDown() && !event.isShiftDown();
        }
    };

    private static boolean metaKeysUp(MouseEvent event) {
        return !event.isAltDown() && !event.isControlDown() && !event.isMetaDown() && !event.isShiftDown();
    }

    /** Determines if zoom-in should be allowed along a single axis or on both. By default it's {@link AxisMode#XY}.*/
    private final ObjectProperty<AxisMode> zoomMode = new SimpleObjectProperty<>(this, "zoomMode", AxisMode.XY);
    public void setZoomMode(AxisMode value) { zoomMode.set(value); }
    public AxisMode getZoomMode() { return zoomMode.get(); }
    public ObjectProperty<AxisMode> zoomModeProperty() { return zoomMode; }

    /** When {@code true} zooming will be animated. By default it's {@code false}. */
    private final BooleanProperty animated = new SimpleBooleanProperty(this, "animated", false);
    public void setAnimated(boolean value) { animated.set(value); }
    public boolean isAnimated() { return animated.get(); }
    public BooleanProperty animatedProperty() { return animated; }

    /** Duration of the {@link #animatedProperty() animated} zoom. By default initialized to 500ms.*/
    private final ObjectProperty<Duration> zoomDuration = new SimpleObjectProperty<Duration>(this, "zoomDuration", DEFAULT_ZOOM_DURATION);
    public void setZoomDuration(Duration value) { zoomDuration.set(value); }
    public Duration getZoomDuration() { return zoomDuration.get(); }
    public ObjectProperty<Duration> zoomDurationProperty() { return zoomDuration; }

    /** Mouse cursor used when drawing zooming rectangle. */
    private final ObjectProperty<Cursor> zoomInCursor = new SimpleObjectProperty<Cursor>(this, "zoomInCursor", Cursor.CROSSHAIR);
    public void setZoomInCursor(Cursor value) { zoomInCursor.set(value); }
    public Cursor getZoomInCursor() { return zoomInCursor.get(); }
    public ObjectProperty<Cursor> zoomInCursorProperty() { return zoomInCursor; }

    private Predicate<MouseEvent> zoomInMouseFilter = DEFAULT_ZOOM_IN_MOUSE_FILTER;
    private Predicate<MouseEvent> zoomOutMouseFilter = DEFAULT_ZOOM_OUT_MOUSE_FILTER;
    private Predicate<MouseEvent> zoomOriginMouseFilter = DEFAULT_ZOOM_ORIGIN_MOUSE_FILTER;

    private final Rectangle zoomRectangle = new Rectangle();
    private Point2D zoomStartPoint = null;
    private Point2D zoomEndPoint = null;
    private final Deque<Rectangle2D> zoomStack = new ArrayDeque<>();
    private final Timeline zoomAnimation = new Timeline();

    /**
     * Creates and initializes a new instance of Zoom with animation disabled and with {@link #zoomModeProperty()
     * zoomMode} initialized to {@link AxisMode#XY}.
     */
    public Zoom() {
        this(AxisMode.XY);
    }

    /**
     * Creates and initializes a new instance of Zoom with animation disabled.
     * 
     * @param zoomMode initial value of {@link #zoomModeProperty() zoomMode} property
     */
    public Zoom(AxisMode zoomMode) {
        this(zoomMode, false);
    }

    /**
     * Creates and initializes a new instance of Zoom.
     * 
     * @param zoomMode initial value of {@link #zoomModeProperty() zoomMode} property
     * @param animated initial value of {@link #animatedProperty() animated} property
     */
    public Zoom(AxisMode zoomMode, boolean animated) {
        setZoomMode(zoomMode);
        setAnimated(animated);

        zoomRectangle.setManaged(false);
        zoomRectangle.getStyleClass().add("chart-zoom-rect");
        getPlotChildren().add(zoomRectangle);
    }

    /**
     * Returns zoom-in mouse event filter.
     * 
     * @return zoom-in mouse event filter
     * @see #setZoomInMouseFilter(Predicate)
     */
    public Predicate<MouseEvent> getZoomInMouseFilter() {
        return zoomInMouseFilter;
    }

    /**
     * Sets filter on {@link MouseEvent#DRAG_DETECTED DRAG_DETECTED} events that should start zoom-in operation.
     * 
     * @param zoomInMouseFilter the filter to accept zoom-in mouse event. If {@code null} then any DRAG_DETECTED
     * event will start zoom-in operation. By default it's set to {@link #DEFAULT_ZOOM_IN_MOUSE_FILTER}.
     * @see #getZoomInMouseFilter()
     */
    public void setZoomInMouseFilter(Predicate<MouseEvent> zoomInMouseFilter) {
        this.zoomInMouseFilter = zoomInMouseFilter;
    }

    /**
     * Returns zoom-out mouse filter.
     * 
     * @return zoom-out mouse filter
     * @see #setZoomOutMouseFilter(Predicate)
     */
    public Predicate<MouseEvent> getZoomOutMouseFilter() {
        return zoomOutMouseFilter;
    }

    /**
     * Sets filter on {@link MouseEvent#MOUSE_CLICKED MOUSE_CLICKED} events that should trigger zoom-out operation.
     * 
     * @param zoomOutMouseFilter the filter to accept zoom-out mouse event. If {@code null} then any MOUSE_CLICKED
     * event will start zoom-out operation. By default it's set to {@link #DEFAULT_ZOOM_OUT_MOUSE_FILTER}.
     * @see #getZoomOutMouseFilter()
     */
    public void setZoomOutMouseFilter(Predicate<MouseEvent> zoomOutMouseFilter) {
        this.zoomOutMouseFilter = zoomOutMouseFilter;
    }

    /**
     * Returns zoom-origin mouse filter.
     * 
     * @return zoom-origin mouse filter
     * @see #setZoomOriginMouseFilter(Predicate)
     */    
    public Predicate<MouseEvent> getZoomOriginMouseFilter() {
        return zoomOriginMouseFilter;
    }

    /**
     * Sets filter on {@link MouseEvent#MOUSE_CLICKED MOUSE_CLICKED} events that should trigger zoom-origin operation.
     * 
     * @param zoomOriginMouseFilter the filter to accept zoom-origin mouse event. If {@code null} then any MOUSE_CLICKED
     * event will start zoom-origin operation. By default it's set to {@link #DEFAULT_ZOOM_ORIGIN_MOUSE_FILTER}.
     * @see #getZoomOriginMouseFilter()
     */    
    public void setZoomOriginMouseFilter(Predicate<MouseEvent> zoomOriginMouseFilter) {
        this.zoomOriginMouseFilter = zoomOriginMouseFilter;
    }

    @Override
    protected void chartDisconnected(Chart oldChart) {
        unbindListeners(oldChart);
    }

    @Override
    protected void chartConnected(Chart newChart) {
        if ((newChart instanceof BarChart)){
            super.chartDisconnected(newChart);
            
        } else {
        if(newChart instanceof XYChart<?, ?>){
            assertValueAxis(((XYChart<?, ?>) newChart).getXAxis(), "X");
            assertValueAxis(((XYChart<?, ?>) newChart).getYAxis(), "Y");
        } else if(newChart instanceof DensityChartFX<?, ?>){
            assertValueAxis(((DensityChartFX<?, ?>) newChart).getXAxis(), "X");
            assertValueAxis(((DensityChartFX<?, ?>) newChart).getYAxis(), "Y");
        }        

        bindListeners(newChart);
    }}

    private void unbindListeners(Chart chart) {
        chart.removeEventHandler(MouseEvent.MOUSE_PRESSED, zoomInStartHandler);
        chart.removeEventHandler(MouseEvent.MOUSE_DRAGGED, zoomInDragHandler);
        chart.removeEventHandler(MouseEvent.MOUSE_RELEASED, zoomInEndHandler);
        chart.removeEventHandler(MouseEvent.MOUSE_CLICKED, zoomOutHandler);
        chart.removeEventHandler(MouseEvent.MOUSE_CLICKED, zoomOriginHandler);
    }

    private void bindListeners(Chart chart) {
        chart.addEventHandler(MouseEvent.MOUSE_PRESSED, zoomInStartHandler);
        chart.addEventHandler(MouseEvent.MOUSE_DRAGGED, zoomInDragHandler);
        chart.addEventHandler(MouseEvent.MOUSE_RELEASED, zoomInEndHandler);
        chart.addEventHandler(MouseEvent.MOUSE_CLICKED, zoomOutHandler);
        chart.addEventHandler(MouseEvent.MOUSE_CLICKED, zoomOriginHandler);
    }

    private final EventHandler<MouseEvent> zoomInStartHandler = (MouseEvent event) -> {
        if (zoomInMouseFilter == null || zoomInMouseFilter.test(event)) {
            zoomInStarted(event);
            event.consume();
        }
    };

    private final EventHandler<MouseEvent> zoomInDragHandler = (MouseEvent event) -> {
        if (zoomOngoing()) {
            zoomInDragged(event);
            event.consume();
        }
    };

    private final EventHandler<MouseEvent> zoomInEndHandler = (MouseEvent event) -> {
        if (zoomOngoing()) {
            zoomInEnded();
            event.consume();
        }
    };

    private boolean zoomOngoing() {
        return zoomStartPoint != null;
    }

    private final EventHandler<MouseEvent> zoomOutHandler = (MouseEvent event) -> {
        if (zoomOutMouseFilter == null || zoomOutMouseFilter.test(event)) {
            zoomOut();
            event.consume();
        }
    };

    private final EventHandler<MouseEvent> zoomOriginHandler = (MouseEvent event) -> {
        if (zoomOriginMouseFilter == null || zoomOriginMouseFilter.test(event)) {
            zoomOrigin();
            event.consume();
        }
    };

    private void zoomInStarted(MouseEvent event) {
        viewReset = true;
        zoomStartPoint = getLocationInPlotArea(event);
        if(getChart() instanceof DensityChartFX<?, ?>){ 
            zoomStartPoint = zoomStartPoint.add(new Point2D(getXValueAxis().getLayoutX(),0.0));
        } 
        zoomRectangle.setX(zoomStartPoint.getX());
        zoomRectangle.setY(zoomStartPoint.getY());
        zoomRectangle.setWidth(0);
        zoomRectangle.setHeight(0);
        zoomRectangle.setVisible(true);
        getChart().getScene().setCursor(getZoomInCursor());
    }

    private void zoomInDragged(MouseEvent event) {
        zoomEndPoint = limitToPlotArea(getLocationInPlotArea(event));
        if(getChart() instanceof DensityChartFX<?, ?>){ 
            zoomEndPoint = zoomEndPoint.add(new Point2D(getXValueAxis().getLayoutX(),0.0));
        } 
        Bounds plotAreaBounds = getPlotAreaBounds();

        double zoomRectX = plotAreaBounds.getMinX();
        double zoomRectY = plotAreaBounds.getMinY();
        double zoomRectWidth = plotAreaBounds.getWidth();
        double zoomRectHeight = plotAreaBounds.getHeight();

        if (getZoomMode() == AxisMode.X || getZoomMode() == AxisMode.XY) {
            zoomRectX = Math.min(zoomStartPoint.getX(), zoomEndPoint.getX());
            zoomRectWidth = Math.abs(zoomEndPoint.getX() - zoomStartPoint.getX());
        }
        if (getZoomMode() == AxisMode.Y || getZoomMode() == AxisMode.XY) {
            zoomRectY = Math.min(zoomStartPoint.getY(), zoomEndPoint.getY());
            zoomRectHeight = Math.abs(zoomEndPoint.getY() - zoomStartPoint.getY());
        }
        zoomRectangle.setX(zoomRectX);
        zoomRectangle.setY(zoomRectY);
        zoomRectangle.setWidth(zoomRectWidth);
        zoomRectangle.setHeight(zoomRectHeight);
    }

    private Point2D limitToPlotArea(Point2D point) {
        Bounds plotBounds = getPlotAreaBounds();
        double limitedX = Math.max(Math.min(point.getX(), plotBounds.getMaxX()), plotBounds.getMinX());
        double limitedY = Math.max(Math.min(point.getY(), plotBounds.getMaxY()), plotBounds.getMinY());
        return new Point2D(limitedX, limitedY);
    }
    
    private void zoomInEnded() {
        zoomRectangle.setVisible(false);
        if (zoomRectangle.getWidth() > ZOOM_RECT_MIN_SIZE && zoomRectangle.getHeight() > ZOOM_RECT_MIN_SIZE) {
            performZoomIn();
        }
        zoomStartPoint = zoomEndPoint = null;
        getChart().getScene().setCursor(Cursor.DEFAULT);
    }

    private void performZoomIn() {
        pushCurrentZoomWindow();
        getXValueAxis().setAutoRanging(false);
        getYValueAxis().setAutoRanging(false);
        performZoom(getZoomDataWindow());
    }

    private void pushCurrentZoomWindow() {
        ValueAxis<?> xAxis = getXValueAxis();
        ValueAxis<?> yAxis = getYValueAxis();

        zoomStack.addFirst(new Rectangle2D(xAxis.getLowerBound(), yAxis.getLowerBound(), xAxis.getUpperBound()
                - xAxis.getLowerBound(), yAxis.getUpperBound() - yAxis.getLowerBound()));
    }

    private Rectangle2D getZoomDataWindow() {
        double zoomMinX = getXValueForDisplayAsDouble(zoomRectangle.getX());
        double zoomMaxX = getXValueForDisplayAsDouble(zoomRectangle.getX() + zoomRectangle.getWidth());
        double zoomMinY = getYValueForDisplayAsDouble(zoomRectangle.getY() + zoomRectangle.getHeight());
        double zoomMaxY = getYValueForDisplayAsDouble(zoomRectangle.getY());

        return new Rectangle2D(zoomMinX, zoomMinY, zoomMaxX - zoomMinX, zoomMaxY - zoomMinY);
    }

    private void performZoom(Rectangle2D zoomWindow) {
        zoomAnimation.stop();
        if (isAnimated()) {
            ValueAxis<?> xAxis = getXValueAxis();
            ValueAxis<?> yAxis = getYValueAxis();
            zoomAnimation.getKeyFrames().setAll(
                    new KeyFrame(Duration.ZERO, 
                            new KeyValue(xAxis.lowerBoundProperty(), xAxis.getLowerBound()),
                            new KeyValue(xAxis.upperBoundProperty(), xAxis.getUpperBound()), 
                            new KeyValue(yAxis.lowerBoundProperty(), yAxis.getLowerBound()), 
                            new KeyValue(yAxis.upperBoundProperty(), yAxis.getUpperBound())),
                    new KeyFrame(getZoomDuration() == null ? DEFAULT_ZOOM_DURATION : getZoomDuration(), 
                            new KeyValue(xAxis.lowerBoundProperty(), zoomWindow.getMinX()),
                            new KeyValue(xAxis.upperBoundProperty(), zoomWindow.getMaxX()), 
                            new KeyValue(yAxis.lowerBoundProperty(), zoomWindow.getMinY()), 
                            new KeyValue(yAxis.upperBoundProperty(), zoomWindow.getMaxY())));
            zoomAnimation.play();
        } else {
            getXValueAxis().setLowerBound(zoomWindow.getMinX());
            getXValueAxis().setUpperBound(zoomWindow.getMaxX());
            getYValueAxis().setLowerBound(zoomWindow.getMinY());
            getYValueAxis().setUpperBound(zoomWindow.getMaxY());
        }
    }

    private void zoomOut() {
        viewReset = true;
        
        Rectangle2D zoomWindow = zoomStack.pollFirst();
        if (zoomWindow == null) {
            return;
        }
        performZoom(zoomWindow);
    }

    private void zoomOrigin() {
        autoScale(getChart());
    }
    
   public void autoScale(Chart newChart){
        this.setChart(newChart);
        
        getXValueAxis().setAutoRanging(true);
        getYValueAxis().setAutoRanging(true);
        
    }
}
