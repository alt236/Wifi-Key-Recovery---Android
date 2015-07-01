package uk.co.alt236.wifipasswordaccess.util;

import android.util.Pair;

import junit.framework.TestCase;

import uk.co.alt236.wifipasswordaccess.util.KeyValueSplitter;

/**
 *
 */
public class KeyValueSplitterTest extends TestCase {
    public void test1(){
        final KeyValueSplitter splitter = new KeyValueSplitter("=");
        final Pair<String, String> result = splitter.split("test1=test2");
        assertEquals("test1", result.first);
        assertEquals("test2", result.second);
    }

    public void test2(){
        final KeyValueSplitter splitter = new KeyValueSplitter("=");
        final Pair<String, String> result = splitter.split("test1test2");
        assertEquals(null, result);
    }
}