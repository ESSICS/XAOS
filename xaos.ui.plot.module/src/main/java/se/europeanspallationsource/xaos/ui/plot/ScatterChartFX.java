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
import java.util.List;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ScatterChart;
import org.apache.commons.lang3.Validate;
import se.europeanspallationsource.xaos.core.util.LogUtils;
import se.europeanspallationsource.xaos.ui.plot.Legend.LegendItem;
import se.europeanspallationsource.xaos.ui.plot.plugins.Pluggable;
import se.europeanspallationsource.xaos.ui.plot.util.SeriesColorUtils;

import static java.util.logging.Level.WARNING;


/**
 * A thin extension of the FX {@link ScatterChart} supporting custom
 * {@link Plugin plugin} implementations.
 *
 * @param <X> Type of X values.
 * @param <Y> Type of Y values.
 * @author Grzegorz Kruk (original author).
 * @author claudio.rosati@esss.se
 */
public class ScatterChartFX<X, Y> extends LineChart<X, Y> implements Pluggable {

	private static final Logger LOGGER = Logger.getLogger(ScatterChartFX.class.getName());

	/**
	 * Quick way of creating a scatter chart showing the given {@code data}. X
	 * axis will contain the index in the data point in the given list.
	 *
	 * @param data The data list to be charted.
	 * @return A {@link AreaChartFX} chart.
	 */
	public static ScatterChartFX<Number, Number> of( ObservableList<Double> data ) {

		Series<Number, Number> dataSet = new Series<>();
		ObservableList<Data<Number, Number>> list = dataSet.getData();

		for ( int i = 0; i < data.size(); i++ ) {
			list.add(new Data<>(i, data.get(i)));
		}

		return new ScatterChartFX<>(
			new NumberAxis(),
			new NumberAxis(),
			FXCollections.singletonObservableList(dataSet)
		);

	}

	private final List<String> notShowwInLegend = new ArrayList<>(4);
	private final Group pluginsNodesGroup = new Group();
	private final PluginManager pluginManager = new PluginManager(this, pluginsNodesGroup);
	private final List<String> seriesDrawnInPlot = new ArrayList<>(4);

	/**
	 * Construct a new scatter chart with the given axis.
	 *
	 * @param xAxis The x axis to use.
	 * @param yAxis The y axis to use.
	 * @see javafx.scene.chart.ScatterChart#ScatterChart(Axis, Axis)
	 */
	public ScatterChartFX( Axis<X> xAxis, Axis<Y> yAxis ) {
		this(xAxis, yAxis, FXCollections.<Series<X, Y>>observableArrayList());
	}

	/**
	 * Construct a new scatter chart with the given axis and data.
	 *
	 * @param xAxis The x axis to use.
	 * @param yAxis The y axis to use.
	 * @param data  The data to use, this is the actual list used so any changes
	 *              to it will be reflected in the chart.
	 * @see javafx.scene.chart.ScatterChart#ScatterChart(Axis, Axis, ObservableList)
	 */
	public ScatterChartFX( Axis<X> xAxis, Axis<Y> yAxis, ObservableList<Series<X, Y>> data ) {

		super(xAxis, yAxis, data);

		getStylesheets().add(ScatterChartFX.class.getResource("/styles/chart.css").toExternalForm());
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
		return notShowwInLegend.contains(name);
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
		notShowwInLegend.add(name);
		updateLegend();
	}

	@Override
	protected void layoutPlotChildren() {

		//	Move plugins nodes to front.
		ObservableList<Node> plotChildren = getPlotChildren();

		plotChildren.remove(pluginsNodesGroup);
		plotChildren.add(pluginsNodesGroup);

		//	Layout plot children. This call will create fresh new symbols
		//	that are by default visible.
		super.layoutPlotChildren();

		//	If the track is hidden, then hide the symbols.
		getData().stream()
			.filter(series -> !seriesDrawnInPlot.contains(series.getName()))
			.flatMap(series -> series.getData().stream())
			.forEach(d -> d.getNode().setVisible(false));

//		// update symbol positions
//		List<LineTo> constructedPath = new ArrayList<>(this.getData().size());
//		for ( Iterator<Series<X, Y>> sit = getDisplayedSeriesIterator(); sit.hasNext(); ) {
//			Series<X, Y> series = sit.next();
//			if ( notShowwInLegend.contains(series.getName()) ) {
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
//				series.getNode().setVisible(false);
//				for ( Iterator<Data<X, Y>> it = getDisplayedDataIterator(series); it.hasNext(); ) {
//					Data<X, Y> item = it.next();
//					double x = getXAxis().getDisplayPosition(item.getXValue());
//					double y = getYAxis().getDisplayPosition(item.getYValue());
//					if ( Double.isNaN(x) || Double.isNaN(y) ) {
//						continue;
//					}
//					Node symbol = item.getNode();
//					if ( symbol != null ) {
//						final double w = symbol.prefWidth(-1);
//						final double h = symbol.prefHeight(-1);
//						symbol.resizeRelocate(x - ( w / 2 ), y - ( h / 2 ), w, h);
//					}
//				}
//			}
//		}
//		movePluginsNodesToFront();

	}

	@Override
	protected void updateLegend() {
//        final Legend legend = new Legend();
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
