package org.txt2xml.core;


public class RepeatRegexMatchProcessor extends RegexDelimitedProcessor {
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
            // No more matches, return false
            return false;
        } else {
            // match against the stuff before the delimiter we matched
            matchStart = matcher.start();
            // match to the start of the delimiter we just matched
            matchEnd = matcher.end();
            // start next match from beyond the delimiter we matched
            nextMatchFrom = matcher.end();
        }
        return true;
    }

    protected CharSequence getRemainderText() {
        return new SubCharSequence(chars, nextMatchFrom, chars.length() - nextMatchFrom);
    }
}
