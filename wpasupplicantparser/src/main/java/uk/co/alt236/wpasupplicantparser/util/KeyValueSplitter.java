package uk.co.alt236.wpasupplicantparser.util;

public class KeyValueSplitter {
    private final String separator;

    public KeyValueSplitter(final String separator) {
        this.separator = separator;
    }

    public KV<String, String> split(final String text) {
        final int position = text.indexOf(separator);

        if (position >= 0) {
            return new KV<>(
                    text.substring(0, position),
                    text.substring(position + 1, text.length()));
        }

        return null;
    }


    public static class KV<K, V> {
        private final K key;
        private final V value;

        public KV(final K key, final V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }
}
