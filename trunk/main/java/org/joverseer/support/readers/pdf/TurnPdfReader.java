package org.joverseer.support.readers.pdf;

import org.pdfbox.ExtractText;
import org.pdfbox.util.PDFTextStripper;
import org.pdfbox.pdmodel.PDDocument;
import org.txt2xml.core.Processor;
import org.txt2xml.config.ProcessorFactory;
import org.txt2xml.driver.StreamDriver;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 10 Δεκ 2006
 * Time: 9:59:31 μμ
 * To change this template use File | Settings | File Templates.
 */
public class TurnPdfReader {
    public static final String DEFAULT_ENCODING = "UTF-8";

    public static String parsePdf(String pdfFile) throws Exception {
        String encoding = DEFAULT_ENCODING;
        int startPage = 1;
        int endPage = Integer.MAX_VALUE;
        Writer output = null;
        PDDocument document = null;
        ByteArrayOutputStream outs = null;
        try {
            document = PDDocument.load(pdfFile);


            if (encoding != null) {
                output = new OutputStreamWriter(outs = new ByteArrayOutputStream(), encoding);
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
        return new String(outs.toByteArray(), "UTF-8");
    }

    public static void pdf2xml(String pdfFile) {
        String xmlFile = null;
        if (xmlFile == null && pdfFile.length() > 4) {
            xmlFile = pdfFile + ".xml";
        }
        try {
            Processor processor = ProcessorFactory.getInstance().createProcessor(new FileReader("target/classes/ctx/txt2xml.config.xml"));
            String pdfContents = parsePdf(pdfFile);
            FileOutputStream outStream = new FileOutputStream(xmlFile);
            StreamDriver driver = new StreamDriver(processor);
            driver.useDebugOutputProperties();
            driver.generateXmlDocument(pdfContents, outStream);
            outStream.close();

        }
        catch (Exception exc) {
            // TODO
            int a = 1;
        }

    }

    public static void main(String[] args) throws Exception {
        pdf2xml(args[0]);
    }
}


