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

import java.util.logging.Logger;

/**
 * Repeatedly matches sections of text delimited 
 * by a regex pattern. For example, when
 * regex=',' this Processor will operate against "1,2,3" with the
 * following matches: "1", "2", "3".
 * 
 * @author <A HREF="mailto:smeyfroi@users.sourceforge.net">Steve Meyfroidt</A>
 */
public class RegexDelimitedProcessor extends AbstractRegexProcessor {
	
	private static final Logger LOG = Logger.getLogger(RegexDelimitedProcessor.class.getName());
    
    protected int matchStart;
    protected int matchEnd;
    protected int nextMatchFrom;

    protected boolean findMatch() {
        assert (chars != null);// : "Null text but asked to findMatch!";
        if (matcher == null) {
            throw new IllegalStateException("No matcher for this Processor. Was a regex pattern specified?");
        }
        
        // Fallen off the end of the text? Then no match.
        if (nextMatchFrom >= chars.length()) {
            matchStart = chars.length();
            matchEnd = chars.length();
            return false;
        }
        
        if (! matcher.find()) {
            // No more delimiters so match the end of the text
            matchStart = nextMatchFrom;
            matchEnd = chars.length();
            nextMatchFrom = chars.length();
        } else {
            // match against the stuff before the delimiter we matched
            matchStart = nextMatchFrom;
            // match to the start of the delimiter we just matched
            matchEnd = matcher.start();
            // start next match from beyond the delimiter we matched
            nextMatchFrom = matcher.end();
        }
        return true;
    }
    
    protected CharSequence getMatchedText() {
        assert (matchStart < chars.length());// : "Fallen off end of text but asked to getMatchedText!";
        return new SubCharSequence(chars, matchStart, matchEnd - matchStart);
    }

    /**
     * @return null because this repeating Processor 
     * completely consumes the text.
     */
    protected CharSequence getRemainderText() {
        return null;
    }
    
    protected void resetMatching() {
        super.resetMatching();
        nextMatchFrom = 0;
    }
}
