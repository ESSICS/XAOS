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


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import org.apache.commons.lang3.Validate;
import se.europeanspallationsource.xaos.core.util.LogUtils;
import se.europeanspallationsource.xaos.ui.plot.Legend.LegendItem;
import se.europeanspallationsource.xaos.ui.plot.plugins.Pluggable;
import se.europeanspallationsource.xaos.ui.plot.util.SeriesColorUtils;

import static java.util.logging.Level.WARNING;


/**
 * A specialized {@link LineChart} used to draw histograms.
 *
 * @param <X> Type of X values.
 * @param <Y> Type of Y values.
 * @author claudio.rosati@esss.se
 */
public class HistogramChartFX<X extends Number, Y extends Number> extends LineChart<X, Y> implements Pluggable {

	private static final Logger LOGGER = Logger.getLogger(HistogramChartFX.class.getName());

	/**
	 * Quick way of creating a histogram chart showing the given {@code data}.
	 *
	 * @param data The data list to be charted.
	 * @param bars The desired number of histogram bars.
	 * @return A {@link AreaChartFX} chart.
	 */
	public static HistogramChartFX<Number, Number> of( List<Double> data, int bars ) {
		return new HistogramChartFX<>(
			new NumberAxis(),
			new NumberAxis(),
			FXCollections.singletonObservableList(seriesOf(data, bars))
		);
	}

	/**
	 * Quick way of creating a histogram chart showing the given {@code data}.
	 *
	 * @param data   The data list to be charted.
	 * @param minVal Minimum data value.
	 * @param maxVal Maximum data value.
	 * @param bars   The desired number of histogram bars.
	 * @return A {@link AreaChartFX} chart.
	 */
	public static HistogramChartFX<Number, Number> of( List<Double> data, double minVal, double maxVal, int bars ) {
		return new HistogramChartFX<>(
			new NumberAxis(),
			new NumberAxis(),
			FXCollections.singletonObservableList(seriesOf(data, minVal, maxVal, bars))
		);
	}

	/**
	 * Quick way of creating a histogram chart series from the given
	 * {@code data}.
	 *
	 * @param data   The data list to be charted.
	 * @param bars   The desired number of histogram bars.
	 * @return A {@link AreaChartFX} chart.
	 */
	public static Series<Number, Number> seriesOf( List<Double> data, int bars ) {

		List<Double> sortedData = new ArrayList<>(data);

		Collections.sort(sortedData);

		Series<Number, Number> series = new Series<>();
		double minVal = sortedData.get(0);
		double maxVal = sortedData.get(sortedData.size() - 1);
		double delta = ( maxVal - minVal ) / ( bars - 1 );

		for ( int bar = 0; bar < bars; bar++ ) {

			int count = 0;

			for ( int index = 0; index < sortedData.size(); index++ ) {
				if ( ( sortedData.get(index) <= bar * delta + minVal ) ) {
					count++;
					sortedData.remove(sortedData.get(index));
				}
			}

			series.getData().add(new XYChart.Data<>(delta * bar + minVal, (double) count));

		}

		return series;

	}

	/**
	 * Quick way of creating a histogram chart series from the given
	 * {@code data}.
	 *
	 * @param data   The data list to be charted.
	 * @param minVal Minimum data value.
	 * @param maxVal Maximum data value.
	 * @param bars   The desired number of histogram bars.
	 * @return A {@link AreaChartFX} chart.
	 */
	public static Series<Number, Number> seriesOf( List<Double> data, double minVal, double maxVal, int bars ) {

		List<Double> sortedData = new ArrayList<>(data);

		Collections.sort(sortedData);

		Series<Number, Number> series = new Series<>();
		double delta = ( maxVal - minVal ) / ( bars - 1 );

		for ( int bar = 0; bar < bars; bar++ ) {

			int count = 0;

			for ( int index = 0; index < sortedData.size(); index++ ) {
				if ( ( sortedData.get(index) <= bar * delta + minVal ) ) {
					count++;
					sortedData.remove(sortedData.get(index));
				}
			}

			series.getData().add(new XYChart.Data<>(delta * bar + minVal, (double) count));

		}

		return series;

	}

	private final List<String> notShownInLegend = new ArrayList<>(4);
	private final Group pluginsNodesGroup = new Group();
	private final PluginManager pluginManager = new PluginManager(this, pluginsNodesGroup);
	private final List<String> seriesDrawnInPlot = new ArrayList<>(4);

	/* *********************************************************************** *
	 * START OF JAVAFX PROPERTIES                                              *
	 * *********************************************************************** */

	/*
	 * ---- barGap -------------------------------------------------------------
	 */
	private final DoubleProperty barGap = new SimpleDoubleProperty(this, "barGap", 0) {
		@Override
		protected void invalidated() {
			requestChartLayout();
		}
	};

	/**
	 * @return A property representing the number of pixels between histogram
	 *         bars.
	 */
	public final DoubleProperty barGapProperty() {
		return barGap;
	}

	/**
	 * @return The number of pixels between histogram bars.
	 * @see #barGapProperty()
	 */
	public double getBarGap() {
		return barGapProperty().get();
	}

	/**
	 * @param barGap The number of pixels between histogram bars.
	 * @see #barGapProperty()
	 */
	public void setBarGap( double barGap ) {
		barGapProperty().set(barGap);
	}

	/* *********************************************************************** *
	 * END OF JAVAFX PROPERTIES                                                *
	 * *********************************************************************** */

	/**
	 * Construct a new histogram chart with the given axis.
	 *
	 * @param xAxis The x axis to use.
	 * @param yAxis The y axis to use.
	 * @see javafx.scene.chart.LineChart#LineChart(Axis, Axis)
	 */
	public HistogramChartFX( ValueAxis<X> xAxis, ValueAxis<Y> yAxis ) {
		this(xAxis, yAxis, FXCollections.<Series<X, Y>>observableArrayList());
	}

	/**
	 * Construct a new histogram chart with the given data.
	 *
	 * @param xAxis The X axis.
	 * @param yAxis The Y axis.
	 * @param data  Data to included in the chart
	 */
	public HistogramChartFX( ValueAxis<X> xAxis, ValueAxis<Y> yAxis, ObservableList<Series<X, Y>> data ) {

		super(xAxis, yAxis, data);

		getStylesheets().add(HistogramChartFX.class.getResource("/styles/histogram-chart.css").toExternalForm());
		getPlotChildren().add(pluginsNodesGroup);

	}

	/**
	 * More robust method for adding plugins to chart.
	 * <p>
	 * <b>Note:</b> Only necessary if more than one plugin is being added at
	 * once.</p>
	 *
	 * @param plugins List of {@link Plugin}s to be added.
	 */
	public void addChartPlugins( ObservableList<Plugin> plugins ) {
		plugins.forEach(plugin -> {
			try {
				pluginManager.getPlugins().add(plugin);
			} catch ( Exception ex ) {
				LogUtils.log(
					LOGGER,
					WARNING,
					"Error occured whilst adding {0} [{1}].",
					plugin.getClass().getName(),
					ex.getMessage()
				);
			}
		});
	}

	@Override
	public Chart getChart() {
		return this;
	}

	@Override
	public ObservableList<LegendItem> getLegendItems() {

		Node legend = getLegend();

		if ( legend instanceof Legend ) {
			return ( (Legend) legend ).getItems();
		} else {
			return FXCollections.emptyObservableList();
		}

	}

	@Override
	public final ObservableList<Node> getPlotChildren() {
		return super.getPlotChildren();
	}

	@Override
	public final ObservableList<Plugin> getPlugins() {
		return pluginManager.getPlugins();
	}

	@Override
	public boolean isNotShownInLegend( String name ) {
		return notShownInLegend.contains(name);
	}

	public boolean isSeriesDrawn( String name ) {
		return seriesDrawnInPlot.contains(name);
	}

	/**
	 * Sets which series has to be considered "horizontal", "vertical" and
	 * "longitudinal". Special colors will be used to represent horizontal
	 * (red), vertical (blue) and longitudinal (green) series.
	 *
	 * @param horizontal   Index of the horizontal series. Use -1 if no
	 *                     horizontal series exists.
	 * @param vertical     Index of the vertical series. Use -1 if no vertical
	 *                     series exists.
	 * @param longitudinal Index of the longitudinal series. Use -1 if no
	 *                     longitudinal series exists.
	 */
	public final void setHVLSeries( int horizontal, int vertical, int longitudinal ) {

		int size = getData().size();

		Validate.isTrue(horizontal < size, "Out of range 'horizontal' parameter.");
		Validate.isTrue(vertical < size, "Out of range 'vertical' parameter.");
		Validate.isTrue(longitudinal < size, "Out of range 'longitudinal' parameter.");

		lookup(".chart").setStyle(SeriesColorUtils.styles(horizontal, vertical, longitudinal));

	}

	@Override
	public final void setNotShownInLegend( String name ) {
		notShownInLegend.add(name);
		updateLegend();
	}

	@Override
	protected void layoutPlotChildren() {

		//	Layout plot children. This call will create fresh new symbols
		//	that are by default visible.
//		super.layoutPlotChildren();

		@SuppressWarnings( "unchecked" )
		final double zeroPos = ( ( (ValueAxis) getYAxis() ).getLowerBound() > 0 )
							 ? ( (ValueAxis) getYAxis() ).getDisplayPosition(( (ValueAxis) getYAxis() ).getLowerBound())
							 : ( (ValueAxis) getYAxis() ).getZeroPosition();
		final double offset = getBarGap();




		for ( Iterator<Series<X, Y>> sit = getDisplayedSeriesIterator(); sit.hasNext(); ) {

			Series<X, Y> series = sit.next();
			ObservableList<Data<X, Y>> data = series.getData();
			final double barwidth = ( getXAxis().getDisplayPosition(data.get(1).getXValue()) - getXAxis().getDisplayPosition(data.get(0).getXValue()) ) - offset;

			data.forEach(dataItem -> {
				if ( dataItem != null ) {

					final Node bar = dataItem.getNode();
					final double xPos = getXAxis().getDisplayPosition(dataItem.getXValue());
					final double yPos = getYAxis().getDisplayPosition(dataItem.getYValue());
					final double bottom = Math.min(yPos, zeroPos);
					final double top = Math.max(yPos, zeroPos);

					bar.resizeRelocate(xPos - barwidth / 2, bottom, barwidth, top - bottom);
				}
			});

		}





		//	If the track is hidden, then hide the symbols.
//		getData().stream()
//			.filter(series -> !seriesDrawnInPlot.contains(series.getName()))
//			.flatMap(series -> series.getData().stream())
//			.forEach(d -> d.getNode().setVisible(false));

		//	Move plugins nodes to front.
		ObservableList<Node> plotChildren = getPlotChildren();

		plotChildren.remove(pluginsNodesGroup);
		plotChildren.add(pluginsNodesGroup);





//		final double zeroPos = ( ( (ValueAxis) getYAxis() ).getLowerBound() > 0 )
//							   ? ( (ValueAxis) getYAxis() ).getDisplayPosition(( (ValueAxis) getYAxis() ).getLowerBound()) : ( (ValueAxis) getYAxis() ).getZeroPosition();
//		final double offset = getBarGap();
//		List<LineTo> constructedPath = new ArrayList<>(this.getData().size());
//		for ( Iterator<Series<X, Y>> sit = getDisplayedSeriesIterator(); sit.hasNext(); ) {
//			Series<X, Y> series = sit.next();
//			if ( notShownInLegend.contains(series.getName()) ) {
//				final ObservableList<PathElement> seriesLine = ( (Path) series.getNode() ).getElements();
//				seriesLine.clear();
//				constructedPath.clear();
//				for ( Iterator<Data<X, Y>> it = getDisplayedDataIterator(series); it.hasNext(); ) {
//					Data<X, Y> item = it.next();
//					double x = getXAxis().getDisplayPosition(item.getXValue());
//					double y = getYAxis().getDisplayPosition(item.getYValue());
//					if ( Double.isNaN(x) || Double.isNaN(y) ) {
//						continue;
//					}
//					constructedPath.add(new LineTo(x, y));
//				}
//				if ( !constructedPath.isEmpty() ) {
//					LineTo first = constructedPath.get(0);
//					seriesLine.add(new MoveTo(first.getX(), first.getY()));
//					seriesLine.addAll(constructedPath);
//				}
//			} else {
//				final double barwidth = ( getXAxis().getDisplayPosition(series.getData().get(1).getXValue()) - getXAxis().getDisplayPosition(series.getData().get(0).getXValue()) ) - offset;
//				series.getData().forEach(dataItem -> {
//					if ( dataItem != null ) {
//						final Node bar = dataItem.getNode();
//						final double xPos;
//						final double yPos;
//						xPos = getXAxis().getDisplayPosition(dataItem.getXValue());
//						yPos = getYAxis().getDisplayPosition(dataItem.getYValue());
//						final double bottom = Math.min(yPos, zeroPos);
//						final double top = Math.max(yPos, zeroPos);
//						bar.resizeRelocate(xPos - barwidth / 2, bottom, barwidth, top - bottom);
//					}
//				});
//			}
//		}
//		movePluginsNodesToFront();
	}

	@Override
	protected void updateLegend() {
//        final Legend legend = new Legend();
//        seriesDrawnInPlot.clear();
//        legend.getItems().clear();
//        for (final Series<X, Y> series : getData())
//        {
//            if(!noShowInLegend.contains(series.getName())){
//                Legend.LegendItem legenditem = new Legend.LegendItem(series.getName());
//                final CheckBox cb = new CheckBox(series.getName());
//                seriesDrawnInPlot.add(series.getName());
//                cb.setUserData(series);
//                cb.setSelected(true);
//                //cb.setPadding(new Insets(0,10,0,0));
//                cb.setStyle("-fx-text-fill: -color"+this.getData().indexOf(series)+" ;");
//                cb.addEventHandler(ActionEvent.ACTION, e ->{
//                    final CheckBox box = (CheckBox) e.getSource();
//                    @SuppressWarnings("unchecked")
//                    final Series<Number, Number> s = (Series<Number, Number>) box.getUserData();
//                    s.getNode().setVisible(box.isSelected());
//                    s.getData().forEach(data ->{
//                        StackPane stackPane = (StackPane) data.getNode();
//                        stackPane.setVisible(box.isSelected());
//                    });
//                    if(box.isSelected()){
//                        if (!seriesDrawnInPlot.contains(s.getName())){
//                            seriesDrawnInPlot.add(s.getName());
//                        }
//                    } else {
//                        seriesDrawnInPlot.remove(s.getName());
//                    }
//                });
//                legenditem.setText("");
//                legenditem.setSymbol(cb);
//                legend.getItems().add(legenditem);
//            }
//        }
//        setLegend(legend);
	}

	private void movePluginsNodesToFront() {
		getPlotChildren().remove(pluginsNodesGroup);
		getPlotChildren().add(pluginsNodesGroup);
	}

}
