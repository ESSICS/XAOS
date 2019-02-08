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
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Axis;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.StackPane;

/**
 * A thin extension of the FX {@link AreaChart} supporting custom {@link XYChartPlugin} plugin implementations.
 * 
 * @author Grzegorz Kruk
 * @param <X> type of X values
 * @param <Y> type of Y values
 */
public class AreaChartFX<X, Y> extends AreaChart<X, Y> {

    //Variables used to include plugIns in teh Chart
    private final Group pluginsNodesGroup = new Group();
    private final PluginManager pluginManager = new PluginManager(this, pluginsNodesGroup);
    
    //Variables that controls the line that doens't show in the Legend or series that are not displayed at the chart at a given time
    private final List<String> noShowInLegend = new ArrayList<>();
    private final List<String> seriesDrawnInPlot = new ArrayList<>();
   
    // Variable that stored the color line setup
    private String colorStyle = new String();

    /**
     * Construct a new Area Chart with the given axis
     * 
     * @param xAxis The x axis to use
     * @param yAxis The y axis to use
     * @see javafx.scene.chart.AreaChart#AreaChart(Axis, Axis)
     */
    public AreaChartFX(Axis<X> xAxis, Axis<Y> yAxis) {
        this(xAxis, yAxis, FXCollections.<Series<X, Y>> observableArrayList());
    }

    /**
     * Construct a new Area Chart with the given axis and data
     *
     * @param xAxis The x axis to use
     * @param yAxis The y axis to use
     * @param data The data to use, this is the actual list used so any changes to it will be reflected in the chart
     * @see javafx.scene.chart.AreaChart#AreaChart(Axis, Axis, ObservableList)
     */
    public AreaChartFX(Axis<X> xAxis, Axis<Y> yAxis, ObservableList<Series<X, Y>> data) {
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

    @Override
    protected void layoutPlotChildren() {
        movePluginsNodesToFront();
        super.layoutPlotChildren();
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
    protected void updateLegend()
    {
        final Legend legend = new Legend();     
        seriesDrawnInPlot.clear();
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
                    s.getNode().setVisible(box.isSelected());
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
