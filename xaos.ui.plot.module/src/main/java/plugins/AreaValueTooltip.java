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

package plugins;


import javafx.geometry.Point2D;
import se.europeanspallationsource.xaos.ui.plot.impl.plugins.CursorDisplay;

/**
 *
 * @author reubenlindroos
 */
public class AreaValueTooltip extends CursorDisplay{ 

	@Override
	protected String textAtPosition( Point2D mouseLocation ) {
		return null;
	}

    public AreaValueTooltip(){
        
    }
        
//    /**
//     * Called when the plugin is added to a chart.
//     * <p>
//     * Can be overridden by concrete implementations to register e.g. mouse listeners or perform any other
//     * initializations.
//     * </p>
//     *
//     * @param newChart the chart to which the plugin has been added
//     */
//    protected void chartConnected(Chart newChart) {
//        super.chartConnected(newChart);
//    }
//
//     protected void chartDisconnected(XYChart<?, ?> oldChart) {
//        //
//    }
//
//   @Override
//    protected XYChart.Data<?, ?> getDataPoint(Point2D mouseLocation) {
//
//        if (!(getChart() instanceof AreaChartFX)) {
//            return null;
//        }
//        return calculateArea(mouseLocation);
//
//    }
//
//    private XYChart.Data<?,?> calculateArea(Point2D mouseLocation){
//        AreaChartFX<?,?> chart = (AreaChartFX) getChart();
//        XYChart.Data<String,Number> datapoint = new XYChart.Data<String,Number>();
//
//        for (Integer seriesIndex = 0; seriesIndex < chart.getData().size();seriesIndex ++) {
//        if (chart.getData().get(seriesIndex).getNode().contains(mouseLocation)) {
//            datapoint.setXValue(chart.getData().get(seriesIndex).getName());
//            datapoint.setYValue(trapezoidalRule(seriesIndex));
//
//
//        }
//        };
//        if (datapoint.getXValue()== null || datapoint.getYValue()==null){
//            return toDataPoint(mouseLocation);
//        }
//        return datapoint;
//
//    }
//    @SuppressWarnings({ "unchecked", "rawtypes" })
//    private double trapezoidalRule(Integer seriesIndex) {
//        Double area = 0.0;
//        AreaChartFX<?,?> chart = (AreaChartFX) getChart();
//        for (Integer index=1; index<chart.getData().get(seriesIndex).getData().size(); index++){
//            Double b = Double.parseDouble(chart.getData().get(seriesIndex).getData().get(index).getXValue().toString());
//            Double a = Double.parseDouble(chart.getData().get(seriesIndex).getData().get(index-1).getXValue().toString());
//            Double fb = Double.parseDouble(chart.getData().get(seriesIndex).getData().get(index).getYValue().toString());
//            Double fa = Double.parseDouble(chart.getData().get(seriesIndex).getData().get(index-1).getYValue().toString());
//            area+=Math.abs((b-a)/2*(fa+fb));
//        }
//
//    return area;
//    }
    
}
