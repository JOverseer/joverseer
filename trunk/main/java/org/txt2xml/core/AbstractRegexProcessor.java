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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base class for regex based Processors that can accept a pattern String
 * and need to create a java.util.regex.Matcher before processing text.
 * 
 * @author <A HREF="mailto:smeyfroi@users.sourceforge.net">Steve Meyfroidt</A>
 */
public abstract class AbstractRegexProcessor extends Processor {
    
    private static final Logger LOG = Logger.getLogger(AbstractRegexProcessor.class.getName());
    
    protected String regex;
    protected Pattern pattern;
    protected Matcher matcher;
    protected int multiline = 1;
    
    protected void resetMatching() {
	    assert (chars != null); // : "No chars but asked to resetMatching!";
	    if (pattern == null) {
	        throw new IllegalStateException("Can't match because no pattern set");
	    }
	    matcher = pattern.matcher(chars);
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        if (LOG.isLoggable(Level.FINER)) LOG.finer("regex=" + regex);
        if (regex == null) {
            throw new IllegalArgumentException("Can't set a null regex pattern");
        }
        this.regex = regex;
        compileRegex();
    }
    
    private void compileRegex() {
    	if (multiline == 1) {
    		pattern = Pattern.compile(regex, Pattern.MULTILINE);
    	} else {
    		pattern = Pattern.compile(regex);
    	}
    }
    
    public int getMultiline() {
    	return multiline;
    }
    
    public void setMultiline(int m) {
    	this.multiline = m;
    	if (regex != null) {
    		compileRegex();
    	}
    }
}
