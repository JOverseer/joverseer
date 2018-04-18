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

/**
 * Repeatedly matches sections of text delimited 
 * by a regex pattern. For example, when
 * regex=',' this Processor will operate against "1,2,3" with the
 * following matches: "1", "2", "3".
 * 
 * @author <A HREF="mailto:smeyfroi@users.sourceforge.net">Steve Meyfroidt</A>
 */
public class RegexDelimitedProcessor extends AbstractRegexProcessor {
	
    protected int matchStart;
    protected int matchEnd;
    protected int nextMatchFrom;

    @Override
	protected boolean findMatch() {
        assert (this.chars != null);// : "Null text but asked to findMatch!";
        if (this.matcher == null) {
            throw new IllegalStateException("No matcher for this Processor. Was a regex pattern specified?");
        }
        
        // Fallen off the end of the text? Then no match.
        if (this.nextMatchFrom >= this.chars.length()) {
            this.matchStart = this.chars.length();
            this.matchEnd = this.chars.length();
            return false;
        }
        
        if (! this.matcher.find()) {
            // No more delimiters so match the end of the text
            this.matchStart = this.nextMatchFrom;
            this.matchEnd = this.chars.length();
            this.nextMatchFrom = this.chars.length();
        } else {
            // match against the stuff before the delimiter we matched
            this.matchStart = this.nextMatchFrom;
            // match to the start of the delimiter we just matched
            this.matchEnd = this.matcher.start();
            // start next match from beyond the delimiter we matched
            this.nextMatchFrom = this.matcher.end();
        }
        return true;
    }
    
    @Override
	protected CharSequence getMatchedText() {
        assert (this.matchStart < this.chars.length());// : "Fallen off end of text but asked to getMatchedText!";
        return new SubCharSequence(this.chars, this.matchStart, this.matchEnd - this.matchStart);
    }

    /**
     * @return null because this repeating Processor 
     * completely consumes the text.
     */
    @Override
	protected CharSequence getRemainderText() {
        return null;
    }
    
    @Override
	protected void resetMatching() {
        super.resetMatching();
        this.nextMatchFrom = 0;
    }
}
