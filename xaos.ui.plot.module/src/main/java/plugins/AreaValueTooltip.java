/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plugins;


import chart.AreaChartFX;
import java.util.LinkedList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.chart.Axis;
import javafx.scene.chart.Chart;

import javafx.scene.chart.XYChart;

/**
 *
 * @author reubenlindroos
 */
public class AreaValueTooltip extends CoordinatesLabel{
 
    public AreaValueTooltip(){
        
    }
        
    /**
     * Called when the plugin is added to a chart.
     * <p>
     * Can be overridden by concrete implementations to register e.g. mouse listeners or perform any other
     * initializations.
     * </p>
     * 
     * @param newChart the chart to which the plugin has been added
     */
    protected void chartConnected(Chart newChart) {
        super.chartConnected(newChart);
    }
    
     protected void chartDisconnected(XYChart<?, ?> oldChart) {
        //
    }
    
   @Override
    protected XYChart.Data<?, ?> getDataPoint(Point2D mouseLocation) {
        
        if (!(getChart() instanceof AreaChartFX)) {
            return null;
        }
        return calculateArea(mouseLocation);
        
    } 
    
    private XYChart.Data<?,?> calculateArea(Point2D mouseLocation){
        AreaChartFX<?,?> chart = (AreaChartFX) getChart();
        XYChart.Data<String,Number> datapoint = new XYChart.Data<String,Number>();
        
        for (Integer seriesIndex = 0; seriesIndex < chart.getData().size();seriesIndex ++) {
        if (chart.getData().get(seriesIndex).getNode().contains(mouseLocation)) {
            datapoint.setXValue(chart.getData().get(seriesIndex).getName());
            datapoint.setYValue(trapezoidalRule(seriesIndex));
            
            
        }
        };
        if (datapoint.getXValue()== null || datapoint.getYValue()==null){
            return toDataPoint(mouseLocation);
        }
        return datapoint;
        
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private double trapezoidalRule(Integer seriesIndex) {
        Double area = 0.0;
        AreaChartFX<?,?> chart = (AreaChartFX) getChart();
        for (Integer index=1; index<chart.getData().get(seriesIndex).getData().size(); index++){
            Double b = Double.parseDouble(chart.getData().get(seriesIndex).getData().get(index).getXValue().toString());
            Double a = Double.parseDouble(chart.getData().get(seriesIndex).getData().get(index-1).getXValue().toString());
            Double fb = Double.parseDouble(chart.getData().get(seriesIndex).getData().get(index).getYValue().toString());
            Double fa = Double.parseDouble(chart.getData().get(seriesIndex).getData().get(index-1).getYValue().toString());
            area+=Math.abs((b-a)/2*(fa+fb));
        }
        
    return area;
    }
    
}
