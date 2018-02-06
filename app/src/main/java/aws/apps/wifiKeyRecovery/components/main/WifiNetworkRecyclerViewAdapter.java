package aws.apps.wifiKeyRecovery.components.main;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import aws.apps.wifiKeyRecovery.components.common.navigation.IntentDispatcher;
import aws.apps.wifiKeyRecovery.components.common.recyclerview.BaseRecyclerViewAdapter;
import uk.co.alt236.wpasupplicantparser.container.WifiNetworkInfo;

class WifiNetworkRecyclerViewAdapter extends BaseRecyclerViewAdapter<WifiNetworkInfo, WifiNetworkViewHolder> {
    private final WifiNetworkViewHolderBinder mBinder;

    public WifiNetworkRecyclerViewAdapter(final Activity activity,
                                          final IntentDispatcher intentDispatcher) {
        super(intentDispatcher);
        mBinder = new WifiNetworkViewHolderBinder(activity, getIntentDispatcher());
    }

    @Override
    protected boolean isItemValid(final WifiNetworkInfo item) {
        return true;
    }

    @Override
    public void onBindViewHolder(final WifiNetworkViewHolder holder, final int position) {
        mBinder.bind(holder, getItem(position));
    }

    @Override
    public WifiNetworkViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final int layoutId = WifiNetworkViewHolder.getLayoutId();
        final View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new WifiNetworkViewHolder(view);
    }
}