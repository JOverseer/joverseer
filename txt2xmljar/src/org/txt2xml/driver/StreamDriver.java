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

package org.txt2xml.driver;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Logger;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import org.txt2xml.core.Processor;
import org.xml.sax.SAXException;

/**
 * Sends XML from a Processor to a Stream via a null Transformer used
 * as a serializer.
 * 
 * @author <A HREF="mailto:smeyfroi@users.sourceforge.net">Steve Meyfroidt</A>
 */
public class StreamDriver extends SaxDriver {
    
    @SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(StreamDriver.class.getName());
    
    private Properties outputProperties;

    public StreamDriver() {
        super();
    }
    
    public StreamDriver(Processor processor) {
        super(processor);
    }
    
    /**
     * Make a set of default OutputProperties for 
     * <code>Transformer.setOutputProperties</code>
     * suitable for debug XML output.
     *
     * @see javax.xml.transform.OutputKeys
     */
    public void useDebugOutputProperties() {
        this.outputProperties = new Properties();
        this.outputProperties.put(OutputKeys.METHOD, "xml");
        this.outputProperties.put(OutputKeys.INDENT, "yes");
    }

    /**
     * Dump XML to an OutputStream using a null
     * XSL Transformer as a serializer, and closing the
     * OutputStream after use.
     * @param text the text.
     * @param stream the stream
     * @throws SAXException if any exception encountered.
     * @throws TransformerConfigurationException if any exception encountered.
     * @throws IOException if any IOexception encountered.
     */
    public void generateXmlDocument(CharSequence text, OutputStream stream)
    throws SAXException, TransformerConfigurationException, IOException {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        TransformerHandler trans = ((SAXTransformerFactory)tFactory).newTransformerHandler();
        if (this.outputProperties != null) {
            trans.getTransformer().setOutputProperties(this.outputProperties);
        }
        trans.setResult(new StreamResult(stream));
        try {
            generateXmlDocument(text, trans);
        } finally {
            stream.flush();
        }
    }

	/**
	 * Gets the current output properties used for the 
     * Transformer that serializes XML to an OutputStream.
     *
     * @see #useDebugOutputProperties()
     * @see javax.xml.transform.OutputKeys
	 * @return current output properties used for the 
     * Transformer that serializes XML to an OutputStream
	 */
	public Properties getOutputProperties() {
		return this.outputProperties;
	}

	/**
     * Gets the current output properties used for the 
     * Transformer that serializes XML to an OutputStream.
     *
     * @see #useDebugOutputProperties()
     * @see javax.xml.transform.OutputKeys
	 * @param outputProperties current output properties used for the 
     * Transformer that serializes XML to an OutputStream
	 */
	public void setOutputProperties(Properties outputProperties) {
		this.outputProperties = outputProperties;
	}
}
