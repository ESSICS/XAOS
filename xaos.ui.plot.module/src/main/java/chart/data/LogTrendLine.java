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
public class LogTrendLine extends OLSTrendLine {
           
    
    @Override
    protected double[] xVector(double x) {
        return new double[]{1,Math.log(x)};
    }

    @Override
    protected boolean logY() {return false;}

    @Override
    public Integer getDegree() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getResultText(String seriesName) {
        String text = new String();
        text=seriesName+String.format("\n f(x) = %+.2f %+.2f * Ln(x)\n", getCoefficients()[0], getCoefficients()[1]);
        
        return text;
    }

    @Override
    public Double getOffset() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    
}
