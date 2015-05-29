package aws.apps.wifiKeyRecovery.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import aws.apps.wifiKeyRecovery.R;
import aws.apps.wifiKeyRecovery.fragments.WifiDetailsFragment;
import uk.co.alt236.wifipasswordaccess.container.WifiNetworkInfo;

public class WifiDetailsActivity extends ActionBarActivity {
    public final static String EXTRAS_NETWORK_INFO = "aws.apps.wifiKeyRecovery.activities.EXTRAS_NETWORK_INFO";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_details);
        if (savedInstanceState == null) {
            final Fragment fragment = WifiDetailsFragment.getInstance(
                    getIntent().getExtras().<WifiNetworkInfo>getParcelable(EXTRAS_NETWORK_INFO));
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_wifi_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
