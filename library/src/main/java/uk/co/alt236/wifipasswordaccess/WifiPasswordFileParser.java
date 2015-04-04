package uk.co.alt236.wifipasswordaccess;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by alex on 04/04/15.
 */
public class WifiPasswordFileParser {
    private final static int RESULT_TITLE_LENGTH = 14;
    private final static String COMMENT_LINE_PREFIX = "#";
    private final static String WIFI_BLOCK_START = "network={";
    private final static String WIFI_BLOCK_END = "}";
    private final String TAG = getClass().getName();

    private final WifiNetworkInfo parseBlock(final String block){
        final String blockLines[] = getSanitizedBlockLines(block);

        if (validateBlock(blockLines)) {
            final Map<String, String> passKeys = new HashMap<>();
            final Map<String, String> settings = new HashMap<>();

            int type = -1;

            String ssid = "";
            String password = ""; // only one, for the qr code;

            for (final String blockLine : blockLines) {
               final String trimmedBlockLine = blockLine.trim();

                if (trimmedBlockLine.startsWith("ssid=")) {
                    ssid = trimmedBlockLine.replace("ssid=", "");

                    // Network Keys:
                } else if (trimmedBlockLine.startsWith("psk=")) {
                    passKeys.put("psk", trimmedBlockLine.replace("psk=", ""));
                    password = trimmedBlockLine.replace("psk=", "");
                    type = WifiNetworkInfo.TYPE_WPA;
                } else if (trimmedBlockLine.startsWith("wep_key0=")) {
                    passKeys.put("WEP Key 0", trimmedBlockLine.replace("wep_key0=", ""));
                    password = trimmedBlockLine.replace("psk=", "");
                    type = WifiNetworkInfo.TYPE_WEP;
                } else if (trimmedBlockLine.startsWith("wep_key1=")) {
                    passKeys.put("WEP Key 1", trimmedBlockLine.replace("wep_key1=", ""));
                } else if (trimmedBlockLine.startsWith("wep_key2=")) {
                    passKeys.put("WEP Key 2", trimmedBlockLine.replace("wep_key2=", ""));
                } else if (trimmedBlockLine.startsWith("wep_key3=")) {
                    passKeys.put("WEP Key 3", trimmedBlockLine.replace("wep_key3=", ""));
                } else if (trimmedBlockLine.startsWith("password=")) {
                    passKeys.put("Password", trimmedBlockLine.replace("password=", ""));
                    password = trimmedBlockLine.replace("psk=", "");

                    // Settings:
                } else if (trimmedBlockLine.startsWith("key_mgmt=")) {
                    settings.put("Key MGMT", trimmedBlockLine.replace("key_mgmt=", ""));
                } else if (trimmedBlockLine.startsWith("group=")) {
                    settings.put("Group", trimmedBlockLine.replace("group=", ""));
                } else if (trimmedBlockLine.startsWith("auth_alg=")) {
                    settings.put("Algorithm", trimmedBlockLine.replace("auth_alg=", ""));
                } else if (trimmedBlockLine.startsWith("eap=")) {
                    settings.put("EAP", trimmedBlockLine.replace("eap=", ""));
                } else if (trimmedBlockLine.startsWith("identity=")) {
                    settings.put("Identity", trimmedBlockLine.replace("identity=", ""));
                } else if (trimmedBlockLine.startsWith("anonymous_identity=")) {
                    settings.put("Anonymous ID", trimmedBlockLine.replace("anonymous_identity=", ""));
                } else if (trimmedBlockLine.startsWith("phase2=")) {
                    settings.put("Phase2 Auth", trimmedBlockLine.replace("phase2=", ""));
                }
            }

            String result = "";

            if (!passKeys.isEmpty()) {
                if (ssid.length() > 0) {
                    result += Util.appendBlanks("SSID:", RESULT_TITLE_LENGTH) + ssid + "\n";
                }

                Iterator<Map.Entry<String, String>> it = passKeys.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> pairs = it.next();
                    result += Util.appendBlanks(pairs.getKey() + ":", RESULT_TITLE_LENGTH) + pairs.getValue() + "\n";
                }

                it = settings.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> pairs = it.next();
                    result += Util.appendBlanks(pairs.getKey() + ":", RESULT_TITLE_LENGTH) + pairs.getValue() + "\n";
                }

            }

            if (result.trim().length() > 0) {
               return new WifiNetworkInfo(result.trim(), ssid, password, type);
            }
        } else {
            Log.d(TAG, "BLOCKPARSER: Invalid Block! " + block);
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
            final WifiNetworkInfo item = parseBlock(passwordBlock.trim());

            if (item != null) {
                methodResult.add(item);
            }
        }

        Log.d(TAG, "FILEPARSER: Parsed Items: " + methodResult.size());
        return methodResult;
    }

    private boolean validateBlock(final String blockLines[]){
        if(blockLines.length < 1){return false;}

        final boolean firstLineOk = WIFI_BLOCK_START.equals(blockLines[0]);
        final boolean lastLineOk = WIFI_BLOCK_END.equals(blockLines[blockLines.length -1]);

        return firstLineOk && lastLineOk;
    }

    private static String[] getSanitizedBlockLines(final String block){
        final List<String> lines = new ArrayList<>();
        final String blockLines[] = block.trim().split("\n");
        for(final String line : blockLines){
            final String trimmedLine = line.trim();
            if(!trimmedLine.startsWith(COMMENT_LINE_PREFIX)){
                lines.add(trimmedLine);
            }
        }

        return lines.toArray(new String[lines.size()]);
    }
}
