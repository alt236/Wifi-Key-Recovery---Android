package aws.apps.wifiKeyRecovery.components.main;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import aws.apps.wifiKeyRecovery.R;
import aws.apps.wifiKeyRecovery.components.common.recyclerview.BaseRecyclerViewHolder;

class WifiNetworkViewHolder extends BaseRecyclerViewHolder {
    private static final int ITEM_LAYOUT = R.layout.list_item_network_info;
    final TextView additional;
    final ImageView icon;
    private final TextView ssid;

    public WifiNetworkViewHolder(final View itemView) {
        super(itemView);
        ssid = (TextView) getRoot().findViewById(R.id.ssid);
        additional = (TextView) getRoot().findViewById(R.id.additional);
        icon = (ImageView) getRoot().findViewById(R.id.icon);
    }

    public static int getLayoutId() {
        return ITEM_LAYOUT;
    }

    public TextView getSsid() {
        return ssid;
    }

    public TextView getAdditional() {
        return additional;
    }

    public ImageView getIcon() {
        return icon;
    }
}