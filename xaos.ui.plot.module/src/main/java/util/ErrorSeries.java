/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Line;

/**
 *
 * @author nataliamilas
 */

public class ErrorSeries<X,Y>{
   
    private ObservableList<ErrorData<X,Y>> displayedErrorData = FXCollections.observableArrayList();
    
    //Series number to attach error bar
    private Integer seriesRef = null; 
    boolean setToRemove = false;
    StringProperty name;                
    
    /*private final ListChangeListener<ErrorData<X,Y>> dataChangeListener = new ListChangeListener<ErrorData<X, Y>>() {
            @Override public void onChanged(ListChangeListener.Change<? extends ErrorData<X, Y>> c) {
                ObservableList<? extends ErrorData<X, Y>> data = c.getList();
                while (c.next()) {
                    if (c.wasPermutated()) {
                        displayedErrorData.sort((o1, o2) -> data.indexOf(o2) - data.indexOf(o1));
                        return;
                    }

                    Set<ErrorData<X, Y>> dupCheck = new HashSet<>(displayedErrorData);
                    dupCheck.removeAll(c.getRemoved());
                    for (ErrorData<X, Y> d : c.getAddedSubList()) {
                        if (!dupCheck.add(d)) {
                            throw new IllegalArgumentException("Duplicate data added");
                        }
                    }

                    // update data items reference to series
                    for (ErrorData<X, Y> item : c.getRemoved()) {
                        item.setToRemove = true;
                    }

                    if (c.getAddedSize() > 0) {
                        for (ErrorData<X, Y> itemPtr : c.getAddedSubList()) {
                            if (itemPtr.setToRemove) {
                                itemPtr.setToRemove = false;
                            }
                        }

                        if (c.getFrom() == 0) {
                            displayedErrorData.addAll(0, c.getAddedSubList());
                        } else {
                            displayedErrorData.addAll(displayedErrorData.indexOf(data.get(c.getFrom() - 1)) + 1, c.getAddedSubList());
                        }
                    }                                        
                }
            };
        }; */     

        // -------------- PUBLIC PROPERTIES ----------------------------------------

        
        public Integer getSeriesRef() {
        return seriesRef;
        }

        public void setSeriesRef(Integer seriesRef) {
            this.seriesRef = seriesRef;
        }
    
    
        public final String getName() { return name.get(); }
        public final void setName(String value) { name.set(value); }
        public final StringProperty nameProperty() { return name; }

        /**
         * The node to display for this series. This is created by the chart if it uses nodes to represent the whole
         * series. For example line chart uses this for the line but scatter chart does not use it. This node will be
         * set as soon as the series is added to the chart. You can then get it to add mouse listeners etc.
         */
        private ObjectProperty<Node> node = new SimpleObjectProperty<Node>(this, "node");
        public final Node getNode() { return node.get(); }
        public final void setNode(Node value) { node.set(value); }
        public final ObjectProperty<Node> nodeProperty() { return node; }

        /** ObservableList of data items that make up this series */
        /*private final ObjectProperty<ObservableList<ErrorData<X,Y>>> data = new ObjectPropertyBase<ObservableList<ErrorData<X,Y>>>() {
            private ObservableList<ErrorData<X,Y>> old;
            @Override protected void invalidated() {
                final ObservableList<ErrorData<X,Y>> current = getValue();
                // add remove listeners
                if(old != null) old.removeListener(dataChangeListener);
                if(current != null) current.addListener(dataChangeListener);
                // fire data change event if series are added or removed
                if(old != null || current != null) {
                    final List<ErrorData<X,Y>> removed = (old != null) ? old : Collections.<ErrorData<X,Y>>emptyList();
                    final int toIndex = (current != null) ? current.size() : 0;
                    // let data listener know all old data have been removed and new data that has been added
                    if (toIndex > 0 || !removed.isEmpty()) {
                        dataChangeListener.onChanged(new NonIterableChange<ErrorData<X,Y>>(0, toIndex, current){
                            @Override public List<ErrorData<X,Y>> getRemoved() { return removed; }

                            @Override protected int[] getPermutation() {
                                return new int[0];
                            }
                        });
                    }
                } else if (old != null && old.size() > 0) {
                    // let series listener know all old series have been removed
                    dataChangeListener.onChanged(new NonIterableChange<ErrorData<X,Y>>(0, 0, current){
                        @Override public List<ErrorData<X,Y>> getRemoved() { return old; }
                        @Override protected int[] getPermutation() {
                            return new int[0];
                        }
                    });
                }
                old = current;
            }

            @Override
            public Object getBean() {
                return ErrorSeries.this;
            }

            @Override
            public String getName() {
                return "data";
            }
        };
        public final ObservableList<ErrorData<X,Y>> getData() { return displayedErrorData; }
        public final void setData(ObservableList<ErrorData<X,Y>> value) { data.setValue(value); }
        public final ObjectProperty<ObservableList<ErrorData<X,Y>>> dataProperty() { return data; }*/
        
        public final ObservableList<ErrorData<X,Y>> getData() { return displayedErrorData; }
        public final void setData(ObservableList<ErrorData<X,Y>> data) { displayedErrorData = data; }

        // -------------- CONSTRUCTORS ----------------------------------------------

        /**
         * Construct a empty series
         */
        public ErrorSeries() {

        }

        /**
         * Constructs a Series and populates it with the given {@link ObservableList} data.
         *
         * @param data ObservableList of XYChart.Data
         */
        public ErrorSeries(ObservableList<ErrorData<X,Y>> data) {
            setData(data);
            for(ErrorData<X,Y> item:data) item.setErrorSeries(this);
        }

        /**
         * Constructs a named Series and populates it with the given {@link ObservableList} data.
         *
         * @param name a name for the series
         * @param data ObservableList of XYChart.Data
         */
        public ErrorSeries(String name, ObservableList<ErrorData<X,Y>> data) {
            this(data);
            setName(name);
        }

        // -------------- PUBLIC METHODS ----------------------------------------------

        /**
         * Returns a string representation of this {@code Series} object.
         * @return a string representation of this {@code Series} object.
         */
        @Override public String toString() {
            return "Series["+getName()+"]";
        }
        
        public void addErrorData(ErrorData<X,Y> value){
            displayedErrorData.add(value);
        }

        // -------------- PRIVATE/PROTECTED METHODS -----------------------------------

        /*
         * The following methods are for manipulating the pointers in the linked list
         * when data is deleted.
         */
        private void removeDataItemRef(ErrorData<X,Y> item) {
            if (item != null) item.setToRemove = false;
            displayedErrorData.remove(item);
        }

        int getItemIndex(XYChart.Data<X,Y> item) {
            return displayedErrorData.indexOf(item);
        }

        ErrorData<X, Y> getItem(int i) {
            return displayedErrorData.get(i);
        }

        int getDataSize() {
            return displayedErrorData.size();
        }
        

    public static final class ErrorData<X,Y>{

        // data point connected to the error bar
        private XYChart.Data<X,Y> errorData = new XYChart.Data<X,Y>();

        private Line xErrorLine = new Line(); 
        private Line yErrorLine = new Line();    

        //Coordinates of the error start and end
        private List<X> xErrorBar = new ArrayList<X>();   
        private List<Y> yErrorBar = new ArrayList<Y>();  

        private boolean setToRemove = false;
        /** The series this data belongs to */
        private ErrorSeries<X,Y> errorSeries;
        void setErrorSeries(ErrorSeries<X,Y> series) {
            this.errorSeries = series;
        }

        /**
        * Constructor
        */
        public ErrorData(){

        }

        public ErrorData(XYChart.Data<X,Y> data){
           errorData = data;        
        }

        public ErrorData(XYChart.Data<X,Y> data, double yError){
           this(data);
           List catList = new ArrayList<>();
           List<Number> numberList = new ArrayList<>();


           if ((!(data.getXValue() instanceof Number)||(data.getXValue() instanceof BigDecimal)||(data.getXValue() instanceof Long))&& (data.getYValue() instanceof Number)){
               numberList.add(0, Double.parseDouble(errorData.getYValue().toString()) * (1-yError));
               numberList.add(1, Double.parseDouble(errorData.getYValue().toString()) * (1+yError));           
               catList.add(0, errorData.getXValue());
               catList.add(1, errorData.getXValue());
                setErrors(catList,numberList);}

           else if ((!(data.getYValue() instanceof Number))&& (data.getXValue() instanceof Number)){
                numberList.add(0, Double.parseDouble(errorData.getXValue().toString()) * (1-yError));
                numberList.add(1, Double.parseDouble(errorData.getXValue().toString()) * (1+yError));
                catList.add(0, errorData.getYValue());
                catList.add(1, errorData.getYValue());
                setErrors(numberList,catList);
           }
           else{
               throw new IllegalArgumentException("Axis type incorrect, at least one of the Axes should be a Number axis. If both are the error val should be specified for both x and y. ");
           };
           };


        public ErrorData(XYChart.Data<X,Y> data, double[] xError, double[] yError){
           this(data);
           List<Number> xErrorList = new ArrayList<>();
           List<Number> yErrorList = new ArrayList<>();
           xErrorList.add(0, Double.parseDouble(errorData.getXValue().toString()) + xError[0]);
           xErrorList.add(1, Double.parseDouble(errorData.getXValue().toString()) - xError[1]);
           yErrorList.add(0, Double.parseDouble(errorData.getYValue().toString()) + yError[0]);
           yErrorList.add(1, Double.parseDouble(errorData.getYValue().toString()) - yError[1]);
           setErrors(xErrorList,yErrorList);
        }

        public ErrorData(XYChart.Data<X,Y> data, double xError, double yError){       
           this(data);
           List<Number> xErrorList = new ArrayList<>();
           List<Number> yErrorList = new ArrayList<>();
           xErrorList.add(0, Double.parseDouble(errorData.getXValue().toString()) * (1-xError));
           xErrorList.add(1, Double.parseDouble(errorData.getXValue().toString()) * (1+xError));
           yErrorList.add(0, Double.parseDouble(errorData.getYValue().toString()) * (1-yError));
           yErrorList.add(1, Double.parseDouble(errorData.getYValue().toString()) * (1+yError));
           setErrors(xErrorList,yErrorList);
        }

        public void setErrors(List<?> xError, List<Number> yError){
           xErrorBar.add(0, (X) xError.get(0));
           xErrorBar.add(1, (X) xError.get(1)); 
           yErrorBar.add(0, (Y) yError.get(0));
           yErrorBar.add(1, (Y) yError.get(1)); 
        }     

        public XYChart.Data<?,?> getDataPoint(){
            return errorData;
        }

        public void setDataPoint(XYChart.Data<X,Y> data){
            this.errorData = data;
        }
        @SuppressWarnings({ "unchecked", "rawtypes" })
        public List<?> getXErrorBar(){
            return xErrorBar;
        }

        public Line getXErrorLine(){
            return xErrorLine;
        }

        public void setXErrorBar(List<X> xErrorBar) {
            this.xErrorBar = xErrorBar;
        }

        public List<?> getYErrorBar(){
            return yErrorBar;
        }

        public Line getYErrorLine(){
            return yErrorLine;
        }

        public void setYErrorBar(List<Y> yErrorBar) {
            this.yErrorBar = yErrorBar;
        }                

        public Boolean isData(XYChart.Data<?,?> data){
            if (errorData.getXValue().equals(data.getXValue()) && errorData.getYValue().equals(data.getYValue())){
                return true;
            } else {
                return false;
            }
        }   

        public void resetLine(){
            xErrorLine.setStartX(0);
            xErrorLine.setEndX(0);
            xErrorLine.setStartY(0);
            xErrorLine.setEndY(0);

            yErrorLine.setStartX(0);
            yErrorLine.setEndX(0);
            yErrorLine.setStartY(0);
            yErrorLine.setEndY(0);        
        }
    }

}

   
    

