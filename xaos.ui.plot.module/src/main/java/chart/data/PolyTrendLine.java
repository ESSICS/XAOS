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

/**
 *
 * @author reubenlindroos
 */
public class PolyTrendLine extends OLSTrendLine {
    
    final int degree;
    
    public PolyTrendLine(int degree) {
        if (degree < 0) throw new IllegalArgumentException("The degree of the polynomial must not be negative");
        this.degree = degree;
    }
    
    protected double[] xVector(double x) { // {1, x, x*x, x*x*x, ...}
        double[] poly = new double[degree+1];
        double xi=1;
        for(int i=0; i<=degree; i++) {
            poly[i]=xi;
            xi*=x;
        }
        return poly;
    }
    
    @Override
    protected boolean logY() {return false;}
    
    @Override
    public Integer getDegree() {
        return degree;
    }       

    @Override
    public String getResultText(String seriesName) {
        String text = new String();
        text = seriesName+"\n f(x) = ";
        for (int i = 0; i <= getDegree(); i++) {
            text=text+String.format("%+.2f x^%d", getCoefficients()[i], i);
        }
        text=text+"\n";
        return text;
    }

    @Override
    public Double getOffset() {
        return 0.0;
    }    

      
}
