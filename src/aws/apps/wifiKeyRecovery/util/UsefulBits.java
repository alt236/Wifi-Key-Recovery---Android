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
package aws.apps.wifiKeyRecovery.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Toast;
import aws.apps.wifiKeyRecovery.R;
import aws.apps.wifiKeyRecovery.ui.MyAlertBox;

public class UsefulBits {
	final String TAG =  this.getClass().getName();
	private Context mContext;

	public UsefulBits(Context cntx) {
		Log.d(TAG, "^ Object created");
		mContext = cntx;
	}

	public static boolean validateIPv4Address(String address) {
		try {
			java.net.Inet4Address.getByName(address);
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	public static boolean validateIPv6Address(String address) {
		try {
			java.net.Inet6Address.getByName(address);
			return true;
		} catch(Exception e) {
			return false;
		}
	}


	public Calendar convertMillisToDate(long millis){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendar;
	}

	public String formatDateTime(String formatString, Date d){
		Format formatter = new SimpleDateFormat(formatString);
		return formatter.format(d);
	}

	public String getAppVersion(){
		PackageInfo pi;
		try {
			pi = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			return "";
		}

	}

	public boolean isActivityAvailable(String packageName, String className) {
		final PackageManager packageManager = mContext.getPackageManager();
		final Intent intent = new Intent();
		intent.setClassName(packageName, className);

		List<ResolveInfo> list =
				packageManager.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);

		if (list.size() > 0) {
			Log.d(TAG, "^ Activity exists:" + className);
		}

		return list.size() > 0;
	}

	public boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> resolveInfo =
				packageManager.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
		if (resolveInfo.size() > 0) {
			return true;
		}
		return false;
	}

	public boolean isOnline() {
		try{
			ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

			if (cm != null) {
				return cm.getActiveNetworkInfo().isConnected();
			} else {
				return false;
			}

		}catch(Exception e){
			return false;
		}
	}

	public void saveToFile(String fileName, File directory, String contents){
		Log.d(TAG, "^ Saving file.");

		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)){
			try {

				if (directory.canWrite()){
					File gpxfile = new File(directory, fileName);
					FileWriter gpxwriter = new FileWriter(gpxfile);
					BufferedWriter out = new BufferedWriter(gpxwriter);
					out.write(contents);
					out.close();
					Log.d(TAG, "^ Saved to SD as '" + directory.getAbsolutePath() + "/" + fileName + "'");
					showToast("Saved to SD as '" + directory.getAbsolutePath() + "/" + fileName + "'",
							Toast.LENGTH_SHORT, Gravity.TOP,0,0);
				}

			} catch (Exception e) {
				showToast("Could not write file:\n+ e.getMessage()",
						Toast.LENGTH_SHORT, Gravity.TOP,0,0);
				Log.e(TAG, "^ Could not write file " + e.getMessage());
			}

		}else{
			showToast("No SD card is mounted...", Toast.LENGTH_SHORT, Gravity.TOP,0,0);
			Log.e(TAG, "^ No SD card is mounted.");
		}
	}

	public void showAboutDialogue(){
		String title = mContext.getString(R.string.app_name) + " v"+ getAppVersion();

		StringBuffer sb = new StringBuffer();

		sb.append(mContext.getString(R.string.app_changelog));
		sb.append("\n\n");
		sb.append(mContext.getString(R.string.app_notes));
		sb.append("\n\n");
		sb.append(mContext.getString(R.string.app_acknowledgements));
		sb.append("\n\n");
		sb.append(mContext.getString(R.string.app_copyright));

		MyAlertBox.create(mContext, sb.toString(), title, mContext.getString(android.R.string.ok)).show();
	}

	public void ShowAlert(String title, String text, String button){
		if (button.equals("")){button = mContext.getString(android.R.string.ok);}

		try{
			AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
			ad.setTitle( title );
			ad.setMessage(text);

			ad.setPositiveButton( button, null );
			ad.show();
		}catch (Exception e){
			Log.e(TAG, "^ ShowAlert()", e);
		}
	}

	public void showApplicationMissingAlert(String title, String message, String button1Text, final String marketUri){
		if (button1Text.equals("")){button1Text = mContext.getString(android.R.string.ok);}

		try{
			// Create the dialog box
			AlertDialog.Builder alertbox = new AlertDialog.Builder(mContext);

			alertbox.setTitle(title);
			alertbox.setMessage(message);

			alertbox.setPositiveButton(button1Text, null);
			alertbox.setNegativeButton("Market", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					try {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(marketUri));
						mContext.startActivity(intent);
					} catch (Exception e){
						Log.e(TAG, "^ Error opening Market Page : " + e.getMessage());
						ShowAlert(mContext.getString(R.string.text_error), mContext.getString(R.string.text_could_not_go_to_market), mContext.getString(android.R.string.ok));
					}
				}
			});

			alertbox.show();

		}catch (Exception e){
			Log.e(TAG, "^ ShowAlertWithWirelessSettings()", e);

		}
	}

	public void showToast(String message, int duration, int location, int x_offset, int y_offset){
		Toast toast = Toast.makeText(mContext.getApplicationContext(), message, duration);
		toast.setGravity(location,x_offset,y_offset);
		toast.show();
	}

	public String listToString(List<?> list) {
		StringBuffer sb = new StringBuffer();
		int cnt = 0;

		for(Object obj:list){
			cnt +=1;
			sb.append("#" + cnt +":\n");
			sb.append(obj + "\n");
		}

		return sb.toString();
	}

	public int dipToPixels(int dip) {
		int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				(float) dip, mContext.getResources().getDisplayMetrics());
		return value;
	}

	public float dipToPixels(float dip) {
		float value = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dip, mContext.getResources().getDisplayMetrics());
		return value;
	}
}
