package org.lucene.thirdTask;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

public class ConcatenationTokenFilter extends TokenFilter {

    // Tests are placed in the: src/test/java/org/lucene/ThirdTaskTest.java AND src/test/java/org/lucene/ThirdTaskRefactoredTest.java

    private final String delimiter;
    private StringBuffer concatenatedTokensBuffer;
    private CharTermAttribute charTermAttr;


    protected ConcatenationTokenFilter(TokenStream input) {
        super(input);
        this.delimiter = " ";
        this.charTermAttr = addAttribute(CharTermAttribute.class);
        concatenatedTokensBuffer = new StringBuffer();

    }

    protected ConcatenationTokenFilter(TokenStream input, String _delimiter) {
        super(input);
        this.delimiter = _delimiter;
        this.charTermAttr = addAttribute(CharTermAttribute.class);
        concatenatedTokensBuffer = new StringBuffer();
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (!input.incrementToken()) {
            return false;
        }

        do {
            char[] buffer = charTermAttr.buffer();

            // new buffer is declared in order to omit null values in the original buffer (it's buffer's free space)
            int length = charTermAttr.length();
            char[] newBuffer = new char[length];
            System.arraycopy(buffer, 0, newBuffer, 0, length);

            // add new tokens to the concatenation StringBuffer
            concatenatedTokensBuffer.append(String.valueOf(newBuffer)).append(delimiter);
            charTermAttr.setEmpty();
        } while(input.incrementToken());

        // remove redundant delimiter at the end of the concatenation
        concatenatedTokensBuffer.delete(concatenatedTokensBuffer.length() - delimiter.length(), concatenatedTokensBuffer.length());

        int start = 0;
        int end = concatenatedTokensBuffer.length();

        char[] arr = new char[end - start];
        concatenatedTokensBuffer.getChars(start, end, arr, 0);

        charTermAttr.setEmpty();

        // following line is responsible for returning token outside the filter
        charTermAttr.copyBuffer(arr, 0, concatenatedTokensBuffer.length());

        concatenatedTokensBuffer = new StringBuffer();
        return true;
    }
}
