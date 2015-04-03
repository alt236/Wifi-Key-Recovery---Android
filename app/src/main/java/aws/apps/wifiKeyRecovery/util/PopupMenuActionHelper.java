package aws.apps.wifiKeyRecovery.util;

import android.content.Context;
import android.view.Menu;
import aws.apps.wifiKeyRecovery.R;
import aws.apps.wifiKeyRecovery.ui.IconFriendlyPopupMenu;

public class PopupMenuActionHelper {
	// Proofer Options
	public static final int ACTION_ID_NETWORK_COPY_ALL_AS_TEXT = 20001;
	public static final int ACTION_ID_NETWORK_COPY_PASSWORD = 20002;
	public static final int ACTION_ID_NETWORK_SHOW_QRCODE = 20003;

	public static void addCopyAll(Context mainActivity, IconFriendlyPopupMenu menu) {
		menu.getMenu().add(
				Menu.NONE,
				ACTION_ID_NETWORK_COPY_ALL_AS_TEXT,
				Menu.NONE,
				R.string.label_copy_all)
				.setIcon(R.drawable.ic_list_copy2);
	}

	public static void addCopyPassword(Context mainActivity, IconFriendlyPopupMenu menu) {
		menu.getMenu().add(
				Menu.NONE,
				ACTION_ID_NETWORK_COPY_PASSWORD,
				Menu.NONE,
				R.string.label_copy_password)
				.setIcon(R.drawable.ic_list_copy);
	}

	public static void addShowQrCode(Context mainActivity, IconFriendlyPopupMenu menu) {
		menu.getMenu().add(
				Menu.NONE,
				ACTION_ID_NETWORK_SHOW_QRCODE,
				Menu.NONE,
				R.string.label_show_qr_code)
				.setIcon(R.drawable.ic_list_barcode);
	}

}
