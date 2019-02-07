/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chart.data;
import org.apache.commons.math3.linear.RealMatrix;

/**
 *
 * @author reubenlindroos
 */
public interface TrendLine {
    
    public void setValues(double[] y, double[] x); // y ~ f(x)
    public double predict(double x); // get a predicted y for a given x
    public double[] getCoefficients(); // get coefficients from the fit
    public Integer getDegree(); //get fitting power
    public Double getOffset(); //get the offset given as input
    public String getResultText(String seriesName);
    
}
