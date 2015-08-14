/*
 * PageOutputParameters.java
 *
 * Created on 19. Maerz 2002, 09:44
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  Thomas
 * @version 
 */
public class PageOutputParameters extends OutputParameters {

    public boolean landscape = false;
    SyncPoint nextSyncPoint;
    
    /** Creates new PageOutputParameters */
    public PageOutputParameters() {
        setPaperMeasures(DINA4_VERTICAL);
    }
    
    public PageOutputParameters(java.awt.print.PageFormat pageFormat){
        setPaperMeasures(pageFormat);
    }

    public static java.awt.print.Paper makePaperFromParameters(PageOutputParameters pop){
        java.awt.print.Paper paper = new java.awt.print.Paper();
        paper.setSize(
                72*(pop.getPaperMeasure("paper:width", PageOutputParameters.INCH_UNIT)),
                72*(pop.getPaperMeasure("paper:height", PageOutputParameters.INCH_UNIT))
                );
        paper.setImageableArea(
                72*pop.getPaperMeasure("margin:left", PageOutputParameters.INCH_UNIT),
                72*pop.getPaperMeasure("margin:top", PageOutputParameters.INCH_UNIT),
                72*(pop.getPaperMeasure("paper:width", PageOutputParameters.INCH_UNIT) - pop.getPaperMeasure("margin:left", PageOutputParameters.INCH_UNIT) - pop.getPaperMeasure("margin:right",PageOutputParameters.INCH_UNIT)),
                72*(pop.getPaperMeasure("paper:height", PageOutputParameters.INCH_UNIT) - pop.getPaperMeasure("margin:top", PageOutputParameters.INCH_UNIT) - pop.getPaperMeasure("margin:bottom",PageOutputParameters.INCH_UNIT))
                );
        return paper;
    }
    
    public void setPaperMeasures(java.awt.print.PageFormat pageFormat){
        setPaperMeasure("paper:width", pageFormat.getWidth()/72, OutputParameters.INCH_UNIT);
        setPaperMeasure("paper:height", pageFormat.getHeight()/72, OutputParameters.INCH_UNIT);
        setPaperMeasure("margin:left", pageFormat.getImageableX()/72, OutputParameters.INCH_UNIT);
        setPaperMeasure("margin:right", (pageFormat.getWidth()-pageFormat.getImageableX()-pageFormat.getImageableWidth())/72, OutputParameters.INCH_UNIT);
        setPaperMeasure("margin:top", pageFormat.getImageableY()/72, OutputParameters.INCH_UNIT);
        setPaperMeasure("margin:bottom", (pageFormat.getHeight()-pageFormat.getImageableY()-pageFormat.getImageableHeight())/72, OutputParameters.INCH_UNIT);
        if (pageFormat.getOrientation()==java.awt.print.PageFormat.LANDSCAPE){
            landscape = true;
        }
    }
    public double getPaperMeasure(String propertyName, short unit){
        try{
            String propertyValue = getProperty(propertyName);
            double twipsValue = new Double(propertyValue).doubleValue();
            switch (unit) {
                case CM_UNIT : return twipsValue / 567;
                case INCH_UNIT : return twipsValue / 1440;
                case PX_UNIT : return twipsValue / 20;
                default : return twipsValue;
            }
        } catch (Throwable t){return 0;}
    }
    
    public double setPaperMeasure(String propertyName, double value, short unit){
        double twipsValue = 0;
        switch (unit) {
            case CM_UNIT : twipsValue=value*567; break;
            case INCH_UNIT : twipsValue=value*1440; break;
            case PX_UNIT : twipsValue=value*20; break;
            default : twipsValue = value; break;
        }
        setProperty(propertyName,Double.toString(twipsValue));
        setWidth(getPixelWidth());
        return twipsValue;
    }
    
    public void setPaperMeasures(PageOutputParameters pop){
        setPaperMeasure("paper:width",pop.getPaperMeasure("paper:width",CM_UNIT),CM_UNIT);
        setPaperMeasure("paper:height",pop.getPaperMeasure("paper:height",CM_UNIT),CM_UNIT);
        setPaperMeasure("margin:left",pop.getPaperMeasure("margin:left",CM_UNIT),CM_UNIT);
        setPaperMeasure("margin:right",pop.getPaperMeasure("margin:right",CM_UNIT),CM_UNIT);
        setPaperMeasure("margin:top",pop.getPaperMeasure("margin:top",CM_UNIT),CM_UNIT);
        setPaperMeasure("margin:bottom",pop.getPaperMeasure("margin:bottom",CM_UNIT),CM_UNIT);
        landscape = pop.landscape;
    }
    
    public void setPaperMeasures(double width, double height, double leftMargin, 
                                  double rightMargin, double topMargin, double bottomMargin, short unit){
        setPaperMeasure("paper:width",width,unit);                                   
        setPaperMeasure("paper:height",height,unit);                                   
        setPaperMeasure("margin:left",leftMargin,unit);                                   
        setPaperMeasure("margin:right",rightMargin,unit);                                   
        setPaperMeasure("margin:top",topMargin,unit);                                   
        setPaperMeasure("margin:bottom",bottomMargin,unit);                                   
    }
    
    public void setPaperMeasures(short paperFormat){
        switch (paperFormat) {
            case DINA4_VERTICAL : setPaperMeasures(21,29.7,2.5,2.5,2.5,2,CM_UNIT); break;
            case DINA4_HORIZONTAL : setPaperMeasures(29.7,21,2,2.5,2.5,2.5,CM_UNIT); landscape=true; break;
            case DINA3_VERTICAL : setPaperMeasures(29.7,42,2.5,2.5,2.5,2,CM_UNIT); break;
            case DINA3_HORIZONTAL : setPaperMeasures(42,29.7,2,2.5,2.5,2.5,CM_UNIT); landscape=true; break;
            default : setPaperMeasures(DINA4_VERTICAL);
        }
    }
    
    public double getPixelWidth(){
        return (getPaperMeasure("paper:width",PX_UNIT) 
                - getPaperMeasure("margin:left", PX_UNIT) 
                - getPaperMeasure("margin:right",PX_UNIT)
                - rightMarginBuffer);
    }
    
    public double getPixelHeight(){
        return (getPaperMeasure("paper:height",PX_UNIT) 
                - getPaperMeasure("margin:top", PX_UNIT) 
                - getPaperMeasure("margin:bottom",PX_UNIT));
    }
    
    @Override
    public void setSettings(String usernode){
        super.setSettings(usernode);
        java.util.prefs.Preferences settings = java.util.prefs.Preferences.userRoot().node(usernode);
        
        // added for version 1.3.3, 09-Oct-2006
        try{
            setPaperMeasure("paper:width", Double.parseDouble(settings.get("PAGE-Width", "21")), CM_UNIT);
            setPaperMeasure("paper:height", Double.parseDouble(settings.get("PAGE-Height", "29.7")), CM_UNIT);
            setPaperMeasure("margin:left", Double.parseDouble(settings.get("PAGE-Margin-Left", "2.5")), CM_UNIT);
            setPaperMeasure("margin:right", Double.parseDouble(settings.get("PAGE-Margin-Right", "2.5")), CM_UNIT);
            setPaperMeasure("margin:top", Double.parseDouble(settings.get("PAGE-Margin-Top", "2.0")), CM_UNIT);
            setPaperMeasure("margin:bottom", Double.parseDouble(settings.get("PAGE-Margin-Bottom", "2.0")), CM_UNIT);
       } catch (NumberFormatException nfe){
           System.out.println("NumberFormatException" + nfe.getLocalizedMessage());
       } catch (NullPointerException npe){
           
       }
        
    }
    

}
