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

/**
 * Matches sections of text specified by groups in a 
 * regex pattern. For example when
 * regex='\s*(\d),\s*(\d)' this Processor will 
 * operate against " 1,  2" with the matches: "1", "2".
 * This Processor does not repeat, so in the above case,]
 * it will operate against "1, 2, 3" with "1", "2" and pass on the
 * remainder ", 3" to a subsequent Processor if any.
 * 
 * @author <A HREF="mailto:smeyfroi@users.sourceforge.net">Steve Meyfroidt</A>
 */
public class RegexMatchProcessor extends AbstractRegexProcessor {
	
	private static final Logger LOG = Logger.getLogger(RegexMatchProcessor.class.getName());
    
    private int group;
    private boolean matched;

    @Override
	protected boolean findMatch() {
        if (LOG.isLoggable(Level.FINER)) LOG.finer("group=" + this.group);
        assert (this.chars != null);// : "Null text but asked to findMatch!";
    	if (this.matcher == null) {
    		throw new IllegalStateException("No matcher for this Processor. Was a Pattern specified?");
    	}

        if (! this.matched) {
            return false;
        }
        
        ++this.group;
        if (this.group > this.matcher.groupCount()) {
            // Run out of matches?
            return false;
        } else {
            return true;
        }
    }
    
    @Override
	protected CharSequence getMatchedText() {
        assert (this.matched);// : "Not matched but asked to getMatchedText!";
        assert (this.group <= this.matcher.groupCount());// : "Fallen off end of matched groups but asked to getMatchedText!";
        if (LOG.isLoggable(Level.FINER)) LOG.finer("getMatchedText: " + this.matcher.group(this.group));
        return new SubCharSequence(this.chars, this.matcher.start(this.group), this.matcher.end(this.group) - this.matcher.start(this.group));
    }

    @Override
	protected CharSequence getRemainderText() {
        assert (this.chars != null);// : "Null text but asked to getRemainderText!";
        if (! this.matched) {
            return this.chars;
        }
        assert (this.matcher.end() <= this.chars.length());// : "Fallen off end of text but asked to getRemainderText! (end=" + matcher.end() + ", length=" + chars.length() + ")";
        return new SubCharSequence(this.chars, this.matcher.end(), this.chars.length() - this.matcher.end());
    }

    /**
     * Matches against the regex to find all matched groups
     * that will be stepped through in {@link #findMatch()}.
     */
    @Override
	protected void resetMatching() {
        super.resetMatching();
        assert (this.chars != null);// : "Null text but asked to resetMatching!";
        if (this.matcher.find()) {
            this.matched = true;
            this.group = 0;
        } else {
            this.matched = false;
        }
    }
}
