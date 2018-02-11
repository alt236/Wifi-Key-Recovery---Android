package aws.apps.wifiKeyRecovery.util.phonedetection;

import android.support.annotation.NonNull;

public enum Manufacturer {
    SAMSUNG("Samsung"),
    OTHER("OTHER");

    private final String manufacturer;

    Manufacturer(final String manufacturer) {
        this.manufacturer = manufacturer;
    }

    @NonNull
    static Manufacturer fromString(final String string) {
        for (final Manufacturer value : Manufacturer.values()) {
            if (value.manufacturer.equalsIgnoreCase(string)) {
                return value;
            }
        }

        return OTHER;
    }
}
