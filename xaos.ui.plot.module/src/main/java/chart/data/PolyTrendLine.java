/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
