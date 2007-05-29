package org.joverseer.domain;

import java.io.Serializable;

import org.pdfbox.pdmodel.PDDocument;

/**
 * Stores the text representation of the pdf turn result for a given nation
 * 
 * @author Marios Skounakis
 */
public class PdfTurnText implements Serializable {
    private static final long serialVersionUID = 4090453769447908846L;
    int nationNo;
    String text;
    
    public int getNationNo() {
        return nationNo;
    }
    
    public void setNationNo(int nationNo) {
        this.nationNo = nationNo;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }

    
    
}
