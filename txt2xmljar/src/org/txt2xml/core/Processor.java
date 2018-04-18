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

package org.txt2xml.core;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Base class for agents that match part(s) of a CharSequence
 * and write XML Elements as match(es) are found.  
 * Matched CharSequences are passed onto the sub-Processor 
 * for further work. When all matches are done, the remainder of
 * the original CharSequence is passed to the nextProcessor.
 * <p>
 * Processors act as iterators that update their internal state during
 * a round of matching. Processors are not thread-safe.
 * </p>
 * <p>
 * Namespaces in generated XML are not yet supported.
 * </p>
 * <p>
 * Subclasses must override 
 * {@link #findMatch()} 
 * {@link #getMatchedText()} 
 * {@link #getRemainderText()} 
 * and optionally {@link #resetMatching()}.
 * </p>
 * 
 * @author <A HREF="mailto:smeyfroi@users.sourceforge.net">Steve Meyfroidt</A>
 */
public abstract class Processor {
    
    private static final Logger LOG = Logger.getLogger(Processor.class.getName());

    protected static final Attributes NULL_ATTRIBUTES = new AttributesImpl();
    
    /** Apply this Processor to all matches, or null if no sub-processing to be done. */
    private Processor subProcessor = null;
    
    /** The name of XML Elements created, or null if no element to be created. */
    private String element = null;
    
    /** The processor to process remainder of text when this Processor has finished or null if no more processing after this. */
    private Processor nextProcessor = null;
    
    /**
     * Parent Processor that is using this as a sub-Processor. 
     * Might be interesting to a child but not used by default here.
     * Updated during {@link #generateXmlFragment(CharSequence, ContentHandler)}.
     */
    protected Processor parent;
    
    /**
     * The current CharSequence being matched against. Set at the start of 
     * {@link #generateXmlFragment(CharSequence, ContentHandler)}.
     */
    protected CharSequence chars;
    
    /**
     * The current ContentHandler to write XML to. Set at the start of
     * {@link #generateXmlFragment(CharSequence, ContentHandler)}.
     */
    protected ContentHandler handler;
    
    // ----------------------- The kernel

    /**
     * Match part of the passed CharSequence.
     * Pass the matched text to the sub-processor if there is one, else
     * generate a SAX element.
     * When no more matches, 
     * pass the remainder of the original CharSequence to the next Processor
     * if there is one.
     * 
     * @param text to match against.
     * @param contentHandler the SAX handler to send XML to.
     * @throws SAXException if any exception encountered.
     */
    public void generateXmlFragment(CharSequence text, ContentHandler contentHandler)
    throws SAXException {
        if (LOG.isLoggable(Level.FINER)) LOG.finer("element=" + this.element + ", chars=" + text);
        if (text == null) {
            throw new IllegalArgumentException("Can't generate XML for null text");
        }
        if (contentHandler == null) {
            throw new IllegalArgumentException("Can't generate XML to a null ContentHandler");
        }

        // Initialise for a sequence of matching against passed text, with XML to passed handler
        this.chars = text;
        this.handler = contentHandler;
        
        // Prepare for the first match
        resetMatching();
        
        // Keep going until no more matches
        while (findMatch()) {

            // Start the XML Element
            if (this.element != null) {
                generateStartXmlElement();
            }
            
            if (getSubProcessor() == null) {
                // Put characters for the match in the element for leaf Processors
                generateXmlElementCharacters();
            } else {
                getSubProcessor().parent = this;
                CharSequence subChars = getMatchedText();
                getSubProcessor().generateXmlFragment(subChars, this.handler);
            }
            
            // End the XML Element
            if (this.element != null) {
                generateEndXmlElement();
            }
        }
        
        // Pass control to the following processor after this one is done if there is more text to process
        if (getNextProcessor() != null && getRemainderText() != null) {
            getNextProcessor().generateXmlFragment(getRemainderText(), contentHandler);
        }
    }
    
    // ---------------- XML generation
    
    /**
     * Write this Processor's start element as a simple
     * element with no attributes. Override if a Processor
     * needs to create a more complex element start.
     * @throws SAXException if any exception encountered.
     */
    protected void generateStartXmlElement() 
    throws SAXException {
        assert (this.element != null && this.element.length() > 0);
        this.handler.startElement("", this.element, this.element, NULL_ATTRIBUTES);
   }

    /**
     * Write the end element for this Processor.
     * @throws SAXException if any exception encountered.
     */    
    protected void generateEndXmlElement() 
    throws SAXException {
        assert (this.element != null && this.element.length() > 0);
        this.handler.endElement("", this.element, this.element);
    }
    
    /**
     * Write the contents of the element. Called only for leaf
     * Processors (ie no sub-Processor). This default implementation
     * writes the matched text as XML characters. Override for
     * other behaviour.
     * @throws SAXException if any exception encountered.
     */
    protected void generateXmlElementCharacters()
    throws SAXException {
        CharSequence match = getMatchedText();
        assert (match != null);// : "No matched text although got a match!";
        char[] matchedChars = new char[match.length()];
        for (int i = 0; i < match.length(); i++) {
			 matchedChars[i] = match.charAt(i);
		}
        this.handler.characters(matchedChars, 0, matchedChars.length);
    }
    
    // ---------------- Abstract methods to be overriden in concrete subclasses
    
    /**
     * Called at start of {@link #generateXmlFragment(CharSequence, ContentHandler)}
     * to reset the state before starting a round of matching. Override to
     * prepare for a round of matching, eg {@link RegexDelimitedProcessor}
     * resets the regex Matcher here.
     */
    protected void resetMatching() {
    }
    
    /**
     * Find next match, updating state appropriately. Override this!
     * @return true if got a match, else false.
     */
    protected abstract boolean findMatch();
    
    /**
     * Override this!
     * @return CharSequence for text matched in last {@link #findMatch()}.
     */
    protected abstract CharSequence getMatchedText();
    
    /**
     * Override this!
     * @return CharSequence for remainder of text after last match.
     */
    protected abstract CharSequence getRemainderText();
    
    // --------------------- Configuration stuff
    
    /**
     * Gets the nextProcessor.
     * @return Returns a Processor
     */
    public Processor getNextProcessor() {
        return this.nextProcessor;
    }

    /**
     * Sets the nextProcessor.
     * @param nextProcessor The nextProcessor to set
     */
    public void setNextProcessor(Processor nextProcessor) {
        this.nextProcessor = nextProcessor;
    }

    /**
     * @return the sub-Processor that will process this
     * Processor's matched text.
     */
    public Processor getSubProcessor() {
        return this.subProcessor;
    }

    /**
     * @param subProcessor the sub-Processor that will process this
     * Processor's matched text.
     */
    public void setSubProcessor(Processor subProcessor) {
        this.subProcessor = subProcessor;
    }

    /**
     * @return the name of the element written by this Processor.
     */
    public String getElement() {
        return this.element;
    }

    /**
     * @param elementName the name of the element 
     * written by this Processor.
     */
    public void setElement(String elementName) {
        this.element = elementName;
    }
}
