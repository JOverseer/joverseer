/*
 * txt2xml: convert arbitrary text into XML.
 * Copyright (c) 2002, Steve Meyfroidt
 * All rights reserved.
 * Email: smeyfroi@users.sourceforge.net
 * 
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name "txt2xml" nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package org.txt2xml.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import org.txt2xml.config.ProcessorFactory;
import org.txt2xml.core.Processor;
import org.txt2xml.driver.StreamDriver;

/**
 * A simple command line utility to apply a txt2xml
 * conversion with a specified configuration to a set
 * of files.
 * 
 * Usage:
 * <pre>
 * java org.txt2xml.cli.Batch &lt;config_xml&gt; &lt;source_file&gt;*
 * </pre>
 * Applies txt2xml to all source files as configured by the config_xml,
 * saving result xml by appending .xml to the source filename.
 * 
 * @author <A HREF="mailto:smeyfroi@users.sourceforge.net">Steve Meyfroidt</A>
 */
public class Batch {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java org.txt2xml.cli.Batch <config_xml> <source_file>*");
            System.out.println("Applies txt2xml to all source files as configured by the config_xml,\n" +
                                        "saving result xml by appending .xml to the source filename.");
            System.exit(1);
        }
        
        String config = args[0];
        String[] sources = new String[args.length - 1];
        System.arraycopy(args, 1, sources, 0, sources.length);
        
        URL configUrl = new File(config).toURL();
        Processor processor = ProcessorFactory.getInstance().createProcessor(configUrl);
        StreamDriver driver = new StreamDriver(processor);
        driver.useDebugOutputProperties();
        
        for (int i = 0; i < sources.length; i++) {
			String sourceName = sources[i];
            processFile(driver, sourceName);
		}
    }
    
    private static void processFile(StreamDriver driver, String sourceName) throws Exception {
        // Make dest file
        String destName = sourceName + ".xml";
        File dest = new File(destName);
        if (dest.exists()) {
            throw new IllegalArgumentException("File '" + destName + "' already exists!");
        }
        // Load source, converting bytes to unicode chars
        FileChannel sourceChannel = new FileInputStream(sourceName).getChannel();
        try {
            MappedByteBuffer sourceByteBuffer = sourceChannel.map(FileChannel.MapMode.READ_ONLY, 0, sourceChannel.size());
            CharsetDecoder decoder = Charset.forName("ISO-8859-15").newDecoder();
            CharBuffer sourceBuffer = decoder.decode(sourceByteBuffer);
            // Do txt2xml
            driver.generateXmlDocument(sourceBuffer, new FileOutputStream(dest));
        } finally {
            // Close source
            sourceChannel.close();
        }
    }
}
