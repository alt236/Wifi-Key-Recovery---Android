package aws.apps.wifiKeyRecovery.components.main;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.ImageView;

import aws.apps.wifiKeyRecovery.R;
import aws.apps.wifiKeyRecovery.components.common.navigation.IntentDispatcher;
import aws.apps.wifiKeyRecovery.components.common.recyclerview.BaseViewBinder;
import uk.co.alt236.wpasupplicantparser.container.WepNetworkInfo;
import uk.co.alt236.wpasupplicantparser.container.WifiNetworkInfo;
import uk.co.alt236.wpasupplicantparser.container.WifiProtectedNetworkInfo;
import uk.co.alt236.wpasupplicantparser.container.WpaNetworkInfo;

class WifiNetworkViewHolderBinder extends BaseViewBinder<WifiNetworkViewHolder, WifiNetworkInfo> {
    private static final int COLOR_RED = Color.parseColor("#F44336");
    private static final int COLOR_ORANGE = Color.parseColor("#FFC107");
    private static final int COLOR_GREEN = Color.parseColor("#4CAF50");

    public WifiNetworkViewHolderBinder(final Context context,
                                       final IntentDispatcher intentDispatcher) {
        super(context, intentDispatcher);
    }

    @Override
    protected void reset(final WifiNetworkViewHolder holder) {

    }

    @Override
    public void setData(final WifiNetworkViewHolder holder, final WifiNetworkInfo item) {

        holder.getSsid().setText(item.getSsid());
        holder.getAdditional().setText(formatAdditionalInfo(item));
        setIcon(holder.getIcon(), item);

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                getIntentDispatcher().openDetails(item);
            }
        });
    }

    private static void setIcon(final ImageView imageView, final WifiNetworkInfo netInfo) {
        if (netInfo instanceof WifiProtectedNetworkInfo) {
            imageView.setImageResource(R.drawable.ic_list_wifi_protected);
        } else {
            imageView.setImageResource(R.drawable.ic_list_wifi_open);
        }

        final int color;
        switch (netInfo.getNetType()) {

            case UNKNOWN:
                color = COLOR_RED;
                break;
            case NO_ENCRYPTION:
                color = COLOR_RED;
                break;
            case WEP:
                color = COLOR_ORANGE;
                break;
            case WPA:
                color = COLOR_GREEN;
                break;
            default:
                color = COLOR_RED;
                break;
        }

        imageView.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
    }

    private static String formatAdditionalInfo(final WifiNetworkInfo netInfo) {
        final StringBuilder sb = new StringBuilder();
        sb.append("TYPE: ");
        sb.append(netInfo.getNetType());
        sb.append("\n");

        appendPassword(sb, netInfo);
        return sb.toString();
    }

    private static void appendPassword(final StringBuilder sb, final WifiNetworkInfo networkInfo) {
        if (networkInfo instanceof WifiProtectedNetworkInfo) {
            final WifiProtectedNetworkInfo protectedNetworkInfo = (WifiProtectedNetworkInfo) networkInfo;
            if (protectedNetworkInfo instanceof WpaNetworkInfo) {
                sb.append("Password: ");
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
                sb.append("Password: ");
                sb.append(protectedNetworkInfo.getPassword());
                sb.append("\n");
            }
        } else {
            sb.append("Password: ");
            sb.append("<No Password>");
            sb.append("\n");
        }
    }
}