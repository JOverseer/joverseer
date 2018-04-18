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

import java.util.logging.Logger;
import org.txt2xml.core.Processor;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Use the XML output from a Processor to drive a SAX
 * ContentHandler.
 * 
 * @author <A HREF="mailto:smeyfroi@users.sourceforge.net">Steve Meyfroidt</A>
 */
public class SaxDriver {
    
    @SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(SaxDriver.class.getName());
    
    protected static final String ROOT_ELEMENT = "txt2xml";
    protected static final Attributes NULL_ATTRIBUTES = new AttributesImpl();
    
    private Processor processor;

    public SaxDriver() {
    }
    
    public SaxDriver(Processor processor) {
        this.processor = processor;
    }

    /**
     * Dump XML to a SAX ContentHandler.
     * @param text the text.
     * @param handler the handler
     * @throws SAXException if any exception encountered.
     */
    public void generateXmlDocument(CharSequence text, ContentHandler handler)
    throws SAXException {
        if (this.processor == null) {
            throw new IllegalStateException("Driver has no Processor to apply to text.");
        }
        handler.startDocument();
        handler.startElement("", ROOT_ELEMENT, ROOT_ELEMENT, NULL_ATTRIBUTES);
        this.processor.generateXmlFragment(text, handler);
        handler.endElement("", ROOT_ELEMENT, ROOT_ELEMENT);
        handler.endDocument();
    }

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}
}
