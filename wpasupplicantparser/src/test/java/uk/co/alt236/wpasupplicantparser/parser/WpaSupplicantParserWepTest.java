package uk.co.alt236.wpasupplicantparser.parser;

import org.junit.Before;
import org.junit.Test;

import uk.co.alt236.wpasupplicantparser.container.WepNetworkInfo;
import uk.co.alt236.wpasupplicantparser.container.WifiNetworkInfo;
import uk.co.alt236.wpasupplicantparser.container.WifiNetworkType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WpaSupplicantParserWepTest {

    private WpaSupplicantParser parser;

    @Before
    public void setUp() {
        parser = new WpaSupplicantParser();
    }

    @Test
    public void testWep01() {
        final String input = "# LEAP with dynamic WEP keys\n" +
                "network={\n" +
                "\tssid=\"leap-example\"\n" +
                "\tkey_mgmt=IEEE8021X\n" +
                "\teap=LEAP\n" +
                "\tidentity=\"user\"\n" +
                "\tpassword=\"foobar\"\n" +
                "}";

        final WifiNetworkInfo networkInfo = parser.parseNetworkBlock(input);
        assertNotNull(networkInfo);
        assertEquals(TestUtil.quote("leap-example"), networkInfo.getSsid());

        assertEquals(WifiNetworkType.WEP, networkInfo.getNetType());
        final WepNetworkInfo wepNetworkInfo = (WepNetworkInfo) networkInfo;
        assertEquals(TestUtil.quote("foobar"), wepNetworkInfo.getPassword());
    }

    @Test
    public void testWep02() {
        final String input = "# Shared WEP key connection (no WPA, no IEEE 802.1X)\n" +
                "network={\n" +
                "\tssid=\"static-wep-test\"\n" +
                "\tkey_mgmt=NONE\n" +
                "\twep_key0=\"abcde\"\n" +
                "\twep_key1=0102030405\n" +
                "\twep_key2=\"1234567890123\"\n" +
                "\twep_tx_keyidx=0\n" +
                "\tpriority=5\n" +
                "}";

        final WifiNetworkInfo networkInfo = parser.parseNetworkBlock(input);
        assertNotNull(networkInfo);
        assertEquals(TestUtil.quote("static-wep-test"), networkInfo.getSsid());

        assertEquals(WifiNetworkType.WEP, networkInfo.getNetType());
        final WepNetworkInfo wepNetworkInfo = (WepNetworkInfo) networkInfo;
        assertEquals(TestUtil.quote("abcde"), wepNetworkInfo.getPassword());
        assertEquals(TestUtil.quote("abcde"), wepNetworkInfo.getPassword(0));
        assertEquals("0102030405", wepNetworkInfo.getPassword(1));
        assertEquals(TestUtil.quote("1234567890123"), wepNetworkInfo.getPassword(2));

    }

    @Test
    public void testWep03() {
        final String input = "# Shared WEP key connection (no WPA, no IEEE 802.1X) using Shared Key\n" +
                "# IEEE 802.11 authentication\n" +
                "network={\n" +
                "\tssid=\"static-wep-test2\"\n" +
                "\tkey_mgmt=NONE\n" +
                "\twep_key0=\"abcde\"\n" +
                "\twep_key1=0102030405\n" +
                "\twep_key2=\"1234567890123\"\n" +
                "\twep_tx_keyidx=0\n" +
                "\tpriority=5\n" +
                "\tauth_alg=SHARED\n" +
                "}";

        final WifiNetworkInfo networkInfo = parser.parseNetworkBlock(input);
        assertNotNull(networkInfo);
        assertEquals(TestUtil.quote("static-wep-test2"), networkInfo.getSsid());

        assertEquals(WifiNetworkType.WEP, networkInfo.getNetType());
        final WepNetworkInfo wepNetworkInfo = (WepNetworkInfo) networkInfo;
        assertEquals(TestUtil.quote("abcde"), wepNetworkInfo.getPassword());
        assertEquals(TestUtil.quote("abcde"), wepNetworkInfo.getPassword(0));
        assertEquals("0102030405", wepNetworkInfo.getPassword(1));
        assertEquals(TestUtil.quote("1234567890123"), wepNetworkInfo.getPassword(2));
    }
}