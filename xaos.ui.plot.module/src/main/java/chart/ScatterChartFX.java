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

import com.sun.javafx.charts.Legend;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

/**
 * A thin extension of the FX {@link ScatterChart} supporting custom {@link XYChartPlugin} plugin implementations.
 * 
 * @author Grzegorz Kruk
 * @param <X> type of X values
 * @param <Y> type of Y values
 */
public class ScatterChartFX<X, Y> extends LineChart<X, Y> {

    //Variables used to include plugIns in teh Chart
    private final Group pluginsNodesGroup = new Group();
    private final PluginManager pluginManager = new PluginManager(this, pluginsNodesGroup);
    
    //Variables that controls the line that doens't show in the Legend or series that are not displayed at the chart at a given time
    private final List<String> noShowInLegend = new ArrayList<>();
    private final List<String> seriesDrawnInPlot = new ArrayList<>();
   
    // Variable that stored the color line setup
    private String colorStyle = new String();

    /**
     * Construct a new ScatterChart with the given axis and data.
     *
     * @param xAxis The x axis to use
     * @param yAxis The y axis to use
     * @see javafx.scene.chart.ScatterChart#ScatterChart(Axis, Axis)
     */
    public ScatterChartFX(Axis<X> xAxis, Axis<Y> yAxis) {
        this(xAxis, yAxis, FXCollections.<Series<X, Y>> observableArrayList());
    }

    /**
     * Construct a new ScatterChart with the given axis and data.
     *
     * @param xAxis The x axis to use
     * @param yAxis The y axis to use
     * @param data The data to use, this is the actual list used so any changes to it will be reflected in the chart
     * @see javafx.scene.chart.ScatterChart#ScatterChart(Axis, Axis, ObservableList)
     */
    public ScatterChartFX(Axis<X> xAxis, Axis<Y> yAxis, ObservableList<Series<X, Y>> data) {
        super(xAxis, yAxis, data);
        getStylesheets().add("/styles/chart.css"); 
        getPlotChildren().add(pluginsNodesGroup);
    }

    /**
     * Returns a list of plugins added to the chart.
     * 
     * @return non-null list of plugins added to the chart
     */
    public final ObservableList<XYChartPlugin> getChartPlugins() {
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
    public void addChartPlugins(ObservableList<XYChartPlugin> plugins){
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
                        
        colorStyle = colorStyle+"\n"+"-color"+index+": -horizontal;";
        this.lookup(".chart").setStyle(colorStyle);
               
    }
    
    public final void setSeriesAsVertical(Integer index){        
                
        colorStyle = colorStyle+"\n"+"-color"+index+": -vertical;";
        this.lookup(".chart").setStyle(colorStyle);  
               
    }
    
    public final void setSeriesAsLongitudinal(Integer index){        
        
        colorStyle = colorStyle+"\n"+"-color"+index+": -longitudinal;";
        this.lookup(".chart").setStyle(colorStyle);  
               
    }
    
    public final void setNoShowInLegend(String name){                
        noShowInLegend.add(name);
        updateLegend();
    }    
    
    public boolean isNoShowInLegend(String name){                
        return noShowInLegend.contains(name);
    }
    
    public boolean isSeriesDrawn(String name){
        return seriesDrawnInPlot.contains(name);
    }        
    
    
    
    @Override
    protected void layoutPlotChildren() {
        // update symbol positions
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
                series.getNode().setVisible(false);
                for (Iterator<Data<X, Y>> it = getDisplayedDataIterator(series); it.hasNext(); ) {
                    Data<X, Y> item = it.next();
                    double x = getXAxis().getDisplayPosition(item.getXValue());
                    double y = getYAxis().getDisplayPosition(item.getYValue());
                    if (Double.isNaN(x) || Double.isNaN(y)) {
                        continue;
                    }
                    Node symbol = item.getNode();
                    if (symbol != null) {
                        final double w = symbol.prefWidth(-1);
                        final double h = symbol.prefHeight(-1);
                        symbol.resizeRelocate(x-(w/2), y-(h/2),w,h);
                    }                    
                }
            }
        }
        movePluginsNodesToFront();
    }        
    
    @Override
    protected void updateLegend()
    {
        final Legend legend = new Legend();     
        legend.getItems().clear();
        for (final Series<X, Y> series : getData())
        {            
            if(!noShowInLegend.contains(series.getName())){
                Legend.LegendItem legenditem = new Legend.LegendItem(series.getName());                
                final CheckBox cb = new CheckBox(series.getName());                
                seriesDrawnInPlot.add(series.getName());
                cb.setUserData(series);
                cb.setSelected(true); 
                //cb.setPadding(new Insets(0,10,0,0));
                cb.setStyle("-fx-text-fill: -color"+this.getData().indexOf(series)+" ;");
                cb.addEventHandler(ActionEvent.ACTION, e ->{
                    final CheckBox box = (CheckBox) e.getSource();
                    @SuppressWarnings("unchecked")
                    final Series<Number, Number> s = (Series<Number, Number>) box.getUserData();
                    s.getData().forEach(data ->{
                        StackPane stackPane = (StackPane) data.getNode();
                        stackPane.setVisible(box.isSelected());
                    }); 
                    if(box.isSelected()){
                        if (!seriesDrawnInPlot.contains(s.getName())){
                            seriesDrawnInPlot.add(s.getName());
                        }
                    } else {
                        seriesDrawnInPlot.remove(s.getName());
                    }
                });
                legenditem.setText("");
                legenditem.setSymbol(cb);
                legend.getItems().add(legenditem);
            }
        }
        setLegend(legend);
    }
    
}
