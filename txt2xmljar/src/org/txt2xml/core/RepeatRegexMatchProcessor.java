package org.txt2xml.core;


public class RepeatRegexMatchProcessor extends RegexDelimitedProcessor {
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
            // No more matches, return false
            return false;
        } else {
            // match against the stuff before the delimiter we matched
            this.matchStart = this.matcher.start();
            // match to the start of the delimiter we just matched
            this.matchEnd = this.matcher.end();
            // start next match from beyond the delimiter we matched
            this.nextMatchFrom = this.matcher.end();
        }
        return true;
    }

    @Override
	protected CharSequence getRemainderText() {
        return new SubCharSequence(this.chars, this.nextMatchFrom, this.chars.length() - this.nextMatchFrom);
    }
}
