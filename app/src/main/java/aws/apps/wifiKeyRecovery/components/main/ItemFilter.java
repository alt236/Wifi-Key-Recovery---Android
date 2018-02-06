package aws.apps.wifiKeyRecovery.components.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import uk.co.alt236.wpasupplicantparser.container.WifiNetworkInfo;

class ItemFilter {
    private List<WifiNetworkInfo> data;

    public void setData(List<WifiNetworkInfo> data) {
        this.data = data;
    }

    public List<WifiNetworkInfo> filter(final String filter) {
        final List<WifiNetworkInfo> retVal = new ArrayList<>();

        for (final WifiNetworkInfo item : safeList(data)) {
            if (isInFilter(item, filter)) {
                retVal.add(item);
            }
        }

        return retVal;
    }

    private boolean isInFilter(final WifiNetworkInfo item, final String filter) {
        return item.getSsid().toLowerCase(Locale.US)
                .contains(filter.toLowerCase(Locale.US));
    }

    private List<WifiNetworkInfo> safeList(final List<WifiNetworkInfo> list) {
        return list == null ? Collections.<WifiNetworkInfo>emptyList() : list;
    }
}
