package aws.apps.wifiKeyRecovery.util;

import android.os.Bundle;

import java.io.Serializable;

public final class Bundler {

    public static void add(final Bundle bundle,
                           final String key,
                           final Serializable serializable) {
        bundle.putSerializable(key, serializable);
    }

    public static Serializable get(final Bundle bundle,
                                   final String key) {
        return bundle.getSerializable(key);
    }
}
