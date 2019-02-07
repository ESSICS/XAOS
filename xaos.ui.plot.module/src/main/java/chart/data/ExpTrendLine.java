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
public class ExpTrendLine extends OLSTrendLine {
    
    final double offset;
    
    public ExpTrendLine(double offset) {
        this.offset = offset;
        if(Double.isNaN(offset)){
            offset =0.0;
        }        
    }    
    
    @Override
    protected double[] xVector(double x) {
        return new double[]{1,x};
    }

    @Override
    protected boolean logY() {return true;}

    @Override
    public Integer getDegree() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getResultText(String seriesName) {
        String text = new String();        
        double a = Math.exp(getCoefficients()[0]);
        double b = getCoefficients()[1];
        text=seriesName+String.format("\n f(x) = %+.2f * Exp(%+.2f * x)\n", a, b);
        
        return text;
    }

    @Override
    public Double getOffset() {
        return offset;
    }
       
}
