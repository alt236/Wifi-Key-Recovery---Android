package uk.co.alt236.wifipasswordaccess.util;

import android.util.Pair;

/**
 *
 */
public class KeyValueSplitter {
    private final String separator;

    public KeyValueSplitter(final String separator) {
        this.separator = separator;
    }

    public Pair<String, String> split(final String text) {
        final int position = text.indexOf(separator);

        if (position >= 0) {
            return new Pair<>(
                    text.substring(0, position),
                    text.substring(position + 1, text.length()));
        }

        return null;
    }

}
