package org.lucene.thirdTask;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

/*
todo: Please add End To End test scenario

 Let us add a test case, where we will actually create a Lucene Index by indexer component and use custom analyzer MyAnalyzer
 Than in runtime, we can make sure that f.e. for phrase in document "this is a phrase" we can match with termQuery with value "this phrase"
 */
public class ThirdTaskTest {

    private static final Logger LOGGER = Logger.getLogger(ThirdTaskTest.class.getName());

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;


    // needed to check if printed out content is valid
    @Before
    public void setUpStream() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStream() {
        System.setOut(originalOut);
    }


    public static void printConcatenatedResult(String textToTest) throws IOException {
        // default delimiter
        printConcatenatedResult(textToTest, " ");
    }

    public static void printConcatenatedResult(String textToTest, String delimiter) throws IOException {
        // create analyzer with custom delimiter which is passed to the ConcatenationTokenFilter
        MyAnalyzer analyzer = new MyAnalyzer(delimiter);
        TokenStream stream = analyzer.tokenStream("field", new StringReader(textToTest));

        // get the CharTermAttribute from the TokenStream
        CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);

        try {
            stream.reset();

            // print all tokens until stream is exhausted
            while (stream.incrementToken()) {
                System.out.println(termAtt.toString());
            }
            stream.end();
        } finally {
            stream.close();
        }
    }

    @Test
    public void whenEnterDefaultDelimiter_thenConcatenateWithSpace() throws IOException {
        String textToConcat = "the following section is intended as a \"getting started\" guide. " +
                "it has three audiences: first-time users looking to install Apache Lucene in their application; ";
        printConcatenatedResult(textToConcat);

        LOGGER.info("Before concatenation:\n" + textToConcat);
        LOGGER.info("After concatenation:\n" + outContent);

        // \n in the expected string since sout adds new line when printing
        assertEquals(
                "following section intended \"getting started\" guide. has three audiences: first-time users looking install Apache Lucene application;\n",
                outContent.toString());
    }

    @Test
    public void whenEnterCustomDelimiter_thenConcatenateWithCustomDelimiter() throws IOException {
        String textToConcat = "the following section is intended as a \"getting started\" guide. " +
                "it has three audiences: first-time users looking to install Apache Lucene in their application; ";
        String delimiter = "--";
        printConcatenatedResult(textToConcat, delimiter);

        LOGGER.info("Before concatenation:\n" + textToConcat);
        LOGGER.info("After concatenation:\n" + outContent);

        // \n in the expected string since sout adds new line when printing
        assertEquals(
                "following--section--intended--\"getting--started\"--guide.--has--three--audiences:--first-time--users--looking--install--Apache--Lucene--application;\n",
                outContent.toString());
    }
}
