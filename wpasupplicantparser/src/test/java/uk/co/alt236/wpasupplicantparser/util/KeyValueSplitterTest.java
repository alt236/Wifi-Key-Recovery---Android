package uk.co.alt236.wpasupplicantparser.util;


import junit.framework.TestCase;

public class KeyValueSplitterTest extends TestCase {
    public void test1() {
        final KeyValueSplitter splitter = new KeyValueSplitter("=");
        final KeyValueSplitter.KV<String, String> result = splitter.split("test1=test2");
        assertEquals("test1", result.getKey());
        assertEquals("test2", result.getValue());
    }

    public void test2() {
        final KeyValueSplitter splitter = new KeyValueSplitter("=");
        final KeyValueSplitter.KV<String, String> result = splitter.split("test1test2");
        assertEquals(null, result);
    }
}