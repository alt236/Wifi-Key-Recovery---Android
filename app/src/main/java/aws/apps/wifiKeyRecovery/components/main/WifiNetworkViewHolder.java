package aws.apps.wifiKeyRecovery.components.main;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import aws.apps.wifiKeyRecovery.R;
import aws.apps.wifiKeyRecovery.components.common.recyclerview.BaseRecyclerViewHolder;

class WifiNetworkViewHolder extends BaseRecyclerViewHolder {
    private static final int ITEM_LAYOUT = R.layout.list_item_network_info;
    private final TextView additional;
    private final ImageView icon;
    private final TextView ssid;
    private final TextView networkType;
    private final View qrCodeButton;
    private final View copyPasswordButton;

    public WifiNetworkViewHolder(final View itemView) {
        super(itemView);
        ssid = getRoot().findViewById(R.id.ssid);
        networkType = getRoot().findViewById(R.id.network_type);
        additional = getRoot().findViewById(R.id.additional);
        icon = getRoot().findViewById(R.id.icon);
        qrCodeButton = getRoot().findViewById(R.id.cta_show_qr_code);
        copyPasswordButton = getRoot().findViewById(R.id.cta_copy_password);
    }

    public TextView getSsid() {
        return ssid;
    }

    public TextView getNetworkType() {
        return networkType;
    }

    public TextView getAdditional() {
        return additional;
    }

    public ImageView getIcon() {
        return icon;
    }

    public void setCopyButtonVisible(final boolean visible) {
        copyPasswordButton.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setShowQrCodeClickListener(final View.OnClickListener listener) {
        qrCodeButton.setOnClickListener(listener);
    }

    public void setCopyPasswordClickListener(final View.OnClickListener listener) {
        copyPasswordButton.setOnClickListener(listener);
    }

    public static int getLayoutId() {
        return ITEM_LAYOUT;
    }
}