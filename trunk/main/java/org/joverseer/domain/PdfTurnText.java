package org.joverseer.domain;

import java.io.Serializable;

import org.pdfbox.pdmodel.PDDocument;


public class PdfTurnText implements Serializable {
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
