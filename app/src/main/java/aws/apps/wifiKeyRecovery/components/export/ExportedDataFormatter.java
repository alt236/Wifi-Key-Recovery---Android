package aws.apps.wifiKeyRecovery.components.export;

import android.content.Context;

import java.util.List;

import aws.apps.wifiKeyRecovery.R;
import aws.apps.wifiKeyRecovery.util.WiFiNetworkValitator;
import uk.co.alt236.wifipasswordaccess.container.WepNetworkInfo;
import uk.co.alt236.wifipasswordaccess.container.WifiNetworkInfo;
import uk.co.alt236.wifipasswordaccess.container.WifiProtectedNetworkInfo;
import uk.co.alt236.wifipasswordaccess.container.WpaNetworkInfo;

/**
 *
 */
/*protected*/ class ExportedDataFormatter {

    private final Context context;

    public ExportedDataFormatter(final Context context) {
        this.context = context;
    }

    private static void appendPassword(final StringBuilder sb, final WifiNetworkInfo networkInfo) {
        if (networkInfo instanceof WifiProtectedNetworkInfo) {
            final WifiProtectedNetworkInfo protectedNetworkInfo = (WifiProtectedNetworkInfo) networkInfo;
            if (protectedNetworkInfo instanceof WpaNetworkInfo) {
                sb.append("PASSWORD: ");
                sb.append(protectedNetworkInfo.getPassword());
                sb.append("\n");
            } else if (protectedNetworkInfo instanceof WepNetworkInfo) {
                final WepNetworkInfo wepNetworkInfo = (WepNetworkInfo) protectedNetworkInfo;
                for (int i = 0; i < wepNetworkInfo.getPasswordCount(); i++) {
                    //noinspection StringConcatenationInsideStringBufferAppend
                    sb.append("WEP_KEY_" + i + ": ");
                    sb.append(wepNetworkInfo.getPassword(i));
                    sb.append("\n");
                }
            } else {
                sb.append("PASSWORD: ");
                sb.append(protectedNetworkInfo.getPassword());
                sb.append("\n");
            }
        } else {
            sb.append("PASSWORD: ");
            sb.append("<NO PASSWORD>");
            sb.append("\n");
        }
    }

    public String getString(final List<WifiNetworkInfo> data) {
        return listToString(data);
    }

    public String listToString(final List<WifiNetworkInfo> list) {
        final StringBuilder sb = new StringBuilder();
        final List<WifiNetworkInfo> validList = WiFiNetworkValitator.getValidNetworks(list);
        final int size = validList.size();

        int cnt = 0;

        sb.append(context.getString(R.string.label_wifi_passwords));
        sb.append("\n");

        for (final WifiNetworkInfo networkInfo : validList) {
            cnt += 1;

            sb.append("#");
            sb.append(cnt);
            sb.append(":");
            sb.append("\n");

            sb.append("SSID: ");
            sb.append(networkInfo.getSsid());
            sb.append("\n");

            sb.append("TYPE: ");
            sb.append(networkInfo.getNetType());
            sb.append("\n");

            appendPassword(sb, networkInfo);
            sb.append("\n");
        }

        sb.append("\n");
        sb.append("\n");
        sb.append("Count: ");
        sb.append(size);

        return sb.toString();
    }
}