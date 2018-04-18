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

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;

import org.txt2xml.core.Processor;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Sends XML from a Processor into a DOM document.
 * 
 * Note: this is not tested
 * 
 * @author <A HREF="mailto:smeyfroi@users.sourceforge.net">Steve Meyfroidt</A>
 */
public class DomDriver extends SaxDriver {
    
    @SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DomDriver.class.getName());
    
    public DomDriver() {
        super();
    }
    
    public DomDriver(Processor processor) {
        super(processor);
    }
    
    /**
     * Dump XML into a returned DOM Document.
     * @param text the text.
     * @return Node the fragment
     * @throws SAXException if any exception encountered.
     * @throws TransformerConfigurationException if any exception encountered.
     */
    public Node generateXmlDocument(CharSequence text)
    throws SAXException, TransformerConfigurationException {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        TransformerHandler trans = ((SAXTransformerFactory)tFactory).newTransformerHandler();
        DOMResult domResult = new DOMResult();
        trans.setResult(domResult);
        generateXmlDocument(text, trans);
        return domResult.getNode();
    }
}
