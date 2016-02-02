package aws.apps.wifiKeyRecovery.activities.main;

import android.content.Context;
import android.view.Menu;

import aws.apps.wifiKeyRecovery.R;

/*package*/ final class PopupMenuActionHelper {
    public static final int ACTION_ID_NETWORK_COPY_ALL_AS_TEXT = 20001;
    public static final int ACTION_ID_NETWORK_COPY_PASSWORD = 20002;
    public static final int ACTION_ID_NETWORK_SHOW_QRCODE = 20003;

    private PopupMenuActionHelper() {
    }

    public static void addCopyAll(final Context activity,
                                  final IconFriendlyPopupMenu menu) {
        menu.getMenu().add(
                Menu.NONE,
                ACTION_ID_NETWORK_COPY_ALL_AS_TEXT,
                Menu.NONE,
                R.string.label_copy_all)
                .setIcon(R.drawable.ic_list_copy2);
    }

    public static void addCopyPassword(final Context activity,
                                       final IconFriendlyPopupMenu menu) {
        menu.getMenu().add(
                Menu.NONE,
                ACTION_ID_NETWORK_COPY_PASSWORD,
                Menu.NONE,
                R.string.label_copy_password)
                .setIcon(R.drawable.ic_list_copy);
    }

    public static void addShowQrCode(final Context activity,
                                     final IconFriendlyPopupMenu menu) {
        menu.getMenu().add(
                Menu.NONE,
                ACTION_ID_NETWORK_SHOW_QRCODE,
                Menu.NONE,
                R.string.label_show_qr_code)
                .setIcon(R.drawable.ic_list_barcode);
    }

}
