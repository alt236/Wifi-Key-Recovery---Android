package aws.apps.wifiKeyRecovery.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import uk.co.alt236.wpasupplicantparser.container.WifiNetworkInfo;
import uk.co.alt236.wpasupplicantparser.container.WifiProtectedNetworkInfo;

/**
 *
 */
public final class WiFiNetworkValitator {

    private WiFiNetworkValitator() {
        // NOOP
    }

    @NonNull
    public static List<WifiNetworkInfo> getValidNetworks(@Nullable final List<WifiNetworkInfo> networkInfoList) {
        final List<WifiNetworkInfo> retVal = new ArrayList<>();
        if (networkInfoList != null) {
            for (final WifiNetworkInfo networkInfo : networkInfoList) {
                if (isValid(networkInfo)) {
                    retVal.add(networkInfo);
                }
            }
        }
        return retVal;
    }

    private static boolean isValid(@Nullable final WifiNetworkInfo networkInfo) {
        return networkInfo != null
                && networkInfo instanceof WifiProtectedNetworkInfo
                && !TextUtils.isEmpty(((WifiProtectedNetworkInfo) networkInfo).getPassword());
    }
}
