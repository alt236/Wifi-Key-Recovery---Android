package aws.apps.wifiKeyRecovery.components.main;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.widget.ImageView;
import android.widget.Toast;

import aws.apps.wifiKeyRecovery.R;
import aws.apps.wifiKeyRecovery.components.common.navigation.IntentDispatcher;
import aws.apps.wifiKeyRecovery.components.common.recyclerview.BaseViewBinder;
import uk.co.alt236.wpasupplicantparser.container.WepNetworkInfo;
import uk.co.alt236.wpasupplicantparser.container.WifiNetworkInfo;
import uk.co.alt236.wpasupplicantparser.container.WifiProtectedNetworkInfo;
import uk.co.alt236.wpasupplicantparser.container.WpaNetworkInfo;
import uk.co.alt236.wpasupplicantparser.util.TextUtils;

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
        holder.setShowQrCodeClickListener(null);
    }

    @Override
    public void setData(final WifiNetworkViewHolder holder, final WifiNetworkInfo item) {

        holder.getSsid().setText(item.getSsid());
        holder.getAdditional().setText(formatAdditionalInfo(item));
        holder.getNetworkType().setText(getNetworkType(item));
        setIcon(holder.getIcon(), item);

        holder.setShowQrCodeClickListener(v -> getIntentDispatcher().openDetails(item));

        boolean hasPassword = hasPassword(item);
        holder.setCopyButtonVisible(hasPassword);
        if (hasPassword) {
            final WifiProtectedNetworkInfo protectedNetwork = (WifiProtectedNetworkInfo) item;
            holder.setCopyPasswordClickListener(v -> copyStringToClipboard(protectedNetwork.getPassword()));
        }
    }

    private boolean hasPassword(final WifiNetworkInfo netInfo) {
        return (netInfo instanceof WifiProtectedNetworkInfo)
                && !TextUtils.isEmpty(((WifiProtectedNetworkInfo) netInfo).getPassword());
    }

    private String getNetworkType(final WifiNetworkInfo netInfo) {
        switch (netInfo.getNetType()) {
            case NO_ENCRYPTION:
                return "Open";
            case WEP:
                return "WEP";
            case WPA:
                return "WPA";
            case UNKNOWN:
            default:
                return "Unknown";
        }
    }

    private void setIcon(final ImageView imageView, final WifiNetworkInfo netInfo) {
        if (netInfo instanceof WifiProtectedNetworkInfo) {
            imageView.setImageResource(R.drawable.ic_list_wifi_protected);
        } else {
            imageView.setImageResource(R.drawable.ic_list_wifi_open);
        }

        final int color;
        switch (netInfo.getNetType()) {
            case NO_ENCRYPTION:
                color = COLOR_RED;
                break;
            case WEP:
                color = COLOR_ORANGE;
                break;
            case WPA:
                color = COLOR_GREEN;
                break;
            case UNKNOWN:
            default:
                color = COLOR_RED;
                break;
        }

        imageView.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
    }

    private void copyStringToClipboard(final String text) {
        final ClipboardManager clipMan = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);

        if (text.length() > 0 && clipMan != null) {
            clipMan.setPrimaryClip(ClipData.newPlainText("wifi_password", text));

            final String msgtext;
            if (text.length() > 150) {
                msgtext = text.substring(0, 150) + "...";
            } else {
                msgtext = text;
            }

            final String message = "'" + msgtext + "' " + getContext().getString(R.string.text_copied);
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private static String formatAdditionalInfo(final WifiNetworkInfo netInfo) {
        final StringBuilder sb = new StringBuilder();
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