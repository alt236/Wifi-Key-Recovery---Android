package uk.co.alt236.wifipasswordaccess.parser;

import junit.framework.Assert;
import junit.framework.TestCase;

import uk.co.alt236.wifipasswordaccess.WifiNetworkType;
import uk.co.alt236.wifipasswordaccess.container.WifiNetworkInfo;
import uk.co.alt236.wifipasswordaccess.container.WpaNetworkInfo;

public class WpaSupplicantParserWpaTest extends TestCase {
    private final WpaSupplicantParser parser = new WpaSupplicantParser();

    public void testWpa01() {
        final String input = "# Simple case: WPA-PSK, PSK as an ASCII passphrase, allow all valid ciphers\n" +
                "network={\n" +
                "\tssid=\"simple\"\n" +
                "\tpsk=\"very secret passphrase\"\n" +
                "\tpriority=5\n" +
                "}";

        final WifiNetworkInfo networkInfo = parser.parseNetworkBlock(input);
        assertNotNull(networkInfo);
        Assert.assertEquals(TestUtil.quote("simple"), networkInfo.getSsid());

        Assert.assertEquals(WifiNetworkType.WPA, networkInfo.getNetType());
        final WpaNetworkInfo wpaNetworkInfo = (WpaNetworkInfo) networkInfo;
        assertEquals(TestUtil.quote("very secret passphrase"), wpaNetworkInfo.getPassword());
    }

    public void testWpa02() {
        final String input = "network={\n" +
                "\tssid=\"second ssid\"\n" +
                "\tscan_ssid=1\n" +
                "\tpsk=\"very secret passphrase\"\n" +
                "\tpriority=2\n" +
                "}";

        final WifiNetworkInfo networkInfo = parser.parseNetworkBlock(input);
        assertNotNull(networkInfo);
        assertEquals(TestUtil.quote("second ssid"), networkInfo.getSsid());

        assertEquals(WifiNetworkType.WPA, networkInfo.getNetType());
        final WpaNetworkInfo wpaNetworkInfo = (WpaNetworkInfo) networkInfo;
        assertEquals(TestUtil.quote("very secret passphrase"), wpaNetworkInfo.getPassword());
    }

    public void testWpa03() {
        final String input = "# Only WPA-PSK is used. Any valid cipher combination is accepted.\n" +
                "network={\n" +
                "\tssid=\"example\"\n" +
                "\tproto=WPA\n" +
                "\tkey_mgmt=WPA-PSK\n" +
                "\tpairwise=CCMP TKIP\n" +
                "\tgroup=CCMP TKIP WEP104 WEP40\n" +
                "\tpsk=06b4be19da289f475aa46a33cb793029d4ab3db7a23ee92382eb0106c72ac7bb\n" +
                "\tpriority=2\n" +
                "}";

        final WifiNetworkInfo networkInfo = parser.parseNetworkBlock(input);
        assertNotNull(networkInfo);
        assertEquals(TestUtil.quote("example"), networkInfo.getSsid());

        assertEquals(WifiNetworkType.WPA, networkInfo.getNetType());
        final WpaNetworkInfo wpaNetworkInfo = (WpaNetworkInfo) networkInfo;
        assertEquals("06b4be19da289f475aa46a33cb793029d4ab3db7a23ee92382eb0106c72ac7bb", wpaNetworkInfo.getPassword());
    }

    public void testWpa04() {
        final String input = "# Only WPA-EAP is used. Both CCMP and TKIP is accepted. An AP that used WEP104\n" +
                "# or WEP40 as the group cipher will not be accepted.\n" +
                "network={\n" +
                "\tssid=\"example\"\n" +
                "\tproto=RSN\n" +
                "\tkey_mgmt=WPA-EAP\n" +
                "\tpairwise=CCMP TKIP\n" +
                "\tgroup=CCMP TKIP\n" +
                "\teap=TLS\n" +
                "\tidentity=\"user@example.com\"\n" +
                "\tca_cert=\"/etc/cert/ca.pem\"\n" +
                "\tclient_cert=\"/etc/cert/user.pem\"\n" +
                "\tprivate_key=\"/etc/cert/user.prv\"\n" +
                "\tprivate_key_passwd=\"password\"\n" +
                "\tpriority=1\n" +
                "}";

        final WifiNetworkInfo networkInfo = parser.parseNetworkBlock(input);
        assertNotNull(networkInfo);
        assertEquals(TestUtil.quote("example"), networkInfo.getSsid());

        assertEquals(WifiNetworkType.WPA, networkInfo.getNetType());
        final WpaNetworkInfo wpaNetworkInfo = (WpaNetworkInfo) networkInfo;
        assertEquals(null, wpaNetworkInfo.getPassword());
    }

    public void testWpa05() {
        final String input = "# EAP-PEAP/MSCHAPv2 configuration for RADIUS servers that use the new peaplabel\n" +
                "# (e.g., Radiator)\n" +
                "network={\n" +
                "\tssid=\"example\"\n" +
                "\tkey_mgmt=WPA-EAP\n" +
                "\teap=PEAP\n" +
                "\tidentity=\"user@example.com\"\n" +
                "\tpassword=\"foobar\"\n" +
                "\tca_cert=\"/etc/cert/ca.pem\"\n" +
                "\tphase1=\"peaplabel=1\"\n" +
                "\tphase2=\"auth=MSCHAPV2\"\n" +
                "\tpriority=10\n" +
                "}";

        final WifiNetworkInfo networkInfo = parser.parseNetworkBlock(input);
        assertNotNull(networkInfo);
        assertEquals(TestUtil.quote("example"), networkInfo.getSsid());

        assertEquals(WifiNetworkType.WPA, networkInfo.getNetType());
        final WpaNetworkInfo wpaNetworkInfo = (WpaNetworkInfo) networkInfo;
        assertEquals(TestUtil.quote("foobar"), wpaNetworkInfo.getPassword());
    }

    public void testWpa06() {
        final String input = "# EAP-TTLS/EAP-MD5-Challenge configuration with anonymous identity for the\n" +
                "# unencrypted use. Real identity is sent only within an encrypted TLS tunnel.\n" +
                "network={\n" +
                "\tssid=\"example\"\n" +
                "\tkey_mgmt=WPA-EAP\n" +
                "\teap=TTLS\n" +
                "\tidentity=\"user@example.com\"\n" +
                "\tanonymous_identity=\"anonymous@example.com\"\n" +
                "\tpassword=\"foobar\"\n" +
                "\tca_cert=\"/etc/cert/ca.pem\"\n" +
                "\tpriority=2\n" +
                "}";

        final WifiNetworkInfo networkInfo = parser.parseNetworkBlock(input);
        assertNotNull(networkInfo);
        assertEquals(TestUtil.quote("example"), networkInfo.getSsid());

        assertEquals(WifiNetworkType.WPA, networkInfo.getNetType());
        final WpaNetworkInfo wpaNetworkInfo = (WpaNetworkInfo) networkInfo;
        assertEquals(TestUtil.quote("foobar"), wpaNetworkInfo.getPassword());
    }

    public void testWpa07() {
        final String input = "# EAP-TTLS/MSCHAPv2 configuration with anonymous identity for the unencrypted\n" +
                "# use. Real identity is sent only within an encrypted TLS tunnel.\n" +
                "network={\n" +
                "\tssid=\"example\"\n" +
                "\tkey_mgmt=WPA-EAP\n" +
                "\teap=TTLS\n" +
                "\tidentity=\"user@example.com\"\n" +
                "\tanonymous_identity=\"anonymous@example.com\"\n" +
                "\tpassword=\"foobar\"\n" +
                "\tca_cert=\"/etc/cert/ca.pem\"\n" +
                "\tphase2=\"auth=MSCHAPV2\"\n" +
                "}";

        final WifiNetworkInfo networkInfo = parser.parseNetworkBlock(input);
        assertNotNull(networkInfo);
        assertEquals(TestUtil.quote("example"), networkInfo.getSsid());

        assertEquals(WifiNetworkType.WPA, networkInfo.getNetType());
        final WpaNetworkInfo wpaNetworkInfo = (WpaNetworkInfo) networkInfo;
        assertEquals(TestUtil.quote("foobar"), wpaNetworkInfo.getPassword());
    }

    public void testWpa08() {
        final String input = "# WPA-EAP, EAP-TTLS with different CA certificate used for outer and inner\n" +
                "# authentication.\n" +
                "network={\n" +
                "\tssid=\"example\"\n" +
                "\tkey_mgmt=WPA-EAP\n" +
                "\teap=TTLS\n" +
                "\t# Phase1 / outer authentication\n" +
                "\tanonymous_identity=\"anonymous@example.com\"\n" +
                "\tca_cert=\"/etc/cert/ca.pem\"\n" +
                "\t# Phase 2 / inner authentication\n" +
                "\tphase2=\"autheap=TLS\"\n" +
                "\tca_cert2=\"/etc/cert/ca2.pem\"\n" +
                "\tclient_cert2=\"/etc/cer/user.pem\"\n" +
                "\tprivate_key2=\"/etc/cer/user.prv\"\n" +
                "\tprivate_key2_passwd=\"password\"\n" +
                "\tpriority=2\n" +
                "}";

        final WifiNetworkInfo networkInfo = parser.parseNetworkBlock(input);
        assertNotNull(networkInfo);
        assertEquals(TestUtil.quote("example"), networkInfo.getSsid());

        assertEquals(WifiNetworkType.WPA, networkInfo.getNetType());
        final WpaNetworkInfo wpaNetworkInfo = (WpaNetworkInfo) networkInfo;
        assertEquals(null, wpaNetworkInfo.getPassword());
    }

    public void testWpa09() {
        final String input = "# Both WPA-PSK and WPA-EAP is accepted. Only CCMP is accepted as pairwise and\n" +
                "# group cipher.\n" +
                "network={\n" +
                "\tssid=\"example\"\n" +
                "\tbssid=00:11:22:33:44:55\n" +
                "\tproto=WPA RSN\n" +
                "\tkey_mgmt=WPA-PSK WPA-EAP\n" +
                "\tpairwise=CCMP\n" +
                "\tgroup=CCMP\n" +
                "\tpsk=06b4be19da289f475aa46a33cb793029d4ab3db7a23ee92382eb0106c72ac7bb\n" +
                "}";

        final WifiNetworkInfo networkInfo = parser.parseNetworkBlock(input);
        assertNotNull(networkInfo);
        assertEquals(TestUtil.quote("example"), networkInfo.getSsid());

        assertEquals(WifiNetworkType.WPA, networkInfo.getNetType());
        final WpaNetworkInfo wpaNetworkInfo = (WpaNetworkInfo) networkInfo;
        assertEquals("06b4be19da289f475aa46a33cb793029d4ab3db7a23ee92382eb0106c72ac7bb", wpaNetworkInfo.getPassword());
    }

    public void testWpa10() {
        final String input = "# Special characters in SSID, so use hex string. Default to WPA-PSK, WPA-EAP\n" +
                "# and all valid ciphers.\n" +
                "network={\n" +
                "\tssid=00010203\n" +
                "\tpsk=000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f\n" +
                "}";

        final WifiNetworkInfo networkInfo = parser.parseNetworkBlock(input);
        assertNotNull(networkInfo);
        assertEquals("00010203", networkInfo.getSsid());

        assertEquals(WifiNetworkType.WPA, networkInfo.getNetType());
        final WpaNetworkInfo wpaNetworkInfo = (WpaNetworkInfo) networkInfo;
        assertEquals("000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f", wpaNetworkInfo.getPassword());
    }

    public void testWpa11() {
        final String input = "# EAP-SIM with a GSM SIM or USIM\n" +
                "network={\n" +
                "\tssid=\"eap-sim-test\"\n" +
                "\tkey_mgmt=WPA-EAP\n" +
                "\teap=SIM\n" +
                "\tpin=\"1234\"\n" +
                "\tpcsc=\"\"\n" +
                "}";

        final WifiNetworkInfo networkInfo = parser.parseNetworkBlock(input);
        assertNotNull(networkInfo);
        assertEquals(TestUtil.quote("eap-sim-test"), networkInfo.getSsid());

        assertEquals(WifiNetworkType.WPA, networkInfo.getNetType());
        final WpaNetworkInfo wpaNetworkInfo = (WpaNetworkInfo) networkInfo;
        assertEquals(TestUtil.quote("1234"), wpaNetworkInfo.getPassword());
    }

    public void testWpa12() {
        final String input = "# EAP-PSK\n" +
                "network={\n" +
                "\tssid=\"eap-psk-test\"\n" +
                "\tkey_mgmt=WPA-EAP\n" +
                "\teap=PSK\n" +
                "\tidentity=\"eap_psk_user\"\n" +
                "\teappsk=06b4be19da289f475aa46a33cb793029\n" +
                "\tnai=\"eap_psk_user@example.com\"\n" +
                "\tserver_nai=\"as@example.com\"\n" +
                "}";

        final WifiNetworkInfo networkInfo = parser.parseNetworkBlock(input);
        assertNotNull(networkInfo);
        assertEquals(TestUtil.quote("eap-psk-test"), networkInfo.getSsid());

        assertEquals(WifiNetworkType.WPA, networkInfo.getNetType());
        final WpaNetworkInfo wpaNetworkInfo = (WpaNetworkInfo) networkInfo;
        assertEquals("06b4be19da289f475aa46a33cb793029", wpaNetworkInfo.getPassword());
    }

    public void testWpa13() {
        final String input = "# IEEE 802.1X/EAPOL with dynamically generated WEP keys (i.e., no WPA) using\n" +
                "# EAP-TLS for authentication and key generation; require both unicast and\n" +
                "# broadcast WEP keys.\n" +
                "network={\n" +
                "\tssid=\"1x-test\"\n" +
                "\tkey_mgmt=IEEE8021X\n" +
                "\teap=TLS\n" +
                "\tidentity=\"user@example.com\"\n" +
                "\tca_cert=\"/etc/cert/ca.pem\"\n" +
                "\tclient_cert=\"/etc/cert/user.pem\"\n" +
                "\tprivate_key=\"/etc/cert/user.prv\"\n" +
                "\tprivate_key_passwd=\"password\"\n" +
                "\teapol_flags=3\n" +
                "}";

        final WifiNetworkInfo networkInfo = parser.parseNetworkBlock(input);
        assertNotNull(networkInfo);
        assertEquals(TestUtil.quote("1x-test"), networkInfo.getSsid());

        assertEquals(WifiNetworkType.WPA, networkInfo.getNetType());
        final WpaNetworkInfo wpaNetworkInfo = (WpaNetworkInfo) networkInfo;
        assertEquals(null, wpaNetworkInfo.getPassword());
    }

    public void testWpa14() {
        final String input = "# EAP-FAST with WPA (WPA or WPA2)\n" +
                "network={\n" +
                "\tssid=\"eap-fast-test\"\n" +
                "\tkey_mgmt=WPA-EAP\n" +
                "\teap=FAST\n" +
                "\tanonymous_identity=\"FAST-000102030405\"\n" +
                "\tidentity=\"username\"\n" +
                "\tpassword=\"password\"\n" +
                "\tphase1=\"fast_provisioning=1\"\n" +
                "\tpac_file=\"/etc/wpa_supplicant.eap-fast-pac\"\n" +
                "}";

        final WifiNetworkInfo networkInfo = parser.parseNetworkBlock(input);
        assertNotNull(networkInfo);
        assertEquals(TestUtil.quote("eap-fast-test"), networkInfo.getSsid());

        assertEquals(WifiNetworkType.WPA, networkInfo.getNetType());
        final WpaNetworkInfo wpaNetworkInfo = (WpaNetworkInfo) networkInfo;
        assertEquals(TestUtil.quote("password"), wpaNetworkInfo.getPassword());
    }

    public void testWpa15() {
        final String input = "# IBSS/ad-hoc network with WPA-None/TKIP.\n" +
                "network={\n" +
                "\tssid=\"test adhoc\"\n" +
                "\tmode=1\n" +
                "\tproto=WPA\n" +
                "\tkey_mgmt=WPA-NONE\n" +
                "\tpairwise=NONE\n" +
                "\tgroup=TKIP\n" +
                "\tpsk=\"secret passphrase\"\n" +
                "}";

        final WifiNetworkInfo networkInfo = parser.parseNetworkBlock(input);
        assertNotNull(networkInfo);
        assertEquals(TestUtil.quote("test adhoc"), networkInfo.getSsid());

        assertEquals(WifiNetworkType.WPA, networkInfo.getNetType());
        final WpaNetworkInfo wpaNetworkInfo = (WpaNetworkInfo) networkInfo;
        assertEquals(TestUtil.quote("secret passphrase"), wpaNetworkInfo.getPassword());
    }

    public void testWpa16() {
        final String input = "network={\n" +
                "\tssid=\"XSF-LSI-PRIVADA\"\n" +
                "\tscan_ssid=1\n" +
                "\tkey_mgmt=WPA-EAP\n" +
                "\tpairwise=CCMP TKIP\n" +
                "\tgroup=CCMP TKIP WEP104 WEP40\n" +
                "\teap=TTLS TLS\n" +
                "\tidentity=\"PONED_VUESTRO_USERNAME_AQUI\"\n" +
                "\tca_cert=\"/etc/certificates/calclsi.pem\"\n" +
                "\tclient_cert=\"/etc/certificates/personal.pem\"\n" +
                "\tprivate_key=\"/etc/certificates/personal.pem\"\n" +
                "\tprivate_key_passwd=\"cl13nt3\"\n" +
                "}";

        final WifiNetworkInfo networkInfo = parser.parseNetworkBlock(input);
        assertNotNull(networkInfo);
        assertEquals(TestUtil.quote("XSF-LSI-PRIVADA"), networkInfo.getSsid());

        assertEquals(WifiNetworkType.WPA, networkInfo.getNetType());
        final WpaNetworkInfo wpaNetworkInfo = (WpaNetworkInfo) networkInfo;
        assertEquals(null, wpaNetworkInfo.getPassword());
    }
}