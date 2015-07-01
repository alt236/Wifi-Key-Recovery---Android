package uk.co.alt236.wifipasswordaccess;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.alt236.wifipasswordaccess.container.WifiNetworkBuilder;
import uk.co.alt236.wifipasswordaccess.container.WifiNetworkInfo;

/**
 * Created by alex on 04/04/15.
 */
public class WifiPasswordFileParser {
    public static final String PREFIX_SSID = "ssid=";
    public static final String PREFIX_PSK = "psk=";
    public static final String PREFIX_PASSWORD = "password=";
    public static final String PREFIX_WEP_KEY0 = "wep_key0=";
    public static final String PREFIX_WEP_KEY1 = "wep_key1=";
    public static final String PREFIX_WEP_KEY2 = "wep_key2=";
    public static final String PREFIX_WEP_KEY3 = "wep_key3=";
    private final static int RESULT_TITLE_LENGTH = 14;
    private final static String COMMENT_LINE_PREFIX = "#";
    private final static String WIFI_BLOCK_START = "network={";
    private final static String WIFI_BLOCK_END = "}";
    private final String TAG = getClass().getName();

    public final WifiNetworkInfo parseNetworkBlock(final String block) {
        final String blockLines[] = getSanitizedBlockLines(block);

        if (validateBlock(blockLines)) {
            final WifiNetworkBuilder builder = new WifiNetworkBuilder();
            // final Map<String, String> passKeys = new HashMap<>();
            final Map<String, String> settings = new HashMap<>();

            WifiNetworkType type = WifiNetworkType.UNKNOWN;

            String ssid = "";
            String password = ""; // only one, for the qr code;

            for (final String blockLine : blockLines) {
                final String trimmedLine = blockLine.trim();

                if (trimmedLine.startsWith(PREFIX_SSID)) {
                    builder.setSsid(removePrefix(trimmedLine, PREFIX_SSID));
                    // Network Keys:
                } else if (trimmedLine.startsWith(PREFIX_PSK)) {
                    builder.setPsk(removePrefix(trimmedLine, PREFIX_PSK));
                } else if (trimmedLine.startsWith(PREFIX_WEP_KEY0)) {
                    builder.setWepPassword(0, removePrefix(trimmedLine, PREFIX_WEP_KEY0));
                } else if (trimmedLine.startsWith(PREFIX_WEP_KEY1)) {
                    builder.setWepPassword(1, removePrefix(trimmedLine, PREFIX_WEP_KEY1));
                } else if (trimmedLine.startsWith(PREFIX_WEP_KEY2)) {
                    builder.setWepPassword(2, removePrefix(trimmedLine, PREFIX_WEP_KEY2));
                } else if (trimmedLine.startsWith(PREFIX_WEP_KEY3)) {
                    builder.setWepPassword(3, removePrefix(trimmedLine, PREFIX_WEP_KEY3));
                } else if (trimmedLine.startsWith(PREFIX_PASSWORD)) {
                    builder.setPassword(removePrefix(trimmedLine, PREFIX_PASSWORD));

                    // Settings:
                } else if (trimmedLine.startsWith("key_mgmt=")) {
                    settings.put("Key MGMT", trimmedLine.replace("key_mgmt=", ""));
                } else if (trimmedLine.startsWith("group=")) {
                    settings.put("Group", trimmedLine.replace("group=", ""));
                } else if (trimmedLine.startsWith("auth_alg=")) {
                    settings.put("Algorithm", trimmedLine.replace("auth_alg=", ""));
                } else if (trimmedLine.startsWith("eap=")) {
                    settings.put("EAP", trimmedLine.replace("eap=", ""));
                } else if (trimmedLine.startsWith("identity=")) {
                    settings.put("Identity", trimmedLine.replace("identity=", ""));
                } else if (trimmedLine.startsWith("anonymous_identity=")) {
                    settings.put("Anonymous ID", trimmedLine.replace("anonymous_identity=", ""));
                } else if (trimmedLine.startsWith("phase2=")) {
                    settings.put("Phase2 Auth", trimmedLine.replace("phase2=", ""));
                }
            }


            return builder.build();
        }

        return null;
    }

    public List<WifiNetworkInfo> parseWifiPasswordFileContents(final String wifiPasswordString) {
        final List<WifiNetworkInfo> methodResult = new ArrayList<>();
        if (TextUtils.isEmpty(wifiPasswordString)) {
            Log.d(TAG, "FILEPARSER: Nothing to parse.");
            return methodResult;
        }

        final String passwordBlocks[] = wifiPasswordString.split("\n\n");

        Log.d(TAG, "FILEPARSER: Blocks: " + passwordBlocks.length);

        for (final String passwordBlock : passwordBlocks) {
            final WifiNetworkInfo item = parseNetworkBlock(passwordBlock.trim());

            if (item != null) {
                methodResult.add(item);
            }
        }

        Log.d(TAG, "FILEPARSER: Parsed Items: " + methodResult.size());
        return methodResult;
    }

    private String removePrefix(final String text, final String prefix) {
        return text.substring(prefix.length());
    }

    private boolean validateBlock(final String blockLines[]) {
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
