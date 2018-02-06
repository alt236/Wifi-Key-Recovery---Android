package aws.apps.wifiKeyRecovery.components.common.navigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;

import aws.apps.wifiKeyRecovery.R;
import aws.apps.wifiKeyRecovery.components.details.WifiDetailsActivity;
import aws.apps.wifiKeyRecovery.components.export.ExportActivity;
import uk.co.alt236.wpasupplicantparser.container.WifiNetworkInfo;

/**
 *
 */
public class IntentDispatcher {
    private final Activity activity;

    public IntentDispatcher(final Activity activity) {
        this.activity = activity;
    }

    public void openExportActivity(final ArrayList<WifiNetworkInfo> networks,
                                   final long timestamp) {

        final Intent intent = ExportActivity.createIntent(activity, networks, timestamp);

        startActivity(intent, null);
    }

    public void openDetails(final WifiNetworkInfo item) {
        final Intent intent = WifiDetailsActivity.createIntent(activity, item);

        startActivity(intent, null);
    }

    public void shareText(final String text, final String subject) {
        final Intent intent = new Intent(Intent.ACTION_SEND);


        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        final Intent share = Intent.createChooser(
                intent,
                activity.getString(R.string.label_share_dialogue_title));

        startActivity(share, null);
    }

    private void startActivity(final Intent intent, final Bundle bundle) {
        ActivityCompat.startActivity(activity, intent, bundle);
    }
}
