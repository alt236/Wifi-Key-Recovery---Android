package uk.co.alt236.wifipasswordaccess.util;

import android.text.TextUtils;

import uk.co.alt236.wifipasswordaccess.WifiNetworkType;
import uk.co.alt236.wifipasswordaccess.container.WifiNetworkInfo;
import uk.co.alt236.wifipasswordaccess.container.WifiProtectedNetworkInfo;


/**
 * Created by alex on 05/04/15.
 */
public final class QrCodeUtils {

    private QrCodeUtils() {
    }

    private static String getNetworkTypeAsString(final WifiNetworkType netType) {
        final String result;

        switch (netType) {
            case WEP:
                result = "WEP";
                break;
            case WPA:
                result = "WPA";
                break;
            default:
                result = "nopass";
        }

        return result;
    }

    public static String getQrCodeString(final WifiNetworkInfo networkInfo) {
        if (TextUtils.isEmpty(networkInfo.getSsid())) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();
        sb.append("WIFI:");
        sb.append("S:" + networkInfo.getSsid() + ";");
        sb.append("T:" + getNetworkTypeAsString(networkInfo.getNetType()) + ";");

        if (networkInfo instanceof WifiProtectedNetworkInfo) {
            if (!TextUtils.isEmpty(((WifiProtectedNetworkInfo) networkInfo).getPassword())) {
                sb.append("P:" + ((WifiProtectedNetworkInfo) networkInfo).getPassword() + ";");
            }
        }

        sb.append(";");
        return sb.toString();
    }
}
