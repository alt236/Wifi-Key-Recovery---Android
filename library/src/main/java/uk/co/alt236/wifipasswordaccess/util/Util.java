package uk.co.alt236.wifipasswordaccess.util;

import android.text.TextUtils;

/**
 * Created by alex on 04/04/15.
 */
final class Util {
    private static final String DOUBLE_QUOTE = "\"";

    private Util() {
    }

    public static String appendBlanks(final String text, final int size) {
        String res = text.trim();

        if (res.length() < size) {
            final int change = size - res.length();

            for (int i = 0; i < change; i++) {
                res += " ";
            }

            return res;
        } else {
            return res;
        }
    }

    public static String stripLeadingAndTrailingQuotes(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }

        if (str.startsWith(DOUBLE_QUOTE)) {
            str = str.substring(1, str.length());
        }

        if (str.endsWith(DOUBLE_QUOTE)) {
            str = str.substring(0, str.length() - 1);
        }

        return str;
    }
}
