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
 * For internal use. A 
 * CharSequence that represents a sub-sequence
 * of some parent java.lang.CharSequence.
 *
 * @author <A HREF="mailto:smeyfroi@users.sourceforge.net">Steve Meyfroidt</A>
 */
public final class SubCharSequence implements CharSequence {

    private CharSequence parent;
    private int offset;
    private int length;

    public SubCharSequence(CharSequence parent, int offset, int length) {
        if (parent == null) {
            throw new IllegalArgumentException("Can't create a SubCharSequence for a null parent");
        }
        if (offset < 0 || length < 0 || offset > parent.length() || offset + length > parent.length()) {
            throw new IndexOutOfBoundsException(
                "Can't create a SubCharSequence with offset=" + offset + ", length=" + length + " for parent: [" + parent + "]");
        }
        this.parent = parent;
        this.offset = offset;
        this.length = length;
    }

    public int getOffset() {
        return this.offset;
    }

    /**
     * @see CharSequence#length()
     */
    @Override
	public int length() {
        return this.length;
    }

    /**
     * @see CharSequence#charAt(int)
     */
    @Override
	public char charAt(int index) {
        if (index > this.length - 1) {
            throw new IndexOutOfBoundsException("Can't get charAt for index=" + index + " when length=" + this.length);
        }
        return this.parent.charAt(this.offset + index);
    }

    /**
     * @see CharSequence#subSequence(int, int)
     */
    @Override
	public CharSequence subSequence(int start, int end) {
        if (start < 0 || end < start || end > this.length) {
            throw new IndexOutOfBoundsException("Can't make subSequence with start=" + start + ", end=" + end);
        }
        return new SubCharSequence(this.parent, this.offset + start, end - start);
    }

    @Override
	public String toString() {
        return this.parent.toString().substring(this.offset, this.offset + this.length);
    }

    @Override
	public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof SubCharSequence) {
            SubCharSequence seq = (SubCharSequence) obj;
            int n = this.length;
            if (n == seq.length) {
                int i = 0;
                while (n-- != 0) {
                    if (charAt(i) != seq.charAt(i)) {
                        return false;
                    }
                    ++i;
                }
                return true;
            }
        }
        return false;
    }
}
