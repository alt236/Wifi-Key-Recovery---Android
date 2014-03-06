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

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;
import net.londatiga.android.QuickAction.OnActionItemClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import aws.apps.wifiKeyRecovery.containers.NetInfo;
import aws.apps.wifiKeyRecovery.containers.SavedData;
import aws.apps.wifiKeyRecovery.ui.MyAlertBox;
import aws.apps.wifiKeyRecovery.util.ExecTerminal;
import aws.apps.wifiKeyRecovery.util.ExecuteThread;
import aws.apps.wifiKeyRecovery.util.UsefulBits;

public class MainActivity extends Activity implements OnItemClickListener, OnActionItemClickListener{
	private static final int ID_COPY_PASSWORD	= 0;
	private static final int ID_COPY_ALL   		= 1;
	private static final int ID_SHOW_QRCODE   	= 2;

	private static final int DIALOG_GET_PASSWORDS = 1;
	final String TAG =  this.getClass().getName();

	private Bundle mThreadBundle;
	private EditText mEditFilter;
	private ExecuteThread mExecuteThread;
	private ListView mList;
	private NetInfoAdapter mNiAdapter;
	private ProgressDialog mExecuteDialog;
	private QuickAction mQuickAction;
	private String mTimeDate="";
	private TextView mLabelDevice;
	private TextView mLabelTimeDate;
	private TextView mTextViewResultCount;
	private UsefulBits mUsefulBits;

	private TextWatcher filterTextWatcher = new TextWatcher() {

		public void afterTextChanged(Editable s) {}

		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if(mNiAdapter != null){
				mNiAdapter.getFilter().filter(s);
			} else {
				Log.w(TAG, "^ TextWatcher: Adapter is null!");
			}
		}
	};

	final Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch(msg.what){

			case ExecuteThread.WORK_COMPLETED:
				Log.d(TAG, "^ Worker Thread: WORK_COMPLETED");
				List<NetInfo> l = new ArrayList<NetInfo>();

				l = (ArrayList<NetInfo>) msg.getData().getSerializable("passwords");

				if (l != null){
					Collections.sort(l, new NetInfoComperator());
					populateList(l);
					mList.setTag(l);
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
		mLabelTimeDate.setText("");
	}

	private void copyStringToClipboard(String text){
		if (text.length() > 0) {
			String msgtext = "";
			if (text.length()>150) {
				msgtext = text.substring(0, 150) + "...";
			} else {
				msgtext = text;
			}
			String message = "'" + msgtext + "' " + getString(R.string.text_copied);
			mUsefulBits.showToast(message, Toast.LENGTH_SHORT, Gravity.TOP,0,0);

			ClipboardManager ClipMan = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			ClipMan.setText(text);
		}
	}

	private void getPasswords(){
		LockScreenRotation();
		ExecTerminal et = new ExecTerminal();

		if(et.checkSu()){
			showDialog(DIALOG_GET_PASSWORDS);
		}else{

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
	private void LockScreenRotation(){
		// Stop the screen orientation changing during an event
		switch (this.getResources().getConfiguration().orientation)
		{
		case Configuration.ORIENTATION_PORTRAIT:
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;
		}
	}

	public void onClearSearchClick(View v){
		mEditFilter.setText("");
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mUsefulBits = new UsefulBits(this);

		//setup GUI
		mList = (ListView) findViewById(R.id.list);
		mLabelTimeDate = (TextView) findViewById(R.id.tvTime);
		mLabelDevice = (TextView) findViewById(R.id.tvDevice);
		mTextViewResultCount = (TextView) findViewById(R.id.tvResults);
		mEditFilter = (EditText) findViewById(R.id.edit_search);

		mList.setFastScrollEnabled(true);
		mList.setOnItemClickListener(this);
		mList.setDivider( null );
		mList.setDividerHeight(mUsefulBits.dipToPixels(1));

		mQuickAction = new QuickAction(this, QuickAction.ORIENTATION_VERTICAL, QuickAction.COLOUR_LIGHT);

		final ActionItem actionCopyPassword = new ActionItem(
				ID_COPY_PASSWORD,
				getString(R.string.label_copy_password),
				getResources().getDrawable(R.drawable.ic_list_copy));

		final ActionItem actionCopyAll = new ActionItem(
				ID_COPY_ALL,
				getString(R.string.label_copy_all),
				getResources().getDrawable(R.drawable.ic_list_copy2));

		final ActionItem actionShowQr = new ActionItem(
				ID_SHOW_QRCODE,
				getString(R.string.label_show_qr_code),
				getResources().getDrawable(R.drawable.ic_list_barcode));

		mQuickAction.addActionItem(actionCopyPassword);
		mQuickAction.addActionItem(actionCopyAll);
		mQuickAction.addActionItem(actionShowQr);

		mQuickAction.setOnActionItemClickListener(this);

		populateInfo();
	}

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
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.home, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mEditFilter.removeTextChangedListener(filterTextWatcher);
	}

	public void onItemClick(AdapterView<?> l, View v, int position, long id){
		mQuickAction.show(v);
	}

	public void onItemClick(QuickAction source, int pos, int actionId) {
		View view = mQuickAction.getLastAnchorView();
		String text;

		final NetInfo ni = (NetInfo) view.getTag();

		switch (actionId) {
		case ID_COPY_ALL:
			copyStringToClipboard(ni.toString());
			break;
		case ID_COPY_PASSWORD:
			copyStringToClipboard(ni.getPassword());
			break;
		case ID_SHOW_QRCODE:
			text = ni.getQrcodeString();

			if (text.length() > 0) {
				if (mUsefulBits.isIntentAvailable(this, "com.google.zxing.client.android.ENCODE")){
					Intent i = new Intent();
					i.setAction("com.google.zxing.client.android.ENCODE");
					i.putExtra ("ENCODE_TYPE", "TEXT_TYPE");
					i.putExtra ("ENCODE_DATA", text);
					startActivity(i);
				} else {
					mUsefulBits.showApplicationMissingAlert(
							getString(R.string.component_missing),
							getString(R.string.you_need_the_barcode_scanner_application),
							getString(R.string.dismiss),
							getString(R.string.zxing_market_url));
				}
			}
			break;
		}
	}

	/** Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
		if(R.id.menu_about == item.getItemId()){
			mUsefulBits.showAboutDialogue();
			return true;
		}
		else if(R.id.menu_export == item.getItemId()){
			Intent myIntent = new Intent();
			String export_text = "";

			export_text += getString(R.string.label_wifi_passwords) + "\n";
			export_text += mUsefulBits.listToString((List<?>) mList.getTag()) + "\n\n";
			export_text += mTextViewResultCount.getText();

			myIntent.putExtra("info", export_text);
			myIntent.putExtra("time", mTimeDate);
			myIntent.setClass(this, ExportActivity.class);
			startActivity(myIntent);
			return true;
		}
		else if(R.id.menu_refresh == item.getItemId()){
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
		if(mEditFilter != null){
			mEditFilter.addTextChangedListener(filterTextWatcher);
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public Object onRetainNonConfigurationInstance() {
		Log.d(TAG, "^ onRetainNonConfigurationInstance()");

		final SavedData saved = new SavedData();

		if(mList.getTag() != null){
			saved.setWiFiPasswordList((List<NetInfo>) mList.getTag());
		}

		saved.setDateTime(mTimeDate);
		return saved;
	}

	/** Retrieves and displays info */
	private void populateInfo(){
		final Object data = getLastNonConfigurationInstance();

		if (data == null) { // We need to do everything from scratch!
			mTimeDate = mUsefulBits.formatDateTime("yyyy-MM-dd-HHmmssZ", new Date());
			mLabelTimeDate.setText(mTimeDate);
			getPasswords();
		} else {
			final SavedData saved = (SavedData) data;
			mTimeDate = saved.getDateTime();

			mLabelTimeDate.setText(mTimeDate);
			populateList(saved.getWifiPasswordList());
			mList.setTag(saved.getWifiPasswordList());
		}
		mLabelDevice.setText(Build.PRODUCT + " " + Build.DEVICE);
	}

	private void populateList(List<NetInfo> l){
		if(l.size() > 0 ){
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

	public class NetInfoComperator implements Comparator<NetInfo> {
		@Override
		public int compare(NetInfo o1, NetInfo o2) {
			return o1.toString().compareToIgnoreCase(o2.toString());
		}
	}
}
