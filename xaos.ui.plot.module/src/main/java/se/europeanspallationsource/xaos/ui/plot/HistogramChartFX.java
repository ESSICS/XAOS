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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import se.europeanspallationsource.xaos.ui.plot.Legend;
import se.europeanspallationsource.xaos.ui.plot.Legend.LegendItem;
import se.europeanspallationsource.xaos.ui.plot.plugins.Pluggable;


/**
 *
 * @author reubenlindroos
 */
public class HistogramChartFX<X,Y> extends LineChart<X,Y> implements Pluggable {
    //Variables used to include plugIns in teh Chart
    private final Group pluginsNodesGroup = new Group();
    private final PluginManager pluginManager = new PluginManager(this, pluginsNodesGroup);
    
    //Variables that controls the line that doens't show in the Legend or series that are not displayed at the chart at a given time
    private final List<String> noShowInLegend = new ArrayList<>();
    private final List<String> seriesDrawnInPlot = new ArrayList<>();
   
    // Variable that stored the color line setup
    private String colorStyle = new String();

    //Variable for Styling the histogram bar
    private double barGap = 0;

    public double getBarGap() {
        return barGap;
    }

    public void setBarGap(double barGap) {
        this.barGap = barGap;
    }
    
    public HistogramChartFX(Axis<X> xAxis, Axis<Y> yAxis) {
        this(xAxis, yAxis, FXCollections.<Series<X, Y>> observableArrayList());
    }
      
    /**
     * Construct a new HistogramChartFX with the given data. 
     * @param xAxis  The X axis.
     * @param yAxis The Y axis.
     * @param data Data to included in the chart
     * @param nbins desired discretisation for bins. 
     */
    public HistogramChartFX(Axis<X> xAxis, Axis<Y> yAxis, ObservableList<Double> data, Integer nbins) {
        super(xAxis, yAxis);
        Collections.sort(data);
        Series<X,Y> binnedDataSet = new Series<X,Y>();
        
        Double maxVal = Collections.max(data);
        Double minVal = Collections.min(data);
        Double delta = (maxVal-minVal)/(nbins-1);
        for (Integer step = 0; step< nbins; step++){
            Double count = 0.0;
            for (Integer index = 0; index < data.size(); index++) {
                
                if ((data.get(index)<=step*delta+minVal)){
                    count+=1.0;
                    data.remove(data.get(index));                                        
                }
             
            }
            XYChart.Data dataPoint = new XYChart.Data(delta*step+minVal,count);
            binnedDataSet.getData().add(dataPoint);        
            
        }
        this.setLegendVisible(false);
        this.setAnimated(false);
        this.getData().add(binnedDataSet);
        getStylesheets().add("/styles/histogram-chart.css");
        getPlotChildren().add(pluginsNodesGroup);
        
    }
    /**
     * Construct a new HistogramChartFX with the given data. 
     * @param xAxis The X axis.
     * @param yAxis The Y axis.
     * @param data Data to included in the chart 
     */
    public HistogramChartFX(Axis<X> xAxis, Axis<Y> yAxis, ObservableList<Series<X,Y>> data){
       super(xAxis,yAxis,data);
       
    }
    
    public HistogramChartFX(Axis<X> xAxis, Axis<Y> yAxis, ObservableList<Double> data, Double minVal, Double maxVal, Integer nbins){
        super(xAxis, yAxis);
        Collections.sort(data);
        Series<X,Y> binnedDataSet = new Series<X,Y>();
        Double delta = (maxVal-minVal)/nbins;
        for (Integer step = 0; step<= nbins; step++){
            Double count = 0.0;
            for (Integer index = 0; index < data.size(); index++) {
                
                if ((data.get(index)<=step*delta+minVal)){
                    count+=1.0;
                    data.remove(data.get(index));                                               
                }
             
            }
            XYChart.Data dataPoint = new XYChart.Data(delta*step+minVal,count);
            binnedDataSet.getData().add(dataPoint);
            
            
        }
        this.setLegendVisible(false);
        this.setAnimated(false);
        this.getData().add(binnedDataSet);
        this.getXAxis().setTickLabelRotation(0);
        getStylesheets().add("/styles/histogram-chart.css");
        getPlotChildren().add(pluginsNodesGroup);
              
    }
    
	@Override
	public Chart getChart() {
		return this;
	}

    public void addDataList(ObservableList<Double> data, Integer nbins){
        Collections.sort(data);
        Series<X,Y> binnedDataSet = new Series<X,Y>();
        
        Double maxVal = Collections.max(data);
        Double minVal = Collections.min(data);
        Double delta = (maxVal-minVal)/(nbins-1);
        for (Integer step = 0; step< nbins; step++){
            Double count = 0.0;
            for (Integer index = 0; index < data.size(); index++) {
                
                if ((data.get(index)<=step*delta+minVal)){
                    count+=1.0;
                    data.remove(data.get(index));                                        
                }
             
            }
            XYChart.Data dataPoint = new XYChart.Data(delta*step+minVal,count);
            binnedDataSet.getData().add(dataPoint);        
            
        }
        this.getData().add(binnedDataSet);
    }
    
    public void addDataList(ObservableList<Double> data, Double minVal, Double maxVal, Integer nbins){
        Collections.sort(data);
        Series<X,Y> binnedDataSet = new Series<X,Y>();
               
        Double delta = (maxVal-minVal)/(nbins-1);
        for (Integer step = 0; step< nbins; step++){
            Double count = 0.0;
            for (Integer index = 0; index < data.size(); index++) {
                
                if ((data.get(index)<=step*delta+minVal)){
                    count+=1.0;
                    data.remove(data.get(index));                                        
                }
             
            }
            XYChart.Data dataPoint = new XYChart.Data(delta*step+minVal,count);
            binnedDataSet.getData().add(dataPoint);        
            
        }
        this.getData().add(binnedDataSet);
    }
    
	@Override
    public final ObservableList<Plugin> getPlugins() {
        return pluginManager.getPlugins();
    }
   
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
   
    private void movePluginsNodesToFront() {
        getPlotChildren().remove(pluginsNodesGroup);
        getPlotChildren().add(pluginsNodesGroup);
    }   
   
    public final void setSeriesAsHorizontal(Integer index){        
                        
        colorStyle = colorStyle+"\n"+"-color"+index+": -xaos-chart-horizontal;";
        this.lookup(".chart").setStyle(colorStyle);
               
    }
    
    public final void setSeriesAsVertical(Integer index){        
                
        colorStyle = colorStyle+"\n"+"-color"+index+": -xaos-chart-vertical;";
        this.lookup(".chart").setStyle(colorStyle);  
               
    }
    
    public final void setSeriesAsLongitudinal(Integer index){        
        
        colorStyle = colorStyle+"\n"+"-color"+index+": -xaos-chart-longitudinal;";
        this.lookup(".chart").setStyle(colorStyle);  
               
    }
    
	@Override
    public final void setNotShowInLegend(String name){
        noShowInLegend.add(name);
        updateLegend();
    }    
    
	@Override
    public boolean isNotShowInLegend(String name){
        return noShowInLegend.contains(name);
    }
    
	@Override
	public ObservableList<Node> getPlotChildren() {
		return super.getPlotChildren();
	}

    public boolean isSeriesDrawn(String name){
        return seriesDrawnInPlot.contains(name);
    }    

    @Override
    protected void layoutPlotChildren() {       
        final double zeroPos = (((ValueAxis)getYAxis()).getLowerBound() > 0) ?
                ((ValueAxis)getYAxis()).getDisplayPosition(((ValueAxis)getYAxis()).getLowerBound()) : ((ValueAxis)getYAxis()).getZeroPosition();
        final double offset = getBarGap();
        List<LineTo> constructedPath = new ArrayList<>(this.getData().size());       
        for (Iterator<Series<X, Y>> sit = getDisplayedSeriesIterator(); sit.hasNext(); ) {
            Series<X, Y> series = sit.next();
            if(noShowInLegend.contains(series.getName())){
                final ObservableList<PathElement> seriesLine = ((Path)series.getNode()).getElements();
                seriesLine.clear();
                constructedPath.clear();
                for (Iterator<Data<X, Y>> it = getDisplayedDataIterator(series); it.hasNext(); ) {
                    Data<X, Y> item = it.next();
                    double x = getXAxis().getDisplayPosition(item.getXValue());
                    double y = getYAxis().getDisplayPosition(item.getYValue());
                    if (Double.isNaN(x) || Double.isNaN(y)) {
                        continue;
                    }
                    constructedPath.add(new LineTo(x, y));
                }
                if (!constructedPath.isEmpty()) {
                    LineTo first = constructedPath.get(0);
                    seriesLine.add(new MoveTo(first.getX(), first.getY()));
                    seriesLine.addAll(constructedPath);
                }                
            } else {
                final double barwidth = (getXAxis().getDisplayPosition(series.getData().get(1).getXValue())-getXAxis().getDisplayPosition(series.getData().get(0).getXValue()))-offset;            
                series.getData().forEach(dataItem -> {
                    if (dataItem != null) {
                        final Node bar = dataItem.getNode();
                        final double xPos;
                        final double yPos;
                        xPos = getXAxis().getDisplayPosition(dataItem.getXValue());
                        yPos = getYAxis().getDisplayPosition(dataItem.getYValue());
                        final double bottom = Math.min(yPos,zeroPos);
                        final double top = Math.max(yPos,zeroPos);                        
                        bar.resizeRelocate(xPos-barwidth/2,bottom,barwidth,top-bottom);                                                
                    }
                });            
            } 
        }
        movePluginsNodesToFront(); 
    }
    
     @Override
    protected void updateLegend()
    {
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
    
	@Override
	public ObservableList<LegendItem> getLegendItems() {

		Node legend = getLegend();

		if ( legend instanceof Legend ) {
			return ((Legend) legend).getItems();
		} else {
			return FXCollections.emptyObservableList();
		}

	}

}

