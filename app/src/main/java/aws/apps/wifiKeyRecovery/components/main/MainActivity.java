/**
 * ****************************************************************************
 * Copyright 2011 Alexandros Schillings
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ****************************************************************************
 */
package aws.apps.wifiKeyRecovery.components.main;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import aws.apps.wifiKeyRecovery.BuildConfig;
import aws.apps.wifiKeyRecovery.R;
import aws.apps.wifiKeyRecovery.components.common.base.BaseActivity;
import aws.apps.wifiKeyRecovery.components.common.dialogs.DialogFactory;
import aws.apps.wifiKeyRecovery.containers.SavedData;
import aws.apps.wifiKeyRecovery.util.ExecTerminal;
import aws.apps.wifiKeyRecovery.util.ExecuteThread;
import uk.co.alt236.wifipasswordaccess.container.WifiNetworkInfo;

@SuppressWarnings("deprecation")
public class MainActivity extends BaseActivity {
    private static final int DIALOG_GET_PASSWORDS = 1;
    private final String TAG = this.getClass().getName();
    private ExecuteThread mExecuteThread;
    private RecyclerView mRecyclerView;
    private WifiNetworkRecyclerViewAdapter mRecyclerAdapter;
    private TextView mTextViewResultCount;
    private final Handler handler = new Handler() {
        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(final Message msg) {
            switch (msg.what) {

                case ExecuteThread.WORK_COMPLETED:
                    Log.d(TAG, "^ Worker Thread: WORK_COMPLETED");

                    final List<WifiNetworkInfo> list = (ArrayList<WifiNetworkInfo>) msg.getData().getSerializable("passwords");

                    if (list != null) {
                        Collections.sort(list, new NetInfoComparator());
                        populateList(list);
                        mRecyclerView.setTag(list);
                    }

                    mExecuteThread.setState(ExecuteThread.STATE_DONE);
                    removeDialog(DIALOG_GET_PASSWORDS);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

                case ExecuteThread.WORK_INTERUPTED:
                    mExecuteThread.setState(ExecuteThread.STATE_DONE);
                    removeDialog(DIALOG_GET_PASSWORDS);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    break;
            }

        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // setup GUI
        mTextViewResultCount = (TextView) findViewById(R.id.tvResults);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadData();
    }

    @Override
    protected Dialog onCreateDialog(final int id) {
        switch (id) {
            case DIALOG_GET_PASSWORDS:
                final ProgressDialog mExecuteDialog = new ProgressDialog(this);
                mExecuteDialog.setMessage(getString(R.string.dialogue_text_please_wait));

                mExecuteThread = new ExecuteThread(handler, this);
                mExecuteThread.start();
                return mExecuteDialog;
            default:
                return null;
        }
    }

    /**
     * Creates the menu items
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                if (mRecyclerAdapter != null) {
                    //mRecyclerAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });

        return true;
    }

    /**
     * Handles item selections
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                DialogFactory.getAboutDialog(this).show();
                return true;
            case R.id.action_export:
                getIntentDispatcher().openExportActivity(
                        (List<WifiNetworkInfo>) mRecyclerView.getTag(),
                        System.currentTimeMillis());
                return true;
            case R.id.action_refresh:
                refreshInfo();
                return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        Log.d(TAG, "^ onRetainNonConfigurationInstance()");

        final SavedData saved = new SavedData();

        if (mRecyclerView.getTag() != null) {
            saved.setWiFiPasswordList((List<WifiNetworkInfo>) mRecyclerView.getTag());
        }

        return saved;
    }

    /**
     * Retrieves and displays info
     */
    private void loadData() {
        final Object data = getLastCustomNonConfigurationInstance();

        if (data == null) { // We need to do everything from scratch!
            final boolean hasRoot;
            if (BuildConfig.USE_DEBUG_DATA) {
                hasRoot = true;
            } else {
                final ExecTerminal et = new ExecTerminal();
                hasRoot = et.checkSu();
            }

            if (hasRoot) {
                showDialog(DIALOG_GET_PASSWORDS);
            } else {
                final Dialog dlg = DialogFactory.getRootWarning(this, new OnDismissListener() {
                    @Override
                    public void onDismiss(final DialogInterface dialog) {
                        MainActivity.this.finish();
                    }
                });

                dlg.show();
            }
        } else {
            final SavedData saved = (SavedData) data;
            populateList(saved.getWifiPasswordList());
            mRecyclerView.setTag(saved.getWifiPasswordList());
        }
    }

    private void populateList(final List<WifiNetworkInfo> l) {
        mTextViewResultCount.setText(String.valueOf(l.size()));

        if (l.size() > 0) {
            final WifiNetworkRecyclerViewAdapter adapter
                    = new WifiNetworkRecyclerViewAdapter(this, getIntentDispatcher());
            adapter.setItems(l);
            mRecyclerView.setAdapter(adapter);
            mRecyclerAdapter = adapter;
        }

        supportInvalidateOptionsMenu();
    }

    /**
     * Convenience function combining clearInfo and getInfo
     */
    private void refreshInfo() {
        loadData();
    }

    private class NetInfoComparator implements Comparator<WifiNetworkInfo> {
        @Override
        public int compare(final WifiNetworkInfo o1, final WifiNetworkInfo o2) {
            return o1.getSsid().compareToIgnoreCase(o2.getSsid());
        }
    }
}
