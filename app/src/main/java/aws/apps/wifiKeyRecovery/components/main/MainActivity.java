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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import aws.apps.wifiKeyRecovery.dataload.PasswordLoader;
import aws.apps.wifiKeyRecovery.util.ExecTerminal;
import aws.apps.wifiKeyRecovery.util.FileUtil;
import aws.apps.wifiKeyRecovery.util.WiFiNetworkValitator;
import uk.co.alt236.wpasupplicantparser.container.WifiNetworkInfo;

public class MainActivity extends BaseActivity {
    private static final int DIALOG_GET_PASSWORDS = 1;
    private static final String TAG = MainActivity.class.getName();
    private static final int LAYOUT_ID = R.layout.activity_main;
    private ItemFilter itemFilter;
    private RecyclerView recyclerView;
    private TextView textViewResultCount;
    private View samsungWarningView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT_ID);
        itemFilter = new ItemFilter();

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // setup GUI
        samsungWarningView = findViewById(R.id.samsung_warning);
        textViewResultCount = findViewById(R.id.tvResults);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        showSamsungWarning();
        loadData();
    }

    private void showSamsungWarning() {
        final PhoneManufacturerDetector phoneManufacturerDetector = new PhoneManufacturerDetector();
        final boolean isSamsung = phoneManufacturerDetector.isManufacturedBy(PhoneManufacturerDetector.Manufacturer.SAMSUNG);
        samsungWarningView.setVisibility(isSamsung ? View.VISIBLE : View.GONE);
    }

    @Override
    protected Dialog onCreateDialog(final int id) {
        switch (id) {
            case DIALOG_GET_PASSWORDS:
                final ProgressDialog mExecuteDialog = new ProgressDialog(this);
                mExecuteDialog.setMessage(getString(R.string.dialogue_text_please_wait));

                final PasswordLoader passwordLoader;
                if (BuildConfig.USE_DEBUG_DATA) {
                    passwordLoader = new PasswordLoader(new FileUtil(this));
                } else {
                    final String[] commands = getResources().getStringArray(R.array.shellCommands);
                    passwordLoader = new PasswordLoader(commands);
                }

                passwordLoader.loadPasswords(new PasswordLoader.PasswordLoadCallback() {
                    @Override
                    public void onPasswordsLoaded(final List<WifiNetworkInfo> list) {
                        final List<WifiNetworkInfo> validList = WiFiNetworkValitator.getValidNetworks(list);

                        Collections.sort(validList, new NetInfoComparator());
                        populateList(validList);
                        recyclerView.setTag(list);
                        resetUi();
                    }

                    @Override
                    public void onError() {
                        resetUi();
                    }

                    private void resetUi() {
                        removeDialog(DIALOG_GET_PASSWORDS);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    }
                });

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
                if (recyclerView != null) {
                    final List<WifiNetworkInfo> data = itemFilter.filter(newText);
                    recyclerView.swapAdapter(createAdapter(data), true);
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
                        (ArrayList<WifiNetworkInfo>) recyclerView.getTag(),
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

        if (recyclerView.getTag() != null) {
            saved.setWiFiPasswordList((List<WifiNetworkInfo>) recyclerView.getTag());
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
            recyclerView.setTag(saved.getWifiPasswordList());
        }
    }

    private void populateList(final List<WifiNetworkInfo> list) {
        textViewResultCount.setText(String.valueOf(list.size()));
        itemFilter.setData(list);

        if (list.size() > 0) {
            recyclerView.setAdapter(createAdapter(list));
        }

        supportInvalidateOptionsMenu();
    }

    /**
     * Convenience function combining clearInfo and getInfo
     */
    private void refreshInfo() {
        loadData();
    }

    private WifiNetworkRecyclerViewAdapter createAdapter(List<WifiNetworkInfo> list) {
        final WifiNetworkRecyclerViewAdapter adapter
                = new WifiNetworkRecyclerViewAdapter(this, getIntentDispatcher());
        adapter.setItems(list);
        return adapter;
    }

    private class NetInfoComparator implements Comparator<WifiNetworkInfo> {
        @Override
        public int compare(final WifiNetworkInfo o1, final WifiNetworkInfo o2) {
            return o1.getSsid().compareToIgnoreCase(o2.getSsid());
        }
    }
}
