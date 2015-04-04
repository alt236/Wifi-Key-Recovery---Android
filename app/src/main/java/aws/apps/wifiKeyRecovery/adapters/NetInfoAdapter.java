/*******************************************************************************
 * Copyright 2011 Alexandros Schillings
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package aws.apps.wifiKeyRecovery.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import aws.apps.wifiKeyRecovery.R;
import uk.co.alt236.wifipasswordaccess.WifiNetworkInfo;

public class NetInfoAdapter extends BaseAdapter implements Filterable {

    final String TAG = this.getClass().getName();
    private final Map<String, Integer> mAlphaIndexer;
    private final Context mContext;
    private List<WifiNetworkInfo> mAllItems;
    private List<WifiNetworkInfo> mSubItems;
    private String[] mSections;
    private Filter mFilter;

    public NetInfoAdapter(Context context, List<WifiNetworkInfo> appsList) {
        super();

        mSubItems = appsList;
        mAllItems = this.mSubItems;

        mContext = context;
        mAlphaIndexer = new HashMap<String, Integer>();

        prepareIndexer();
    }

    @Override
    public int getCount() {
        return mSubItems.size();
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ProoferFilter();
        }
        return mFilter;
    }

    @Override
    public WifiNetworkInfo getItem(int position) {
        return mSubItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getPositionForSection(int section) {
        return mAlphaIndexer.get(mSections[section]);
    }

    public int getSectionForPosition(int position) {
        return 0;
    }

    public Object[] getSections() {
        return mSections;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final WifiNetworkInfo netInfo = mSubItems.get(position);

        if (convertView == null) {
            final LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_item_network_info, null);
        }

        if (netInfo != null) {
            final TextView text = (TextView) convertView.findViewById(R.id.text);

            if (position % 2 == 0) {
                convertView.setBackgroundResource(R.drawable.row_background_alt);
            } else {
                convertView.setBackgroundResource(R.drawable.row_background);
            }

            text.setText(netInfo.getDisplayedString());
            convertView.setTag(netInfo);
        }

        return convertView;
    }

    private void prepareIndexer() {
        int size = mSubItems.size();
        String title;
        String c;

        for (int i = size - 1; i >= 0; i--) {
            title = mSubItems.get(i).getQrSsid();

            try {
                Integer.valueOf(title.substring(0, 1));
                c = "#";
            } catch (NumberFormatException e) {
                c = title.toUpperCase(Locale.US).substring(0, 1);
            }

            mAlphaIndexer.put(c, i);
        }

        final Set<String> keys = mAlphaIndexer.keySet();
        final Iterator<String> it = keys.iterator();
        final List<String> keyList = new ArrayList<String>();

        while (it.hasNext()) {
            keyList.add(it.next());
        }

        Collections.sort(keyList);

        mSections = new String[keyList.size()];
        keyList.toArray(mSections);
    }

    /**
     * Custom Filter implementation for the items adapter.
     */
    private class ProoferFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence filterString) {
            // NOTE: this function is *always* called from a background thread,
            // and
            // not the UI thread.

            final FilterResults results = new FilterResults();
            final List<WifiNetworkInfo> i = new ArrayList<WifiNetworkInfo>();

            if (filterString != null && filterString.toString().length() > 0) {

                for (int index = 0; index < mAllItems.size(); index++) {
                    final WifiNetworkInfo item = mAllItems.get(index);
                    if (item.getQrSsid().toLowerCase(Locale.US).contains(filterString.toString().toLowerCase())) {
                        i.add(item);
                    }

                }
                results.values = i;
                results.count = i.size();
            } else {
                synchronized (mAllItems) {
                    results.values = mAllItems;
                    results.count = mAllItems.size();
                }
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence prefix, FilterResults results) {

            // NOTE: this function is *always* called from the UI thread.
            mSubItems = (ArrayList<WifiNetworkInfo>) results.values;

            notifyDataSetChanged();
        }
    }

}
