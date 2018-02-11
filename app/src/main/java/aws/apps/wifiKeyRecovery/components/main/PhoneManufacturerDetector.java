package aws.apps.wifiKeyRecovery.components.main;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

public class PhoneManufacturerDetector {

    @NonNull
    public Manufacturer getPhoneManufacturer() {
        return getGetPhoneManufacturer(Build.MANUFACTURER);
    }

    @NonNull
    public boolean isManufacturedBy(Manufacturer manufacturer) {
        return getPhoneManufacturer() == manufacturer;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    protected Manufacturer getGetPhoneManufacturer(final String manufacturerString) {
        return Manufacturer.fromString(manufacturerString);
    }

    public enum Manufacturer {
        SAMSUNG("Samsung"),
        OTHER("OTHER");

        private final String manufacturer;

        Manufacturer(final String manufacturer) {
            this.manufacturer = manufacturer;
        }

        @NonNull
        private static Manufacturer fromString(final String string) {
            for (final Manufacturer value : Manufacturer.values()) {
                if (value.manufacturer.equalsIgnoreCase(string)) {
                    return value;
                }
            }

            return OTHER;
        }
    }
}
