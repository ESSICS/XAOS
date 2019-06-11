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

import chart.AreaChartFX;
import chart.DateAxis;
import chart.DensityChartFX;
import chart.HistogramChartFX;
import chart.LineChartFX;
import chart.LogAxis;
import chart.NumberAxis;
import chart.ScatterChartFX;
import chart.Plugin;
import chart.data.ExpTrendLine;
import chart.data.GaussianTrendLine;
import chart.data.LogTrendLine;
import chart.data.PolyTrendLine;
import chart.data.PowerTrendLine;
import chart.data.TrendLine;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import static javafx.scene.paint.Color.WHITE;

import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import se.europeanspallationsource.xaos.ui.util.ClipboardUtils;
import se.europeanspallationsource.xaos.ui.util.ResourceUtils;

/**
 *
 * @author nataliamilas
 */
public class PropertyMenu extends Plugin{
    private final double toolSize = 50.0;
    private final double insetSize = 10.0;
    private double scaleX = 1.0;
    private double scaleY = 1.0;
    private Rectangle menuRectangle = new Rectangle();
    private static ImageView menuPrint;
    private static ImageView menuSave;
    private static ImageView menuAxis;
    private static ImageView menuFit;
    private static ImageView menuProp;
    private static ImageView[] propertiesTool;
    private List<Label> fittingLabel = new ArrayList<Label>();
    //List of series added by the fitting function. These can then be removed even if 
    //the property menu is closed. 
    private ObservableList interpolatedValsList = FXCollections.observableArrayList();
    
    public PropertyMenu() {                      
        menuRectangle.setWidth(toolSize+insetSize); 
        menuRectangle.setOpacity(0.5);
        menuRectangle.setFill(WHITE);
        menuPrint =  new ImageView(ResourceUtils.getImage("/icons/properties/picture.png"));
        menuSave =  new ImageView(ResourceUtils.getImage("/icons/properties/save.png"));
        menuAxis =  new ImageView(ResourceUtils.getImage("/icons/properties/ruler2.png"));
        menuFit =  new ImageView(ResourceUtils.getImage("/icons/properties/fit.png"));
        menuProp =  new ImageView(ResourceUtils.getImage("/icons/properties/properties.png"));
        propertiesTool = getImages();
    }
	
	@Override
	public String getName() {
		return "Property Menu";
	}
    
    public static ImageView[] getImages() {     
        ImageView[] imageCollection ={menuPrint, menuSave, menuFit, menuAxis, menuProp};
        
        return imageCollection;
    }
    
    @Override
    protected void chartDisconnected(Chart oldChart) {
        oldChart.removeEventHandler(MouseEvent.MOUSE_PRESSED, mousePressHandler); 
        oldChart.addEventHandler(MouseEvent.MOUSE_MOVED, mouseMoveHandler);
    }

    @Override
    protected void chartConnected(Chart newChart) {
        newChart.addEventHandler(MouseEvent.MOUSE_PRESSED, mousePressHandler); 
        newChart.addEventHandler(MouseEvent.MOUSE_MOVED, mouseMoveHandler);           
    }
    
    // Makes the object appear when the key is pressed by adding the object
    // to the plugin list
    
    private final EventHandler<MouseEvent> mousePressHandler = (MouseEvent event) ->{
        Point2D mouseLocation = getLocationInPlotArea(event);
        
        if(getChart() instanceof DensityChartFX<?, ?>){ 
            mouseLocation = mouseLocation.add(new Point2D(getXValueAxis().getLayoutX(),0.0));
        }
       
        if(menuAxis.getBoundsInLocal().contains(mouseLocation)){
            if(getChart() instanceof XYChart<?, ?>){
                openAxisMenu(); 
            } else if(getChart() instanceof DensityChartFX<?, ?>){
                openDensityAxisMenu();
            }
        }
        
        if(menuSave.getBoundsInLocal().contains(mouseLocation)){
            if(getChart() instanceof XYChart<?, ?>){
                saveDataMenu();  
            } else if(getChart() instanceof DensityChartFX<?, ?>){
                saveDensityDataMenu(); 
            }                       
        }
        
        if(menuPrint.getBoundsInLocal().contains(mouseLocation)){
            saveImageMenu();            
        }
        
        if(menuProp.getBoundsInLocal().contains(mouseLocation)){
            if(getChart() instanceof XYChart<?, ?>){
                openPropertiesMenu(); 
            }         
        }
          if(menuFit.getBoundsInLocal().contains(mouseLocation)){
            if(getChart() instanceof XYChart<?, ?>){
                openFitMenu(); 
            }         
        }
          
        for(Label fitLabel: fittingLabel){
            if(event.getButton().equals(MouseButton.PRIMARY)){
                System.out.print(event.getClickCount());
                System.out.print(fitLabel.getBoundsInLocal());
                System.out.print(mouseLocation);
                if((event.getClickCount() == 2) && fitLabel.getBoundsInLocal().contains(mouseLocation)){
					ClipboardUtils.copyToSystemClipboard(fitLabel.getText());
                }
            }
        }
                    
    };
    
    private final EventHandler<MouseEvent> mouseMoveHandler = (MouseEvent event) -> {
        Point2D mouseLocation = getLocationInPlotArea(event); 
        double offsetX;
        double offsetY;
                
        if(getPlotAreaBounds().getWidth()/toolSize < 10){
            scaleX = (getPlotAreaBounds().getWidth()/10)/toolSize;
        } else {
            scaleX = 1;
        };
        
        if (getPlotAreaBounds().getHeight()/toolSize<8){
            scaleY = (getPlotAreaBounds().getHeight()/8)/toolSize;
        } else {
            scaleY = 1;
        };
        double scaleLaw = Math.min(scaleX, scaleY);  
        
        offsetX = -(scaleX*toolSize+insetSize/2);
        offsetY = 0;
        
        if(getChart() instanceof DensityChartFX<?, ?>){ 
            offsetX = -insetSize/2;
            offsetY = Math.abs(((DensityChartFX<?, ?>)getChart()).getYAxis().getLayoutY()-getPlotAreaBounds().getMinY());
            mouseLocation = mouseLocation.add(new Point2D(((DensityChartFX<?, ?>)getChart()).getXAxis().getLayoutX(),0.0));
            menuRectangle.setX(((DensityChartFX<?, ?>)getChart()).getXAxis().getLayoutX()+((DensityChartFX<?, ?>)getChart()).getXAxis().getWidth()-toolSize-insetSize);
            menuRectangle.setY(((DensityChartFX<?, ?>)getChart()).getYAxis().getLayoutY());
        } else {
            menuRectangle.setX(getPlotAreaBounds().getMaxX()-(toolSize+insetSize));
            menuRectangle.setY(getPlotAreaBounds().getMinY());
        }
        
        menuRectangle.setHeight(getPlotAreaBounds().getHeight());                                
        
        if (!menuRectangle.contains(mouseLocation)) {
            getPlotChildren().removeAll(propertiesTool);
            getPlotChildren().remove(menuRectangle);
            return;
        }                                     
                        
        if(menuRectangle.contains(mouseLocation)){
            getPlotChildren().removeAll(propertiesTool);
            getPlotChildren().remove(menuRectangle);
            menuPrint.setScaleX(scaleLaw);
            menuPrint.setScaleY(scaleLaw);
            menuPrint.setX(getPlotAreaBounds().getMaxX()+offsetX);
            menuPrint.setY(getPlotAreaBounds().getMinY()+scaleLaw*insetSize/2+offsetY);    
            menuSave.setScaleX(scaleLaw);
            menuSave.setScaleY(scaleLaw);           
            menuSave.setX(getPlotAreaBounds().getMaxX()+offsetX);
            menuSave.setY(menuPrint.getY()+scaleLaw*(insetSize+toolSize)+offsetY);
            menuAxis.setScaleX(scaleLaw);
            menuAxis.setScaleY(scaleLaw);
            menuAxis.setX(getPlotAreaBounds().getMaxX()+offsetX);
            menuAxis.setY(menuSave.getY()+scaleLaw*(insetSize+toolSize)+offsetY);
            menuFit.setScaleX(scaleLaw);
            menuFit.setScaleY(scaleLaw);
            menuFit.setX(getPlotAreaBounds().getMaxX()+offsetX);
            menuFit.setY(menuAxis.getY()+scaleLaw*(insetSize+toolSize)+offsetY);
            menuProp.setScaleX(scaleLaw);
            menuProp.setScaleY(scaleLaw);            
            menuProp.setX(getPlotAreaBounds().getMaxX()+offsetX);
            menuProp.setY(menuFit.getY()+scaleLaw*(insetSize+toolSize)+offsetY);                     
            getPlotChildren().add(menuRectangle);
            getPlotChildren().addAll(propertiesTool);            
        }
    };
    
    private void openFitMenu(){
        final ComboBox seriesCombo = new ComboBox();        
        final ComboBox fitFunction = new ComboBox();
        ((XYChart<?, ?>)getChart()).getData().forEach(series ->{
            seriesCombo.getItems().add(series.getName());
        });
        fitFunction.getItems().addAll("PolyTrendLine","LogTrendLine","PowerTrendLine","ExpTrendLine","GaussianTrendLine");
        fitFunction.setValue("PolyTrendLine");
        seriesCombo.setValue(((XYChart<?, ?>)getChart()).getData().get(0).getName());
        Label discLabel = new Label("Discretization: ");
        Label polDegLabel = new Label("Poly Degree: ");
        Label xMinLabel = new Label("x min.: ");
        Label xMaxLabel = new Label("x max: ");
        TextField polDegText = new TextField();
        TextField discText = new TextField();
        TextField xMinText = new TextField();
        TextField xMaxText = new TextField();
        Button clearButton = new Button("Clear");
        Button clearAllButton = new Button("Clear All");
        Button applyButton = new Button("Apply");
        
        applyButton.setPrefSize(90, 20);
        clearButton.setPrefSize(90, 20);
        polDegText.setText(String.format("%d", 1));
        discText.setText(String.format("%d", 100));
        xMinText.setText(String.format("%.4f",getXValueAxis().getLowerBound()));
        xMaxText.setText(String.format("%.4f",getXValueAxis().getUpperBound()));
        
        
        fitFunction.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String fitFuncName = (String) fitFunction.getSelectionModel().getSelectedItem();
                if(fitFuncName.equals("PolyTrendLine")){
                    polDegLabel.setText("Poly Degree: ");
                    polDegText.setText(String.format("%d", 1));
                    polDegText.setDisable(false);
                } else if (fitFuncName.equals("LogTrendLine")){
                    polDegText.setDisable(true);
                } else {
                    Series<Number,Number> series = (Series<Number,Number>) ((XYChart<?, ?>)getChart()).getData().get(seriesCombo.getItems().indexOf(seriesCombo.getValue()));
                    series.getData().sort(Comparator.comparingDouble(d -> d.getYValue().doubleValue()));
                    double offset = series.getData().get(0).getYValue().doubleValue();
                    polDegLabel.setText("Offset : ");
                    polDegText.setText(String.format("%.3f", offset));
                    polDegText.setDisable(false);
                }
            }
        });
        
        seriesCombo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String fitFuncName = (String) fitFunction.getSelectionModel().getSelectedItem();
                if(!fitFuncName.equals("PolyTrendLine") && !fitFuncName.equals("LogTrendLine")){                    
                    Series<Number,Number> series = (Series<Number,Number>) ((XYChart<?, ?>)getChart()).getData().get(seriesCombo.getItems().indexOf(seriesCombo.getValue()));
                    series.getData().sort(Comparator.comparingDouble(d -> d.getYValue().doubleValue()));
                    double offset = series.getData().get(0).getYValue().doubleValue();
                    polDegLabel.setText("Offset : ");
                    polDegText.setText(String.format("%.3f", offset));
                    polDegText.setDisable(false);
                }
            }
        });
        
        applyButton.setOnMouseClicked(new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent arg0) {
                Series<Number,Number> series = (Series<Number,Number>) ((XYChart<?, ?>)getChart()).getData().get(seriesCombo.getItems().indexOf(seriesCombo.getValue()));
                List<Double> x = new ArrayList<>();
                List<Double> y = new ArrayList<>();                               
                Integer j = 0;               
                
                series.getData().forEach(data->{
                    if(data.getXValue().doubleValue()>=Double.parseDouble(xMinText.getText()) && data.getXValue().doubleValue()<=Double.parseDouble(xMaxText.getText())){
                        x.add(data.getXValue().doubleValue());
                        if ("PolyTrendLine".equals(fitFunction.getValue()) || "LogTrendLine".equals(fitFunction.getValue())){
                            y.add(data.getYValue().doubleValue());
                        } else {
                            y.add(data.getYValue().doubleValue()-Double.parseDouble(polDegText.getText()));
                        }
                    }
                });
                    
                Double disc = Double.parseDouble(discText.getText());                
                double maxX = x.stream().mapToDouble(v -> v).max().getAsDouble();
                double minX = x.stream().mapToDouble(v -> v).min().getAsDouble();
                double range = maxX - minX;
                XYChart.Series<Number,Number> interpolatedVals = new Series<Number,Number>(); 
                TrendLine t = null;

                if ("PolyTrendLine".equals(fitFunction.getValue())){
                    t = new PolyTrendLine(Integer.parseInt(polDegText.getText()));              
                    t.setValues(y.stream().mapToDouble(d -> d).toArray(), x.stream().mapToDouble(d -> d).toArray());
                    
                    for (int i=0; i < disc;i++){
                        interpolatedVals.getData().add(new Data(minX+range/disc*i,t.predict(minX+range/disc*i)));
                    }
                    interpolatedVals.setName("PolyFit of " + seriesCombo.getValue());                    
                }
                if ("LogTrendLine".equals(fitFunction.getValue())){
                    t = new LogTrendLine();              
                    t.setValues(y.stream().mapToDouble(d -> d).toArray(), x.stream().mapToDouble(d -> d).toArray());
                    
                    for (int i=0; i < disc;i++){
                        interpolatedVals.getData().add(new Data(minX+range/disc*i,t.predict(minX+range/disc*i)));
                    }
                    interpolatedVals.setName("LogFit of " + seriesCombo.getValue());                    
                }

                if ("PowerTrendLine".equals(fitFunction.getValue())){
                    t = new PowerTrendLine(Double.parseDouble(polDegText.getText()));              
                    t.setValues(y.stream().mapToDouble(d -> d).toArray(), x.stream().mapToDouble(d -> d).toArray());
                    
                    for (int i=0; i < disc;i++){
                        interpolatedVals.getData().add(new Data(minX+range/disc*i,t.predict(minX+range/disc*i)+t.getOffset()));
                    }
                    interpolatedVals.setName("PowerFit of " + seriesCombo.getValue());                  
                }

                if ("ExpTrendLine".equals(fitFunction.getValue())){
                    t = new ExpTrendLine(Double.parseDouble(polDegText.getText()));              
                    t.setValues(y.stream().mapToDouble(d -> d).toArray(), x.stream().mapToDouble(d -> d).toArray());
                    
                    for (int i=0; i < disc;i++){
                        interpolatedVals.getData().add(new Data(minX+range/disc*i,t.predict(minX+range/disc*i)+t.getOffset()));
                    }
                    interpolatedVals.setName("ExpFit of " + seriesCombo.getValue());                   
                }  
                
                if ("GaussianTrendLine".equals(fitFunction.getValue())){
                    t = new GaussianTrendLine();
                    t.setValues(y.stream().mapToDouble(d -> d).toArray(), x.stream().mapToDouble(d -> d).toArray());                                        
                    for (int i=0; i < disc;i++){
                        interpolatedVals.getData().add(new Data(minX+range/disc*i,t.predict(minX+range/disc*i)+Double.parseDouble(polDegText.getText())));
                    }
                    interpolatedVals.setName("GaussFit of " + seriesCombo.getValue());                    
                }
                    
                ((XYChart<Number, Number>)getChart()).getData().add(interpolatedVals);                    
                if(getChart() instanceof LineChartFX<?, ?>){                    
                    ((LineChartFX<Number, Number>)getChart()).setNoShowInLegend(interpolatedVals.getName());
                } else if (getChart() instanceof HistogramChartFX<?, ?> ){
                    ((HistogramChartFX<Number, Number>)getChart()).setNoShowInLegend(interpolatedVals.getName());
                } else if (getChart() instanceof ScatterChartFX<?, ?> ){
                    ((ScatterChartFX<Number, Number>)getChart()).setNoShowInLegend(interpolatedVals.getName());
                } else if (getChart() instanceof AreaChartFX<?, ?> ){
                    ((AreaChartFX<Number, Number>)getChart()).setNoShowInLegend(interpolatedVals.getName());
                }
                interpolatedVals.getData().forEach(data ->{
                    StackPane stackPane = (StackPane) data.getNode();
                    stackPane.setVisible(false);
                });                   
                interpolatedVals.getNode().setStyle("-fx-stroke: black ; -fx-stroke-dash-array: 2 12 12 2 ; -fx-stroke-width: 2;");
                interpolatedValsList.add(interpolatedVals);
                
                getPlotChildren().removeAll(fittingLabel);
                fittingLabel.add(new Label());
                Integer index = fittingLabel.size()-1;
                fittingLabel.get(index).getStyleClass().add("chart-fitting-label");
                fittingLabel.get(index).setStyle("-fx-border-color: -color"+((XYChart<?, ?>)getChart()).getData().indexOf(series)+";");
                fittingLabel.get(index).setManaged(false);                
                if(t instanceof TrendLine){
                    fittingLabel.get(index).setText(t.getResultText(interpolatedVals.getName()));
                }
                fittingLabel.get(index).setPadding(new Insets(2, 2, 2, 2));   
                double labelWidth = 7.0*(fittingLabel.get(index).getText().split("\n")[1]).length();
                fittingLabel.get(index).resizeRelocate(getChart().getLayoutX()+10, getChart().getLayoutY()+(fittingLabel.size()-1)*60+10,labelWidth,50);                 
                //fittingLabel.get(index).setOnMouseClicked(new EventHandler<MouseEvent>() {
                //        @Override
                //        public void handle(MouseEvent mouseEvent) {
                //            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                //                if(mouseEvent.getClickCount() == 2){
                //                    fittingLabel.forEach(label ->{
                //                        if(label.getBoundsInLocal().contains(getLocationInPlotArea(mouseEvent))){ 
                //                               Utils.copyToClipboardText(label.getText()); 
                //                        }
                //                    });
                //                }
                //            }
                //        }                    
                //});
                getPlotChildren().addAll(fittingLabel);               
            }; 
        });
        
        clearButton.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
               ((XYChart<Number, Number>)getChart()).getData().remove(interpolatedValsList.get(interpolatedValsList.size()-1));
               interpolatedValsList.remove(interpolatedValsList.size()-1);               
               getPlotChildren().removeAll(fittingLabel); 
               fittingLabel.remove(fittingLabel.size()-1);
               getPlotChildren().addAll(fittingLabel);
			   ClipboardUtils.clearSystemClipboard();
            }
        });
        
        clearAllButton.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
               ((XYChart<Number, Number>)getChart()).getData().removeAll(interpolatedValsList);
               getPlotChildren().removeAll(fittingLabel); 
               fittingLabel.clear();
			   ClipboardUtils.clearSystemClipboard();
            }
        });
                
        GridPane chartInfo = new GridPane();
        chartInfo.setHgap(10);
        chartInfo.setVgap(10);
        chartInfo.setPadding(new Insets(10, 10, 5, 10)); 
        chartInfo.add(seriesCombo, 0, 0, 2, 1);
        chartInfo.add(fitFunction, 0, 1, 2, 1);
        chartInfo.add(polDegLabel, 0, 3);
        chartInfo.add(polDegText, 1, 3);
        chartInfo.add(discLabel, 0, 4);
        chartInfo.add(discText, 1, 4);
        chartInfo.add(xMinLabel, 0, 5);
        chartInfo.add(xMinText, 1, 5);
        chartInfo.add(xMaxLabel, 0, 6);
        chartInfo.add(xMaxText, 1, 6);
        
     
        HBox hbox = new HBox(8);                
        hbox.setPadding(new Insets(5, 10, 10, 5));
        
        hbox.getChildren().addAll(applyButton,clearButton,clearAllButton); 
        

        BorderPane secondaryLayout = new BorderPane();
        secondaryLayout.setCenter(chartInfo); 
        secondaryLayout.setBottom(hbox);

        Scene secondScene = new Scene(secondaryLayout, 285, 355);                                

        
        Stage newWindow = new Stage();
        newWindow.setTitle("Fitting Menu");
        newWindow.setScene(secondScene);

        newWindow.show();            
    }
    
    private void openAxisMenu(){
        Label lowerboundXLabel = new Label("X min ");
        Label upperboundXLabel = new Label("X max ");
        Label lowerboundYLabel = new Label("Y min ");
        Label upperboundYLabel = new Label("Y max ");       
        Label axisXTitle = new Label(getXValueAxis().getLabel());
        Label axisYTitle = new Label(getYValueAxis().getLabel());
        axisXTitle.setText("X - Axis");
        axisYTitle.setText("Left Y - Axis");    
        CheckBox autorangeXBox = new CheckBox();
        CheckBox autorangeYBox = new CheckBox();
        CheckBox gridlineXBox = new CheckBox();
        CheckBox gridlineYBox = new CheckBox();
        TextField lowerboundXText = new TextField();
        TextField upperboundXText = new TextField();
        TextField lowerboundYText = new TextField();
        TextField upperboundYText = new TextField();
        Button setButton = new Button("Apply");
        setButton.setPrefSize(90, 20);
    
        axisXTitle.setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD,Font.getDefault().getSize()));
        axisYTitle.setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD,Font.getDefault().getSize()));                
        
        autorangeXBox.setText("Automatic X scale");
        autorangeXBox.setSelected(((XYChart<?, ?>)getChart()).getXAxis().isAutoRanging());
        autorangeYBox.setText("Automatic Y scale");
        autorangeYBox.setSelected(((XYChart<?, ?>)getChart()).getYAxis().isAutoRanging());
                
        gridlineXBox.setText("Show X gridlines");
        gridlineYBox.setText("Show Y gridlines");                
        gridlineYBox.setSelected(((XYChart<?, ?>)getChart()).horizontalGridLinesVisibleProperty().get());
        gridlineXBox.setSelected(((XYChart<?, ?>)getChart()).verticalGridLinesVisibleProperty().get());  
        
        lowerboundXText.disableProperty().bind(autorangeXBox.selectedProperty());                
        upperboundXText.disableProperty().bind(autorangeXBox.selectedProperty());                
        lowerboundYText.disableProperty().bind(autorangeYBox.selectedProperty());                
        upperboundYText.disableProperty().bind(autorangeYBox.selectedProperty());                

        lowerboundXText.setText(String.format("%.4f",getXValueAxis().getLowerBound()));
        lowerboundXText.setPrefSize(90, 20);
        upperboundXText.setText(String.format("%.4f",getXValueAxis().getUpperBound()));
        upperboundXText.setPrefSize(90, 20);
        
        lowerboundYText.setText(String.format("%.4f",getYValueAxis().getLowerBound()));
        lowerboundYText.setPrefSize(90, 20);
        upperboundYText.setText(String.format("%.4f",getYValueAxis().getUpperBound()));
        upperboundYText.setPrefSize(90, 20);

        setButton.setOnMouseClicked(new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent arg0) {
                
                // X Axis
                if(autorangeXBox.isSelected()){
                    ((XYChart<?, ?>)getChart()).getXAxis().setAutoRanging(true);                                             
                } else {
                    ((XYChart<?, ?>)getChart()).getXAxis().setAutoRanging(false);  
                    if(((XYChart<?, ?>)getChart()).getXAxis() instanceof NumberAxis){
                        if (Double.parseDouble(lowerboundXText.getText())<Double.parseDouble(upperboundXText.getText())){
                           getXValueAxis().setLowerBound(Double.parseDouble(lowerboundXText.getText()));
                        } else {
                           lowerboundXText.setText(String.format("%.4f",getXValueAxis().getLowerBound()));
                        }
                        if (getXValueAxis().getLowerBound()<Double.parseDouble(upperboundXText.getText())){
                           getXValueAxis().setUpperBound(Double.parseDouble(upperboundXText.getText()));
                        } else {
                           upperboundXText.setText(String.format("%.4f",getXValueAxis().getUpperBound()));
                        } 
                    } else if (((XYChart<?, ?>)getChart()).getXAxis() instanceof LogAxis){
                        if (Double.parseDouble(lowerboundXText.getText())<Double.parseDouble(upperboundXText.getText()) && Double.parseDouble(lowerboundXText.getText())>0){
                           getXValueAxis().setLowerBound(Double.parseDouble(lowerboundXText.getText()));
                        } else {
                           lowerboundXText.setText(String.format("%.4f",getXValueAxis().getLowerBound()));
                        }
                        if (getXValueAxis().getLowerBound()<Double.parseDouble(upperboundXText.getText()) && Double.parseDouble(upperboundXText.getText())>0){
                           getXValueAxis().setUpperBound(Double.parseDouble(upperboundXText.getText()));
                        } else {
                           upperboundXText.setText(String.format("%.4f",getXValueAxis().getUpperBound()));
                        } 
                    } else if (((XYChart<?, ?>)getChart()).getXAxis() instanceof DateAxis){
                        
                    } else if (((XYChart<?, ?>)getChart()).getXAxis() instanceof CategoryAxis){
                        
                    }
                }
                
                // Y Axis
                if(autorangeYBox.isSelected()){
                    ((XYChart<?, ?>)getChart()).getYAxis().setAutoRanging(true);                                             
                } else {
                    ((XYChart<?, ?>)getChart()).getYAxis().setAutoRanging(false);     
                    if(((XYChart<?, ?>)getChart()).getYAxis() instanceof NumberAxis){
                        if (Double.parseDouble(lowerboundYText.getText())<Double.parseDouble(upperboundYText.getText())){
                           getYValueAxis().setLowerBound(Double.parseDouble(lowerboundYText.getText()));
                        } else {
                           lowerboundYText.setText(String.format("%.4f",getYValueAxis().getLowerBound()));
                        }
                        if (getYValueAxis().getLowerBound()<Double.parseDouble(upperboundYText.getText())){
                           getYValueAxis().setUpperBound(Double.parseDouble(upperboundYText.getText()));
                        } else {
                           upperboundYText.setText(String.format("%.4f",getYValueAxis().getUpperBound()));
                        }
                    } else if (((XYChart<?, ?>)getChart()).getYAxis() instanceof LogAxis){
                        if (Double.parseDouble(lowerboundYText.getText())<Double.parseDouble(upperboundYText.getText()) && Double.parseDouble(lowerboundYText.getText())>0){
                           getYValueAxis().setLowerBound(Double.parseDouble(lowerboundYText.getText()));
                        } else {
                           lowerboundYText.setText(String.format("%.4f",getYValueAxis().getLowerBound()));
                        }
                        if (getYValueAxis().getLowerBound()<Double.parseDouble(upperboundYText.getText()) && Double.parseDouble(upperboundYText.getText())>0){
                           getYValueAxis().setUpperBound(Double.parseDouble(upperboundYText.getText()));
                        } else {
                           upperboundYText.setText(String.format("%.4f",getYValueAxis().getUpperBound()));
                        }
                    } else if (((XYChart<?, ?>)getChart()).getXAxis() instanceof DateAxis){
                        
                    } else if (((XYChart<?, ?>)getChart()).getXAxis() instanceof CategoryAxis){
                        
                    }
                }
                
                //Set gridlines visibility
                if(gridlineYBox.isSelected()){
                    ((XYChart<?, ?>)getChart()).horizontalGridLinesVisibleProperty().setValue(true);
                } else {
                    ((XYChart<?, ?>)getChart()).horizontalGridLinesVisibleProperty().setValue(false);
                }
                if(gridlineXBox.isSelected()){
                    ((XYChart<?, ?>)getChart()).verticalGridLinesVisibleProperty().setValue(true);
                } else {
                    ((XYChart<?, ?>)getChart()).verticalGridLinesVisibleProperty().setValue(false);
                }
            }

        });

        GridPane chartInfo = new GridPane();
        chartInfo.setHgap(10);
        chartInfo.setVgap(10);
        chartInfo.setPadding(new Insets(10, 10, 5, 10));

        chartInfo.add(axisXTitle,1,0);
        chartInfo.add(lowerboundXLabel,0,1);
        chartInfo.add(lowerboundXText ,1,1);
        chartInfo.add(upperboundXLabel,0,2);
        chartInfo.add(upperboundXText ,1,2);
        chartInfo.add(gridlineXBox,0,3,2,1);
        chartInfo.add(autorangeXBox,0,4,2,1);        
        chartInfo.add(axisYTitle,1,5);
        chartInfo.add(lowerboundYLabel,0,6);
        chartInfo.add(lowerboundYText ,1,6);
        chartInfo.add(upperboundYLabel,0,7);
        chartInfo.add(upperboundYText ,1,7);
        chartInfo.add(gridlineYBox,0,8,2,1);
        chartInfo.add(autorangeYBox,0,9,2,1);        

        HBox hbox = new HBox();                
        hbox.setPadding(new Insets(5, 10, 10, 5));
        hbox.getChildren().addAll(setButton);                

        BorderPane secondaryLayout = new BorderPane();
        secondaryLayout.setCenter(chartInfo); 
        secondaryLayout.setBottom(hbox);                

        Scene secondScene = new Scene(secondaryLayout, 205, 400);                                

        // New window (Stage)
        Stage newWindow = new Stage();
        newWindow.setTitle("Axis Properties");
        newWindow.setScene(secondScene);

        newWindow.show();            
    }
    
    private void openDensityAxisMenu(){
        Label lowerboundXLabel = new Label("X min ");
        Label upperboundXLabel = new Label("X max ");
        Label lowerboundYLabel = new Label("Y min ");
        Label upperboundYLabel = new Label("Y max ");       
        Label axisXTitle = new Label(getXValueAxis().getLabel());
        Label axisYTitle = new Label(getYValueAxis().getLabel());
        axisXTitle.setText("X - Axis");
        axisYTitle.setText("Left Y - Axis");    
        CheckBox autorangeXBox = new CheckBox();
        CheckBox autorangeYBox = new CheckBox();
        CheckBox gridlineXBox = new CheckBox();
        CheckBox gridlineYBox = new CheckBox();
        CheckBox projectionBox = new CheckBox();
        CheckBox logZAxisBox = new CheckBox();
        TextField lowerboundXText = new TextField();
        TextField upperboundXText = new TextField();
        TextField lowerboundYText = new TextField();
        TextField upperboundYText = new TextField();
        Button setButton = new Button("Apply");
        setButton.setPrefSize(90, 20);
    
        axisXTitle.setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD,Font.getDefault().getSize()));
        axisYTitle.setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD,Font.getDefault().getSize()));                
        
        autorangeXBox.setText("Automatic X scale");
        autorangeXBox.setSelected(((DensityChartFX<?, ?>)getChart()).getXAxis().isAutoRanging());
        autorangeYBox.setText("Automatic Y scale");
        autorangeYBox.setSelected(((DensityChartFX<?, ?>)getChart()).getYAxis().isAutoRanging());
                
        gridlineXBox.setText("Show X gridlines");
        gridlineYBox.setText("Show Y gridlines");                
        gridlineYBox.setSelected(((DensityChartFX<?, ?>)getChart()).horizontalGridLinesVisibleProperty().get());
        gridlineXBox.setSelected(((DensityChartFX<?, ?>)getChart()).verticalGridLinesVisibleProperty().get());  
        
        projectionBox.setText("Show projections");
        projectionBox.setSelected(((DensityChartFX<?, ?>)getChart()).isProjectionLinesVisible());
        
        logZAxisBox.setText("Set Log Z Scale");
        logZAxisBox.setSelected(((DensityChartFX<?, ?>)getChart()).isLogZAxis());
        
        lowerboundXText.disableProperty().bind(autorangeXBox.selectedProperty());                
        upperboundXText.disableProperty().bind(autorangeXBox.selectedProperty());                
        lowerboundYText.disableProperty().bind(autorangeYBox.selectedProperty());                
        upperboundYText.disableProperty().bind(autorangeYBox.selectedProperty());                

        lowerboundXText.setText(String.format("%.4f",getXValueAxis().getLowerBound()));
        lowerboundXText.setPrefSize(90, 20);
        upperboundXText.setText(String.format("%.4f",getXValueAxis().getUpperBound()));
        upperboundXText.setPrefSize(90, 20);
        
        lowerboundYText.setText(String.format("%.4f",getYValueAxis().getLowerBound()));
        lowerboundYText.setPrefSize(90, 20);
        upperboundYText.setText(String.format("%.4f",getYValueAxis().getUpperBound()));
        upperboundYText.setPrefSize(90, 20);

        setButton.setOnMouseClicked(new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent arg0) {
                
                // X Axis
                if(autorangeXBox.isSelected()){
                    ((DensityChartFX<?, ?>)getChart()).getXAxis().setAutoRanging(true);                                             
                } else {
                    ((DensityChartFX<?, ?>)getChart()).getXAxis().setAutoRanging(false);  
                    if(((DensityChartFX<?, ?>)getChart()).getXAxis() instanceof NumberAxis){
                        if (Double.parseDouble(lowerboundXText.getText())<Double.parseDouble(upperboundXText.getText())){
                           getXValueAxis().setLowerBound(Double.parseDouble(lowerboundXText.getText()));
                        } else {
                           lowerboundXText.setText(String.format("%.4f",getXValueAxis().getLowerBound()));
                        }
                        if (getXValueAxis().getLowerBound()<Double.parseDouble(upperboundXText.getText())){
                           getXValueAxis().setUpperBound(Double.parseDouble(upperboundXText.getText()));
                        } else {
                           upperboundXText.setText(String.format("%.4f",getXValueAxis().getUpperBound()));
                        } 
                    } else if (((DensityChartFX<?, ?>)getChart()).getXAxis() instanceof LogAxis){
                        if (Double.parseDouble(lowerboundXText.getText())<Double.parseDouble(upperboundXText.getText()) && Double.parseDouble(lowerboundXText.getText())>0){
                           getXValueAxis().setLowerBound(Double.parseDouble(lowerboundXText.getText()));
                        } else {
                           lowerboundXText.setText(String.format("%.4f",getXValueAxis().getLowerBound()));
                        }
                        if (getXValueAxis().getLowerBound()<Double.parseDouble(upperboundXText.getText()) && Double.parseDouble(upperboundXText.getText())>0){
                           getXValueAxis().setUpperBound(Double.parseDouble(upperboundXText.getText()));
                        } else {
                           upperboundXText.setText(String.format("%.4f",getXValueAxis().getUpperBound()));
                        } 
                    } 
                }
                
                // Y Axis
                if(autorangeYBox.isSelected()){
                    ((DensityChartFX<?, ?>)getChart()).getYAxis().setAutoRanging(true);                                             
                } else {
                    ((DensityChartFX<?, ?>)getChart()).getYAxis().setAutoRanging(false);     
                    if(((DensityChartFX<?, ?>)getChart()).getYAxis() instanceof NumberAxis){
                        if (Double.parseDouble(lowerboundYText.getText())<Double.parseDouble(upperboundYText.getText())){
                           getYValueAxis().setLowerBound(Double.parseDouble(lowerboundYText.getText()));
                        } else {
                           lowerboundYText.setText(String.format("%.4f",getYValueAxis().getLowerBound()));
                        }
                        if (getYValueAxis().getLowerBound()<Double.parseDouble(upperboundYText.getText())){
                           getYValueAxis().setUpperBound(Double.parseDouble(upperboundYText.getText()));
                        } else {
                           upperboundYText.setText(String.format("%.4f",getYValueAxis().getUpperBound()));
                        }
                    } else if (((DensityChartFX<?, ?>)getChart()).getYAxis() instanceof LogAxis){
                        if (Double.parseDouble(lowerboundYText.getText())<Double.parseDouble(upperboundYText.getText()) && Double.parseDouble(lowerboundYText.getText())>0){
                           getYValueAxis().setLowerBound(Double.parseDouble(lowerboundYText.getText()));
                        } else {
                           lowerboundYText.setText(String.format("%.4f",getYValueAxis().getLowerBound()));
                        }
                        if (getYValueAxis().getLowerBound()<Double.parseDouble(upperboundYText.getText()) && Double.parseDouble(upperboundYText.getText())>0){
                           getYValueAxis().setUpperBound(Double.parseDouble(upperboundYText.getText()));
                        } else {
                           upperboundYText.setText(String.format("%.4f",getYValueAxis().getUpperBound()));
                        }
                    } 
                }
                
                //Set gridlines and projection visibility                
                ((DensityChartFX<?, ?>)getChart()).horizontalGridLinesVisibleProperty().setValue(gridlineYBox.isSelected());
                ((DensityChartFX<?, ?>)getChart()).verticalGridLinesVisibleProperty().setValue(gridlineXBox.isSelected());
                ((DensityChartFX<?, ?>)getChart()).setProjectionLinesVisible(projectionBox.isSelected());
                ((DensityChartFX<?, ?>)getChart()).setLogZAxis(logZAxisBox.isSelected());
                
            }

        });

        GridPane chartInfo = new GridPane();
        chartInfo.setHgap(10);
        chartInfo.setVgap(10);
        chartInfo.setPadding(new Insets(10, 10, 5, 10));

        chartInfo.add(axisXTitle,1,0);
        chartInfo.add(lowerboundXLabel,0,1);
        chartInfo.add(lowerboundXText ,1,1);
        chartInfo.add(upperboundXLabel,0,2);
        chartInfo.add(upperboundXText ,1,2);
        chartInfo.add(gridlineXBox,0,3,2,1);
        chartInfo.add(autorangeXBox,0,4,2,1);        
        chartInfo.add(axisYTitle,1,5);
        chartInfo.add(lowerboundYLabel,0,6);
        chartInfo.add(lowerboundYText ,1,6);
        chartInfo.add(upperboundYLabel,0,7);
        chartInfo.add(upperboundYText ,1,7);
        chartInfo.add(gridlineYBox,0,8,2,1);
        chartInfo.add(autorangeYBox,0,9,2,1);        
        chartInfo.add(projectionBox,0,10,2,1);
        //chartInfo.add(logZAxisBox,0,11,2,1);        

        HBox hbox = new HBox();                
        hbox.setPadding(new Insets(5, 10, 10, 5));
        hbox.getChildren().addAll(setButton);                

        BorderPane secondaryLayout = new BorderPane();
        secondaryLayout.setCenter(chartInfo); 
        secondaryLayout.setBottom(hbox);                

        Scene secondScene = new Scene(secondaryLayout, 205, 450);                                

        // New window (Stage)
        Stage newWindow = new Stage();
        newWindow.setTitle("Axis Properties");
        newWindow.setScene(secondScene);

        newWindow.show();            
    }
    
    private void saveDataMenu(){
        
        FileChooser fileChooser = new FileChooser();
                 
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = 
                new FileChooser.ExtensionFilter("csv files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(null);
        
        if(file != null){
            ((XYChart<?, ?>)getChart()).getData().forEach(series -> {
                FileWriter fw;
                BufferedWriter bw;                       
                try {
                    fw = new FileWriter(new File(file.getPath()+"_"+series.getName()+".csv"), true);
                    bw = new BufferedWriter(fw);
                    series.getData().forEach(data ->{
                        try {                      
                            bw.write(data.getXValue().toString()+","+data.getYValue().toString()+"\n");
                        } catch (IOException ex) {
                            Logger.getLogger(PropertyMenu.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }); 
                    bw.close();   
                } catch (IOException ex) {
                    Logger.getLogger(PropertyMenu.class.getName()).log(Level.SEVERE, null, ex);
                }                                                                       
            });
        }
        
    }
    
    private void saveDensityDataMenu(){
        
        FileChooser fileChooser = new FileChooser();
                 
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = 
                new FileChooser.ExtensionFilter("csv files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(null);
        
        if(file != null){
            BufferedWriter bw;                       
                try {
                    bw = new BufferedWriter(new FileWriter(file));
                    for (int yIndex = 0; yIndex < ((DensityChartFX<?, ?>)getChart()).getData().getYSize(); yIndex++) {
                        for (int xIndex = 0; xIndex < ((DensityChartFX<?, ?>)getChart()).getData().getXSize(); xIndex++) {   
                            try {                      
                                bw.write(((DensityChartFX<?, ?>)getChart()).getData().getXValue(xIndex)+","+((DensityChartFX<?, ?>)getChart()).getData().getYValue(xIndex)+","+
                                        ((DensityChartFX<?, ?>)getChart()).getData().getZValue(xIndex, yIndex)+"\n");
                            } catch (IOException ex) {
                                Logger.getLogger(PropertyMenu.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }   
                    }       
                    bw.close();   
                } catch (IOException ex) {
                    Logger.getLogger(PropertyMenu.class.getName()).log(Level.SEVERE, null, ex);
                }                                                                                                                                                                                
        }
        
    }
    
    private void saveImageMenu(){
        
        FileChooser fileChooser = new FileChooser();
                 
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = 
                new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(null);
        
        if(file != null){
            WritableImage image = getChart().snapshot(new SnapshotParameters(), null);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null),"png",file);
            } catch (IOException ex) {
                Logger.getLogger(PropertyMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private void openPropertiesMenu(){              
        final ComboBox seriesCombo = new ComboBox();        
        
        ((XYChart<?, ?>)getChart()).getData().forEach(series ->{
            seriesCombo.getItems().add(series.getName());
        });
        seriesCombo.setValue(((XYChart<?, ?>)getChart()).getData().get(0).getName());
        
        Series<Number,Number> series = (Series<Number,Number>) ((XYChart<?, ?>)getChart()).getData().get(0);
        double meanX = 0.0;
        double meanY= 0.0;
        double rmsX= 0.0;
        double rmsY= 0.0;
        
        //calculate mean values
        for(Data<Number,Number> data : series.getData()){
            if ((data.getXValue() instanceof Number)){
                meanX = meanX + data.getXValue().doubleValue();
            }
            if ((data.getYValue() instanceof Number)){
                meanY = meanY + data.getYValue().doubleValue();;
                }
        }
        meanX = meanX/series.getData().size();
        meanY = meanY/series.getData().size();

        //calculate rms values
        for(Data<Number,Number> data : series.getData()){
             if ((data.getXValue() instanceof Number)){
            rmsX = rmsX + (data.getXValue().doubleValue()- meanX)*(data.getXValue().doubleValue()- meanX);
             }
             if ((data.getYValue() instanceof Number)){
            rmsY = rmsY + (data.getYValue().doubleValue()- meanY)*(data.getYValue().doubleValue()- meanY);
        }}

        rmsX = Math.sqrt(rmsX/(series.getData().size()-1.0));
        rmsY = Math.sqrt(rmsY/(series.getData().size()-1.0));      
        
        GridPane chartInfo = new GridPane();
        chartInfo.setHgap(10);
        chartInfo.setVgap(10);
        chartInfo.setPadding(new Insets(10, 10, 5, 10));
        
        ListView test = new ListView();
        test.setPadding(new Insets(10, 10, 5, 10));
        test.getItems().add(String.format("mean(x) = %.4f",meanX));
        test.getItems().add(String.format("Stdev(x) = %.4f",rmsX));
        test.getItems().add(String.format("mean(y) = %.4f",meanY));
        test.getItems().add(String.format("Stdev(y) = %.4f",rmsY));
        test.getItems().add(String.format("int(f(x)) = %.4f",trapezoidalRule(seriesCombo.getItems().indexOf(seriesCombo.getValue()))));       
        test.getItems().add(String.format("int(|f(x)|) = %.4f",absTrapezoidalRule(seriesCombo.getItems().indexOf(seriesCombo.getValue()))));       

        chartInfo.add(new Label("Data series: "),0,0);
        chartInfo.add(seriesCombo,1,0);        
        
        BorderPane secondaryLayout = new BorderPane();
        secondaryLayout.setTop(chartInfo); 
        secondaryLayout.setCenter(test); 

        Scene secondScene = new Scene(secondaryLayout, 300, 150);                                

        // New window (Stage)
        Stage newWindow = new Stage();
        newWindow.setTitle("Data Statistics");
        newWindow.setScene(secondScene);

        newWindow.show();    
        
        seriesCombo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                                
                Series<Number,Number> series = (Series<Number,Number>) ((XYChart<?, ?>)getChart()).getData().get(seriesCombo.getItems().indexOf(seriesCombo.getValue()));
                double meanX = 0.0;
                double meanY= 0.0;
                double rmsX= 0.0;
                double rmsY= 0.0;

                //calculate mean values
                for(Data<Number,Number> data : series.getData()){
                    if ((data.getXValue() instanceof Number)){
                        meanX = meanX + data.getXValue().doubleValue();}
                    if ((data.getYValue() instanceof Number)){
                        meanY = meanY + data.getYValue().doubleValue();;
                        }
                }
                meanX = meanX/series.getData().size();
                meanY = meanY/series.getData().size();

                //calculate rms values
                for(Data<Number,Number> data : series.getData()){
                    if ((data.getXValue() instanceof Number)){
                    rmsX = rmsX + (data.getXValue().doubleValue()- meanX)*(data.getXValue().doubleValue()- meanX);}
                    if ((data.getYValue() instanceof Number)){
                    rmsY = rmsY + (data.getYValue().doubleValue()- meanY)*(data.getYValue().doubleValue()- meanY);
                }}

                rmsX = rmsX/(series.getData().size()-1);
                rmsY = rmsY/(series.getData().size()-1);
                
                test.getItems().clear();
                test.getItems().add(String.format("mean(x) = %.4f",meanX));
                test.getItems().add(String.format("Stdev(x)> = %.4f",rmsX));
                test.getItems().add(String.format("mean(y) = %.4f",meanY));
                test.getItems().add(String.format("Stdev(y) = %.4f",rmsY));
                test.getItems().add(String.format("int(f(x)) = %.4f", trapezoidalRule(seriesCombo.getItems().indexOf(seriesCombo.getValue())) ));       
                test.getItems().add(String.format("int(|f(x)|) = %.4f", absTrapezoidalRule(seriesCombo.getItems().indexOf(seriesCombo.getValue())) ));       
                test.refresh();
                
            }
        });                
                       
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private double absTrapezoidalRule(Integer seriesIndex) {
        Double area = 0.0;
        XYChart<?,?> chart = (XYChart) getChart();
        if (!((chart instanceof AreaChart)||(chart instanceof LineChart))) {
            area = 0.0;
        } else {
        for (Integer index=1; index<chart.getData().get(seriesIndex).getData().size(); index++){
            Double b = Double.parseDouble(chart.getData().get(seriesIndex).getData().get(index).getXValue().toString());
            Double a = Double.parseDouble(chart.getData().get(seriesIndex).getData().get(index-1).getXValue().toString());
            Double fb = Double.parseDouble(chart.getData().get(seriesIndex).getData().get(index).getYValue().toString());
            Double fa = Double.parseDouble(chart.getData().get(seriesIndex).getData().get(index-1).getYValue().toString());
            area+=Math.abs((b-a)/2*(fa+fb));}
        }
        
    return area;
    }
    
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private double trapezoidalRule(Integer seriesIndex) {
        Double area = 0.0;
        XYChart<?,?> chart = (XYChart) getChart();
        if (!((chart instanceof AreaChart)||(chart instanceof LineChart))) {
            area = 0.0;}
        else {
        for (Integer index=1; index<chart.getData().get(seriesIndex).getData().size(); index++){
            Double b = Double.parseDouble(chart.getData().get(seriesIndex).getData().get(index).getXValue().toString());
            Double a = Double.parseDouble(chart.getData().get(seriesIndex).getData().get(index-1).getXValue().toString());
            Double fb = Double.parseDouble(chart.getData().get(seriesIndex).getData().get(index).getYValue().toString());
            Double fa = Double.parseDouble(chart.getData().get(seriesIndex).getData().get(index-1).getYValue().toString());
            area+=(b-a)/2*(fa+fb);}
        }
        
    return area;
    }
   
}
