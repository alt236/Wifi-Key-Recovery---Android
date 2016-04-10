package aws.apps.wifiKeyRecovery.components.common.recyclerview;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import aws.apps.wifiKeyRecovery.components.common.navigation.IntentDispatcher;

public abstract class BaseRecyclerViewAdapter<D, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private final IntentDispatcher mIntentDispatcher;
    private final List<D> mItems = new ArrayList<>();
    private boolean dataAdditionAttempted = false;

    protected BaseRecyclerViewAdapter(final IntentDispatcher intentDispatcher) {
        mIntentDispatcher = intentDispatcher;
    }

    public void addItem(final D item, final int position) {
        dataAdditionAttempted = true;
        if (isItemValid(item)) {
            mItems.add(item);
            this.notifyItemInserted(position);
        }
    }

    public void addItem(final D item) {
        addItem(item, mItems.size());
    }

    public void addItems(final Collection<D> items) {
        dataAdditionAttempted = true;
        for (final D item : items) {
            addItem(item);
        }
    }

    public void clearItems() {
        mItems.clear();
        notifyDataSetChanged();
    }

    protected IntentDispatcher getIntentDispatcher() {
        return mIntentDispatcher;
    }

    public D getItem(final int position) {
        return mItems.get(position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public boolean hasAttemptedDataAddition() {
        return dataAdditionAttempted;
    }

    protected abstract boolean isItemValid(final D item);

    public void removeItem(final int position) {
        mItems.remove(position);
        this.notifyItemRemoved(position);
    }

    public void removeItem(final D item) {
        final int position = mItems.indexOf(item);
        if (position > -1) {
            removeItem(position);
        }
    }

    public void setItems(final List<D> items) {
        final List<D> filteredList = new ArrayList<>();

        for (final D item : items) {
            if (isItemValid(item)) {
                filteredList.add(item);
            }
        }

        dataAdditionAttempted = true;
        mItems.clear();
        mItems.addAll(filteredList);
        notifyDataSetChanged();
    }
}