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

package se.europeanspallationsource.xaos.ui.plot.util;
import javafx.scene.image.*;
import javafx.geometry.Point2D;
import chart.XYChartPlugin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.Chart;
import se.europeanspallationsource.xaos.ui.util.ResourceUtils;


/**
 *
 * @author reubenlindroos
 */
public class iconParser extends XYChartPlugin{
    private static final double toolSize = 125.0;
    private static ImageView cenCircle;
    private static ImageView rectangle1;
    private static ImageView rectangle2;
    private static ImageView rectangle3;
    private static ImageView rectangle4;
    private static ImageView dirCircle1;
    private static ImageView dirCircle2; 
    private static ImageView dirCircle3; 
    private static ImageView dirCircle4;
    private static ImageView[] cursorTool;
    
    public iconParser(){
        cenCircle =  new ImageView(ResourceUtils.getImage("/icons/neutral/1-1-neutral.png"));
        rectangle1   = new ImageView(ResourceUtils.getImage("/icons/neutral/Rounded-Rectangle-neutral-1.png"));
        rectangle2   = new ImageView(ResourceUtils.getImage("/icons/neutral/Rounded-Rectangle-neutral-2.png"));
        rectangle3   = new ImageView(ResourceUtils.getImage("/icons/neutral/Rounded-Rectangle-neutral-3.png"));
        rectangle4   = new ImageView(ResourceUtils.getImage("/icons/neutral/Rounded-Rectangle-neutral-4.png"));
        dirCircle1 = new ImageView(ResourceUtils.getImage("/icons/neutral/circle-direction-neutral-1.png"));
        dirCircle2 = new ImageView(ResourceUtils.getImage("/icons/neutral/circle-direction-neutral-2.png"));
        dirCircle3 = new ImageView(ResourceUtils.getImage("/icons/neutral/circle-direction-neutral-3.png"));
        dirCircle4 = new ImageView(ResourceUtils.getImage("/icons/neutral/circle-direction-neutral-4.png"));
        cursorTool= getImages();
    }
    public void setChartForIcon(Chart newChart) {
        this.setChart(newChart);
    }
    
    public static ImageView[] getImages() {     
        ImageView[] imageCollection ={cenCircle,rectangle1, rectangle2, rectangle3, rectangle4, dirCircle1, dirCircle2, dirCircle3, dirCircle4}; 
        
        return imageCollection;
    }
    public ObservableList<ImageView> getImagesAsList() {
        ObservableList<ImageView> list = FXCollections.observableArrayList();
        list.addAll(getImages());
        return list;
        
    }
    
    public static void setLocation(Point2D mousePosition) {
        for (ImageView image: cursorTool) {
            image.setX(mousePosition.getX()-toolSize/2);
            image.setY(mousePosition.getY()-toolSize/2);
        }
    }
    
     public static void setLocation(Double xPos, Double yPos) {
        for (ImageView image: cursorTool) {
            image.setX(xPos-toolSize/2);
            image.setY(yPos-toolSize/2);
        }
    }
    public void updateIcon(Boolean inSmallCircle, Boolean inBigCircle, Boolean inOuterRec, Integer index, Double xPos, Double yPos) {
        
        if (inSmallCircle){
            cenCircle = new ImageView(ResourceUtils.getImage("/icons/roll_over/1-1-roll-over.png"));
            cenCircle.setX(xPos-toolSize/2);
            cenCircle.setY(yPos-toolSize/2);
        }                
        else if (inBigCircle) {
            
            if (index==1){ 
                dirCircle1 = new ImageView(ResourceUtils.getImage("/icons/roll_over/circle-direction-roll-over-1.png"));
                dirCircle1.setX(xPos-toolSize/2);
                dirCircle1.setY(yPos-toolSize/2);
            }
            if (index==2){ 
                dirCircle2 = new ImageView(ResourceUtils.getImage("/icons/roll_over/circle-direction-roll-over-2.png"));
                dirCircle2.setX(xPos-toolSize/2);
                dirCircle2.setY(yPos-toolSize/2);
            }
            if (index==3){ 
                dirCircle3 = new ImageView(ResourceUtils.getImage("/icons/roll_over/circle-direction-roll-over-3.png"));
                dirCircle3.setX(xPos-toolSize/2);
                dirCircle3.setY(yPos-toolSize/2);
            }
            if (index==4){ 
                dirCircle4 = new ImageView(ResourceUtils.getImage("/icons/roll_over/circle-direction-roll-over-4.png"));
                dirCircle4.setX(xPos-toolSize/2);
                dirCircle4.setY(yPos-toolSize/2);
            }
            
        }                
        else if (inOuterRec){
            
            if (index==1){
                rectangle1 = new ImageView(ResourceUtils.getImage("/icons/roll_over/Rounded-Rectangle-roll-over-1.png"));
                rectangle1.setX(xPos-toolSize/2);
                rectangle1.setY(yPos-toolSize/2);
            }
            if (index==2){
                rectangle2 = new ImageView(ResourceUtils.getImage("/icons/roll_over/Rounded-Rectangle-roll-over-2.png"));
                rectangle2.setX(xPos-toolSize/2);
                rectangle2.setY(yPos-toolSize/2);
            }
            if (index==3){
                rectangle3 = new ImageView(ResourceUtils.getImage("/icons/roll_over/Rounded-Rectangle-roll-over-3.png"));
                rectangle3.setX(xPos-toolSize/2);
                rectangle3.setY(yPos-toolSize/2);
            }
            if (index==4){
                rectangle4 = new ImageView(ResourceUtils.getImage("/icons/roll_over/Rounded-Rectangle-roll-over-4.png"));
                rectangle4.setX(xPos-toolSize/2);
                rectangle4.setY(yPos-toolSize/2);
            }
            
        }                       
    
    }
}
