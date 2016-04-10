package aws.apps.wifiKeyRecovery.components.common.recyclerview;


import android.content.Context;
import android.support.v4.content.ContextCompat;

import aws.apps.wifiKeyRecovery.components.common.navigation.IntentDispatcher;

public abstract class BaseViewBinder<VH extends BaseRecyclerViewHolder, D> {
    private final Context mContext;
    private final IntentDispatcher mIntentDispatcher;

    public BaseViewBinder(final Context context, final IntentDispatcher intentDispatcher) {
        mContext = context;
        mIntentDispatcher = intentDispatcher;
    }

    public final void bind(final VH holder, final D item) {
        reset(holder);
        setData(holder, item);
    }

    protected int getColor(final int resId) {
        return ContextCompat.getColor(getContext(), resId);
    }

    protected Context getContext() {
        return mContext;
    }

    protected IntentDispatcher getIntentDispatcher() {
        return mIntentDispatcher;
    }

    protected abstract void reset(final VH holder);

    protected abstract void setData(final VH holder, final D item);
}