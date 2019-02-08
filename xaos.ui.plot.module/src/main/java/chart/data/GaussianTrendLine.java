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

import org.apache.commons.math3.fitting.GaussianCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

/**
 *
 * @author nataliamilas
 */
public class GaussianTrendLine implements TrendLine {    
    
    private WeightedObservedPoints obs;
    double[] parameters;
    
    public GaussianTrendLine() {
        obs = new WeightedObservedPoints();        
    }    
       
    public String getResultText(String seriesName) {
        String text = new String();               
        text=seriesName+String.format("\n f(x) = %+.2f * Exp[-(x %+.2f)^2/(2 * %.2f)^2]\n", parameters[0], parameters[1],parameters[2]);        
        
        return text;
    }    

    @Override
    public void setValues(double[] y, double[] x) {
        //add data points
        if (x.length != y.length) {
            throw new IllegalArgumentException(String.format("The numbers of y and x values must be equal (%d != %d)",y.length,x.length));
        }
        for (int j = 0; j < x.length; j++) {
            obs.add(x[j], y[j]);
        }  
        parameters = GaussianCurveFitter.create().fit(obs.toList());
    }

    @Override
    public double predict(double x) {
        double yhat = parameters[0]*Math.exp(-(x-parameters[1])*(x-parameters[1])/(2*parameters[2]*parameters[2]));                
        return yhat;
    }

    @Override
    public double[] getCoefficients() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer getDegree() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Double getOffset() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
