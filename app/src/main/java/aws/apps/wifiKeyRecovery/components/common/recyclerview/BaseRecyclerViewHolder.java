package aws.apps.wifiKeyRecovery.components.common.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {

    private final View mRoot;

    protected BaseRecyclerViewHolder(final View view) {
        super(view);
        mRoot = view;
    }

    public View getRoot() {
        return mRoot;
    }

    /**
     * Sets an {@link View.OnClickListener} on the root view of this holder
     *
     * @param listener the listener
     */
    public void setOnClickListener(final View.OnClickListener listener) {
        getRoot().setOnClickListener(listener);
        getRoot().setEnabled(listener != null);
    }
}