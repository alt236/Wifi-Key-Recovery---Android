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
package aws.apps.wifiKeyRecovery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import aws.apps.wifiKeyRecovery.containers.NetInfo;
import aws.apps.wifiKeyRecovery.containers.SavedData;
import aws.apps.wifiKeyRecovery.util.ExecTerminal;
import aws.apps.wifiKeyRecovery.util.ExecuteThread;
import aws.apps.wifiKeyRecovery.util.UiUtils;
import aws.apps.wifiKeyRecovery.util.UsefulBits;

import com.commonsware.cwac.merge.MergeAdapter;

public class MainActivity extends Activity {
	private static final int DIALOG_GET_PASSWORDS = 1;
	final String TAG =  this.getClass().getName();

	private MergeAdapter adapter;
	private String TimeDate="";
	private UsefulBits uB;
	private TextView lblTimeDate;
	private TextView lblDevice;	
	private ExecuteThread executeThread;
	private ProgressDialog executeDialog;
	private Bundle threadBundle;
	private TextView tvResultCount;
	private ListView list;
	private UiUtils gui;
	
	final Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			ArrayList<NetInfo> l = new ArrayList<NetInfo>();
			switch(msg.what){

			case ExecuteThread.WORK_COMPLETED:
				Log.d(TAG, "^ Worker Thread: WORK_COMPLETED");

				l = (ArrayList<NetInfo>) msg.getData().getSerializable("passwords");
				
				if (l != null){
					Collections.sort(l, new NetInfoComperator());
					listToList(l, adapter);
					list.setTag(l);
				}
				
				executeThread.setState(ExecuteThread.STATE_DONE);
				removeDialog(DIALOG_GET_PASSWORDS);
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

			case ExecuteThread.WORK_INTERUPTED:
				executeThread.setState(ExecuteThread.STATE_DONE);
				removeDialog(DIALOG_GET_PASSWORDS);
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
				break;
			}

		}
	};

	/** Clears the table and field contents */
	public void clearInfo() {
		lblTimeDate.setText("");
	}

	private View createView(NetInfo ni, int count){
		View v = getLayoutInflater().inflate(R.layout.list_item, null);
		TextView tv;
		ImageButton ib;
		
		tv = (TextView) v.findViewById(R.id.text);
		tv.setText(ni.toString()); 
		tv.setOnClickListener(gui.txtCopyOnClickListener);
		
		if(ni.getWifiQrCodeString().length()>0){
			ib = (ImageButton) v.findViewById(R.id.buttonQr);
			ib.setVisibility(View.VISIBLE);
			ib.setTag(ni.getWifiQrCodeString());
			ib.setOnClickListener(gui.btnQrCodeOnClickListener);
		}
		
		if(count % 2 == 0){
			v.setBackgroundDrawable(getResources().getDrawable(R.drawable.rowbg));
		}else{
			v.setBackgroundDrawable(getResources().getDrawable(R.drawable.rowbg_alt));
		}
		
		return v;
	}

	private void getPasswords(){	
		LockScreenRotation();
		ExecTerminal et = new ExecTerminal();

		if(et.checkSu()){
			showDialog(DIALOG_GET_PASSWORDS);	
		}else{
			TextView tv = new TextView(this);
			tv.setText(getString(R.string.root_needed));
			Linkify.addLinks(tv, Linkify.ALL);
			adapter.addView(tv);
			uB.ShowAlert(
					getString(R.string.text_unable_to_continue), 
					getString(R.string.root_needed), 
					getString(android.R.string.ok));
		}
	}

	private void listToList(List<NetInfo> l, MergeAdapter adapter){
		adapter = new MergeAdapter();
		
		if (l.size() == 0){
			return;
		}
		int count = 0;

		for (int i=0; i < l.size();i++){
			NetInfo ni = l.get(i);
			adapter.addView(createView(ni, count));			
			count+=1;
		}
		
		tvResultCount.setText(String.valueOf(count));
		list.setAdapter(adapter);
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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "^ Intent Started");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		uB = new UsefulBits(this);
		gui = new UiUtils(this);
		
		//setup GUI
		list = (ListView) findViewById(R.id.list);
		lblTimeDate = (TextView) findViewById(R.id.tvTime);
		lblDevice = (TextView) findViewById(R.id.tvDevice);
		tvResultCount = (TextView) findViewById(R.id.tvResults);

		populateInfo();
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_GET_PASSWORDS:
			executeDialog = new ProgressDialog(this);
			executeDialog.setMessage(getString(R.string.dialogue_text_please_wait));

			executeThread = new ExecuteThread(handler, this, threadBundle);
			executeThread.start();
			return executeDialog;
		default:
			return null;
		}
	}

	/** Creates the menu items */
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_BUTTONS.REFRESH.ordinal(), 0,
				getString(R.string.label_menu_refresh)).setIcon(android.R.drawable.ic_menu_rotate);
		menu.add(0, MENU_BUTTONS.EXPORT.ordinal(), 0,
				getString(R.string.label_menu_export)).setIcon(android.R.drawable.ic_menu_upload);
		menu.add(0, MENU_BUTTONS.ABOUT.ordinal(), 0,
				getString(R.string.label_menu_about)).setIcon(android.R.drawable.ic_menu_info_details);
		return true;
	}

	/** Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (MENU_BUTTONS.lookUpByOrdinal(item.getItemId())) {
		case ABOUT:
			uB.showAboutDialogue();
			return true;
		case REFRESH:
			refreshInfo();
			return true;
		case EXPORT:
			Intent myIntent = new Intent();
			String export_text = "";

			export_text += getString(R.string.label_wifi_passwords) + "\n";
			export_text += uB.listToString((List<?>) list.getTag()) + "\n\n";
			export_text += tvResultCount.getText();

			myIntent.putExtra("info", export_text);
			myIntent.putExtra("time", TimeDate);
			myIntent.setClassName(getPackageName(),getPackageName() + ".ExportActivity");
			startActivity(myIntent);
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object onRetainNonConfigurationInstance() {
		Log.d(TAG, "^ onRetainNonConfigurationInstance()");

		final SavedData saved = new SavedData();

		if(list.getTag() != null){
			saved.setWiFiPasswordList((List<NetInfo>) list.getTag());
		}
		saved.setDateTime(TimeDate);
		return saved;
	}

	/** Retrieves and displays info */
	private void populateInfo(){
		final Object data = getLastNonConfigurationInstance();
		
		if (data == null) { // We need to do everything from scratch!
			TimeDate = uB.formatDateTime("yyyy-MM-dd-HHmmssZ", new Date());
			lblTimeDate.setText(TimeDate);
			getPasswords();
		} else {
			final SavedData saved = (SavedData) data;
			TimeDate = saved.getDateTime();

			lblTimeDate.setText(TimeDate);
			listToList(saved.getWifiPasswordList(), adapter);
			list.setTag(saved.getWifiPasswordList());
		}
		lblDevice.setText(Build.PRODUCT + " " + Build.DEVICE);
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

enum MENU_BUTTONS {
	REFRESH, ABOUT, EXPORT;

	public static MENU_BUTTONS lookUpByOrdinal(int i) {
		return MENU_BUTTONS.values()[i];
	}
}
