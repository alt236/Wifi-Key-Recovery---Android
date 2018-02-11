package aws.apps.wifiKeyRecovery.util.phonedetection;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

public class PhoneManufacturerDetector {

    @NonNull
    public Manufacturer getPhoneManufacturer() {
        return getGetPhoneManufacturer(Build.MANUFACTURER);
    }

    public boolean isManufacturedBy(Manufacturer manufacturer) {
        return getPhoneManufacturer() == manufacturer;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    protected Manufacturer getGetPhoneManufacturer(final String manufacturerString) {
        return Manufacturer.fromString(manufacturerString);
    }

}
