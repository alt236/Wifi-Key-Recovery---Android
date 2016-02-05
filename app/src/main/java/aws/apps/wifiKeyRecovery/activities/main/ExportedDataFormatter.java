package aws.apps.wifiKeyRecovery.activities.main;

import android.content.Context;

import java.util.List;

import aws.apps.wifiKeyRecovery.R;
import uk.co.alt236.wifipasswordaccess.container.WifiNetworkInfo;

/**
 *
 */
/*package*/ class ExportedDataFormatter {

    private final Context context;

    public ExportedDataFormatter(final Context context) {
        this.context = context;
    }

    public String getString(final List<WifiNetworkInfo> data) {
        return listToString(data);
    }


    public String listToString(final List<WifiNetworkInfo> list) {
        final StringBuilder sb = new StringBuilder();
        final int size = list.size();

        int cnt = 0;

        sb.append(context.getString(R.string.label_wifi_passwords));
        sb.append("\n");

        for (final WifiNetworkInfo obj : list) {
            cnt += 1;
            sb.append("#");
            sb.append(cnt);
            sb.append(":");
            sb.append("\n");
            sb.append(obj.getSsid());
            sb.append("\n");
        }

        sb.append("\n");
        sb.append("\n");
        sb.append("Count: ");
        sb.append(size);

        return sb.toString();
    }
}
