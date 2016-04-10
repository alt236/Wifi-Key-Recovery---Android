package aws.apps.wifiKeyRecovery.components.common.dialogs;

import android.app.Dialog;
import android.content.Context;

import aws.apps.wifiKeyRecovery.BuildConfig;
import aws.apps.wifiKeyRecovery.R;

/**
 *
 */
public final class DialogFactory {

    private DialogFactory() {
    }

    public static Dialog getAboutDialog(final Context context) {
        final String title = context.getString(R.string.app_name) + " v" + BuildConfig.VERSION_NAME;
        final StringBuilder sb = new StringBuilder();

        sb.append(context.getString(R.string.app_changelog));
        sb.append("\n\n");
        sb.append(context.getString(R.string.app_notes));
        sb.append("\n\n");
        sb.append(context.getString(R.string.app_acknowledgements));
        sb.append("\n\n");
        sb.append(context.getString(R.string.app_copyright));

        return CustomTextDialog.create(
                context,
                sb.toString(),
                title,
                context.getString(android.R.string.ok));
    }

    public static Dialog getRootWarning(final Context context,
                                        final Dialog.OnDismissListener listener) {
        final Dialog dlg = CustomTextDialog.create(
                context,
                context.getString(R.string.root_needed),
                context.getString(R.string.app_name),
                context.getString(android.R.string.ok));

        dlg.setOnDismissListener(listener);

        return dlg;
    }
}
