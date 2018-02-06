package uk.co.alt236.wpasupplicantparser.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.alt236.wpasupplicantparser.container.WifiNetworkBuilder;
import uk.co.alt236.wpasupplicantparser.container.WifiNetworkInfo;
import uk.co.alt236.wpasupplicantparser.util.KeyValueSplitter;
import uk.co.alt236.wpasupplicantparser.util.TextUtils;

public class WpaSupplicantParser {
    private static final String KEY_SSID = "ssid";
    private static final String KEY_PSK = "psk";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_WEP_KEY0 = "wep_key0";
    private static final String KEY_WEP_KEY1 = "wep_key1";
    private static final String KEY_WEP_KEY2 = "wep_key2";
    private static final String KEY_WEP_KEY3 = "wep_key3";
    private static final String KEY_KEY_MGMT = "key_mgmt";
    private static final String KEY_EAP = "eap";

    private final static String COMMENT_LINE_PREFIX = "#";
    private final static String WIFI_BLOCK_START = "network={";
    private final static String WIFI_BLOCK_END = "}";

    @SuppressWarnings("MethodMayBeStatic")
    protected final WifiNetworkInfo parseNetworkBlock(final String block) {
        final String blockLines[] = getSanitizedBlockLines(block);

        final WifiNetworkInfo methodResult;

        if (validateBlock(blockLines)) {
            final KeyValueSplitter splitter = new KeyValueSplitter("=");
            final WifiNetworkBuilder builder = new WifiNetworkBuilder();
            final Map<String, String> settings = new HashMap<>();

            for (final String blockLine : blockLines) {
                final String trimmedLine = blockLine.trim();
                final KeyValueSplitter.KV<String, String> kv = splitter.split(trimmedLine);
                if (kv != null) {
                    settings.put(kv.getKey(), kv.getValue());
                }
            }

            builder.setSsid(settings.get(KEY_SSID));
            builder.setPsk(settings.get(KEY_PSK));
            builder.setWepPassword(0, settings.get(KEY_WEP_KEY0));
            builder.setWepPassword(1, settings.get(KEY_WEP_KEY1));
            builder.setWepPassword(2, settings.get(KEY_WEP_KEY2));
            builder.setWepPassword(3, settings.get(KEY_WEP_KEY3));
            builder.setPassword(settings.get(KEY_PASSWORD));

            builder.setKeyManagement(settings.get(KEY_KEY_MGMT));
            builder.setEap(settings.get(KEY_EAP));

            methodResult = builder.build();
        } else {
            methodResult = null;
        }

        return methodResult;
    }

    public List<WifiNetworkInfo> parseWifiPasswordFileContents(final String wifiPasswordString) {
        final List<WifiNetworkInfo> methodResult = new ArrayList<>();
        if (TextUtils.isEmpty(wifiPasswordString)) {
            return methodResult;
        }

        final String passwordBlocks[] = wifiPasswordString.split("\n\n");

        for (final String passwordBlock : passwordBlocks) {
            final WifiNetworkInfo item = parseNetworkBlock(passwordBlock.trim());

            if (item != null) {
                methodResult.add(item);
            }
        }

        return methodResult;
    }

    private static boolean validateBlock(final String blockLines[]) {
        if (blockLines.length < 1) {
            return false;
        }

        final boolean firstLineOk = WIFI_BLOCK_START.equals(blockLines[0]);
        final boolean lastLineOk = WIFI_BLOCK_END.equals(blockLines[blockLines.length - 1]);

        return firstLineOk && lastLineOk;
    }

    private static String[] getSanitizedBlockLines(final String block) {
        final List<String> lines = new ArrayList<>();
        final String blockLines[] = block.trim().split("\n");
        for (final String line : blockLines) {
            final String trimmedLine = line.trim();
            if (!trimmedLine.startsWith(COMMENT_LINE_PREFIX)) {
                lines.add(trimmedLine);
            }
        }

        return lines.toArray(new String[lines.size()]);
    }
}
