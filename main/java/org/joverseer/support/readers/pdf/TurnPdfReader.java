package org.joverseer.support.readers.pdf;

import org.pdfbox.ExtractText;
import org.pdfbox.util.PDFTextStripper;
import org.pdfbox.pdmodel.PDDocument;

import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 10 Δεκ 2006
 * Time: 9:59:31 μμ
 * To change this template use File | Settings | File Templates.
 */
public class TurnPdfReader {
    public static final String DEFAULT_ENCODING = "UTF-8";

    public static void parsePdf(String pdfFile, String textFile) throws Exception {
        String encoding = DEFAULT_ENCODING;
        int startPage = 1;
        int endPage = Integer.MAX_VALUE;
        Writer output = null;
        PDDocument document = null;
        try {
            document = PDDocument.load(pdfFile);
            if (textFile == null && pdfFile.length() > 4) {
                textFile = pdfFile.substring(0, pdfFile.length() - 4) + ".txt";
            }

            if (encoding != null) {
                output = new OutputStreamWriter(new FileOutputStream(textFile), encoding); 
            }

            PDFTextStripper stripper = null;
            stripper = new PDFTextStripper();
            stripper.setStartPage(startPage);
            stripper.setEndPage(endPage);
            stripper.writeText(document, output);
        }
        finally {
            if (output != null) {
                output.close();
            }
            if (document != null) {
                document.close();
            }
        }
    }

    

    public static void main(String[] args) throws Exception {
        parsePdf(args[0], args[0] + ".txt");
    }
}


