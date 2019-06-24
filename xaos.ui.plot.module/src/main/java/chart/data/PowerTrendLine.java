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

package chart.data;


import se.europeanspallationsource.xaos.ui.plot.data.BaseOLSTrendLine;


/**
 *
 * @author reubenlindroos
 */
public class PowerTrendLine extends BaseOLSTrendLine {
    
    final double offset;
    
    public PowerTrendLine(double offset) {
        this.offset = offset;
        if(Double.isNaN(offset)){
            offset =0.0;
        }        
    }
    
    @Override
    protected double[] xVector(double x) {
        return new double[]{1,Math.log(x)};
    }

    @Override
    protected boolean logY() {return true;}

    @Override
    public int getDegree() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String nameFor(String seriesName) {
        
        String text = new String();
        double a = Math.exp(getCoefficients()[0]);
        double b = getCoefficients()[1];
        text=seriesName+String.format("\n f(x) = %+.2f * x^(%+.2f) \n", a, b);
        
        return text;
    }

    @Override
    public double getOffset() {
        return offset;
    }
   
   
}
