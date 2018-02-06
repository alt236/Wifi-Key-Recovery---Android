package aws.apps.wifiKeyRecovery.components.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import aws.apps.wifiKeyRecovery.R;
import aws.apps.wifiKeyRecovery.components.common.base.BaseActivity;
import uk.co.alt236.wpasupplicantparser.container.WifiNetworkInfo;

public class WifiDetailsActivity extends BaseActivity {
    private final static String EXTRAS_CONTENT = WifiDetailsActivity.class.getName() + ".EXTRA_CONTENT";

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_wifi_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_details);
        if (savedInstanceState == null) {
            final WifiNetworkInfo info = (WifiNetworkInfo) getIntent().getExtras().getSerializable(EXTRAS_CONTENT);
            final Fragment fragment = WifiDetailsFragment.getInstance(info);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    public static Intent createIntent(final Context context, final WifiNetworkInfo networkInfo) {
        final Intent intent = new Intent(context, WifiDetailsActivity.class);
        intent.putExtra(EXTRAS_CONTENT, networkInfo);

        return intent;
    }
}
