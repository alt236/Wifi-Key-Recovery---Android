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
package aws.apps.wifiKeyRecovery.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import aws.apps.wifiKeyRecovery.R;
import aws.apps.wifiKeyRecovery.adapters.NetInfoAdapter;
import aws.apps.wifiKeyRecovery.containers.WifiNetworkInfo;
import aws.apps.wifiKeyRecovery.containers.SavedData;
import aws.apps.wifiKeyRecovery.ui.IconFriendlyPopupMenu;
import aws.apps.wifiKeyRecovery.ui.IconFriendlyPopupMenu.OnMenuItemClickListener;
import aws.apps.wifiKeyRecovery.ui.MyAlertBox;
import aws.apps.wifiKeyRecovery.util.ExecTerminal;
import aws.apps.wifiKeyRecovery.util.ExecuteThread;
import aws.apps.wifiKeyRecovery.util.PopupMenuActionHelper;
import aws.apps.wifiKeyRecovery.util.UsefulBits;

@SuppressWarnings("deprecation")
public class MainActivity extends FragmentActivity implements OnItemClickListener, OnMenuItemClickListener {
	private static final int DIALOG_GET_PASSWORDS = 1;
	final String TAG = this.getClass().getName();

	private Bundle mThreadBundle;
	private EditText mEditFilter;
	private ExecuteThread mExecuteThread;
	private IconFriendlyPopupMenu mPopup;
	private ListView mList;
	private WifiNetworkInfo mCurrentNetinfo;
	private NetInfoAdapter mNiAdapter;
	private ProgressDialog mExecuteDialog;
	private String mTimeDate = "";
	private TextView mTextViewResultCount;
	private UsefulBits mUsefulBits;

	private TextWatcher filterTextWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (mNiAdapter != null) {
				mNiAdapter.getFilter().filter(s);
			} else {
				Log.w(TAG, "^ TextWatcher: Adapter is null!");
			}
		}
	};

	final Handler handler = new Handler() {
		@Override
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case ExecuteThread.WORK_COMPLETED:
				Log.d(TAG, "^ Worker Thread: WORK_COMPLETED");

				final List<WifiNetworkInfo> list = (ArrayList<WifiNetworkInfo>) msg.getData().getSerializable("passwords");

				if (list != null) {
					Collections.sort(list, new NetInfoComperator());
					populateList(list);
					mList.setTag(list);
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

	/** Clears the table and field contents */
	public void clearInfo() {

	}

	private void copyStringToClipboard(String text) {
		if (text.length() > 0) {
			String msgtext = "";
			if (text.length() > 150) {
				msgtext = text.substring(0, 150) + "...";
			} else {
				msgtext = text;
			}

			final String message = "'" + msgtext + "' " + getString(R.string.text_copied);
			mUsefulBits.showToast(message, Toast.LENGTH_SHORT, Gravity.TOP, 0, 0);

			final ClipboardManager ClipMan = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			ClipMan.setText(text);
		}
	}

	private void getPasswords() {
		LockScreenRotation();
		final ExecTerminal et = new ExecTerminal();

		if (et.checkSu()) {
			showDialog(DIALOG_GET_PASSWORDS);
		} else {

			AlertDialog dlg = MyAlertBox.create(this, getString(R.string.root_needed), getString(R.string.app_name), getString(android.R.string.ok));

			dlg.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					MainActivity.this.finish();
				}
			});

			dlg.show();
		}
	}

	// Sets screen rotation as fixed to current rotation setting
	private void LockScreenRotation() {
		// Stop the screen orientation changing during an event
		switch (this.getResources().getConfiguration().orientation){
		case Configuration.ORIENTATION_PORTRAIT:
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;
		}
	}

	public void onClearSearchClick(View v) {
		mEditFilter.setText("");
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mUsefulBits = new UsefulBits(this);

		// setup GUI
		mEditFilter = (EditText) findViewById(R.id.edit_search);
		mList = (ListView) findViewById(R.id.list);
		mTextViewResultCount = (TextView) findViewById(R.id.tvResults);

		mList.setFastScrollEnabled(true);
		mList.setOnItemClickListener(this);
		mList.setDivider(null);
		mList.setDividerHeight(mUsefulBits.dipToPixels(1));
		mList.setFastScrollEnabled(true);
		populateInfo();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_GET_PASSWORDS:
			mExecuteDialog = new ProgressDialog(this);
			mExecuteDialog.setMessage(getString(R.string.dialogue_text_please_wait));

			mExecuteThread = new ExecuteThread(handler, this, mThreadBundle);
			mExecuteThread.start();
			return mExecuteDialog;
		default:
			return null;
		}
	}

	/** Creates the menu items */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.menu_home, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mEditFilter.removeTextChangedListener(filterTextWatcher);
	}

	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		mPopup = new IconFriendlyPopupMenu(this, v, true);
		mPopup.setOnMenuItemClickListener(this);

		mCurrentNetinfo = null;

		if(v.getTag() != null){
			if(v.getTag() instanceof WifiNetworkInfo){
				mCurrentNetinfo = (WifiNetworkInfo) v.getTag();
			}
		}

		PopupMenuActionHelper.addCopyAll(this, mPopup);
		PopupMenuActionHelper.addCopyPassword(this, mPopup);
		PopupMenuActionHelper.addShowQrCode(this, mPopup);

		mPopup.show();
	}

	@Override
	public boolean onMenuItemClick(MenuItem paramMenuItem) {
		final int actionId = paramMenuItem.getItemId();
		String text;

		final boolean res;
		switch (actionId) {
		case PopupMenuActionHelper.ACTION_ID_NETWORK_COPY_ALL_AS_TEXT:
			copyStringToClipboard(mCurrentNetinfo.toString());
			res = true;
			break;
		case PopupMenuActionHelper.ACTION_ID_NETWORK_COPY_PASSWORD:
			copyStringToClipboard(mCurrentNetinfo.getPassword());
			res = true;
			break;
		case PopupMenuActionHelper.ACTION_ID_NETWORK_SHOW_QRCODE:
			text = mCurrentNetinfo.getQrcodeString();

			if (text.length() > 0) {
				final Intent intent = new Intent(this, QrCodeDisplayActivity.class);
				intent.putExtra(
						QrCodeDisplayActivity.EXTRAS_NETWORK_INFO,
						mCurrentNetinfo);

				startActivity(intent);
			}
			res = true;
			break;
		default:
			res = false;
		}

		return res;
	}

	/** Handles item selections */
	@SuppressWarnings("unchecked")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			mUsefulBits.showAboutDialogue();
			return true;
		case R.id.menu_export:
			Intent myIntent = new Intent();
			String export_text = "";
			export_text += getString(R.string.label_wifi_passwords) + "\n";
			export_text += mUsefulBits.listToString((List<WifiNetworkInfo>) mList.getTag()) + "\n\n";
			export_text += mTextViewResultCount.getText();
			myIntent.putExtra("info", export_text);
			myIntent.putExtra("time", mTimeDate);
			myIntent.setClass(this, ExportActivity.class);
			startActivity(myIntent);
			return true;
		case R.id.menu_refresh:
			refreshInfo();
			return true;
		}
		return false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		mEditFilter.removeTextChangedListener(filterTextWatcher);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mEditFilter != null) {
			mEditFilter.addTextChangedListener(filterTextWatcher);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		Log.d(TAG, "^ onRetainNonConfigurationInstance()");

		final SavedData saved = new SavedData();

		if (mList.getTag() != null) {
			saved.setWiFiPasswordList((List<WifiNetworkInfo>) mList.getTag());
		}

		saved.setDateTime(mTimeDate);
		return saved;
	}

	/** Retrieves and displays info */
	private void populateInfo() {
		final Object data = getLastCustomNonConfigurationInstance();

		if (data == null) { // We need to do everything from scratch!
			mTimeDate = mUsefulBits.formatDateTime("yyyy-MM-dd-HHmmssZ", new Date());
			getPasswords();
		} else {
			final SavedData saved = (SavedData) data;
			mTimeDate = saved.getDateTime();

			populateList(saved.getWifiPasswordList());
			mList.setTag(saved.getWifiPasswordList());
		}
	}

	private void populateList(List<WifiNetworkInfo> l) {
		if (l.size() > 0) {
			findViewById(R.id.filter_segment).setVisibility(View.VISIBLE);
			mNiAdapter = new NetInfoAdapter(this, l);
			mTextViewResultCount.setText(String.valueOf(l.size()));
			mList.setAdapter(mNiAdapter);
			mEditFilter.addTextChangedListener(filterTextWatcher);
		} else {
			mTextViewResultCount.setText("0");
			findViewById(R.id.filter_segment).setVisibility(View.GONE);
		}
	}

	/** Convenience function combining clearInfo and getInfo */
	public void refreshInfo() {
		clearInfo();
		populateInfo();
	}

	public class NetInfoComperator implements Comparator<WifiNetworkInfo> {
		@Override
		public int compare(WifiNetworkInfo o1, WifiNetworkInfo o2) {
			return o1.toString().compareToIgnoreCase(o2.toString());
		}
	}
}
