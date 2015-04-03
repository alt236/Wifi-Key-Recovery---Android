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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import aws.apps.wifiKeyRecovery.R;
import aws.apps.wifiKeyRecovery.containers.NetInfo;

public class NetInfoAdapter extends BaseAdapter implements Filterable { // implements
																		// SectionIndexer{
	private final Map<String, Integer> mAlphaIndexer;

	private List<NetInfo> mAllItems;
	private List<NetInfo> mSubItems;

	private final Context mContext;
	private String[] mSections;

	private Filter mFilter;

	final String TAG = this.getClass().getName();

	public NetInfoAdapter(Context context, List<NetInfo> appsList) {
		super();

		mSubItems = appsList;
		mAllItems = this.mSubItems;

		mContext = context;
		mAlphaIndexer = new HashMap<String, Integer>();

		prepareIndexer();
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
				c = title.toUpperCase().substring(0, 1);
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

	public int getCount() {
		return mSubItems.size();
	}

	public NetInfo getItem(int position) {
		return mSubItems.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final NetInfo event = mSubItems.get(position);

		if (convertView == null) {
			final LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.list_item, null);
		}

		if (event != null) {
			final TextView text = (TextView) convertView.findViewById(R.id.text);

			if (position % 2 == 0) {
				convertView.setBackgroundResource(R.drawable.rowbg_alt);
			} else {
				convertView.setBackgroundResource(R.drawable.rowbg);
			}

			text.setText(event.toString());
			convertView.setTag(event);
		}

		return convertView;
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

	public Filter getFilter()
	{
		if (mFilter == null) {
			mFilter = new ProoferFilter();
		}
		return mFilter;
	}

	/**
	 * Custom Filter implementation for the items adapter.
	 *
	 */
	private class ProoferFilter extends Filter {

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence prefix, FilterResults results) {

			// NOTE: this function is *always* called from the UI thread.
			mSubItems = (ArrayList<NetInfo>) results.values;

			notifyDataSetChanged();
		}

		protected FilterResults performFiltering(CharSequence filterString) {
			// NOTE: this function is *always* called from a background thread,
			// and
			// not the UI thread.

			final FilterResults results = new FilterResults();
			final List<NetInfo> i = new ArrayList<NetInfo>();

			if (filterString != null && filterString.toString().length() > 0) {

				for (int index = 0; index < mAllItems.size(); index++) {
					final NetInfo item = mAllItems.get(index);
					if (item.getQrSsid().toLowerCase().contains(filterString.toString().toLowerCase())) {
						i.add(item);
					}

				}
				results.values = i;
				results.count = i.size();
			}
			else {
				synchronized (mAllItems) {
					results.values = mAllItems;
					results.count = mAllItems.size();
				}
			}

			return results;
		}
	}

}
