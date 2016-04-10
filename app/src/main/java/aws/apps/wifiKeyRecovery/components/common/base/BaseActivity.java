package aws.apps.wifiKeyRecovery.components.common.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import aws.apps.wifiKeyRecovery.components.common.navigation.IntentDispatcher;

/**
 *
 */
public class BaseActivity extends AppCompatActivity {

    private IntentDispatcher mIntentDispatcher;

    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        mIntentDispatcher = new IntentDispatcher(this);
    }

    public IntentDispatcher getIntentDispatcher() {
        return mIntentDispatcher;
    }
}
