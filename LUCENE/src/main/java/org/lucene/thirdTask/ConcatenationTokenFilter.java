package org.lucene;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

import java.io.IOException;

public class ConcatenationTokenFilter extends TokenFilter {

    // Part of the 3rd task:

    //extend org.apache.lucene.analysis.TokenFilter to have a filter that concatenates all tokens
    // from a given token stream via a configured delimiter (" " by default).
    //Such a filter can be useful to solve the problem of indexing whole field values with stop words removal.
    protected ConcatenationTokenFilter(TokenStream input) {
        super(input);
    }

    @Override
    public boolean incrementToken() throws IOException {
        return false;
    }
}
