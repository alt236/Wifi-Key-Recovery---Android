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

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import aws.apps.wifiKeyRecovery.R;
import aws.apps.wifiKeyRecovery.util.UsefulBits;

public class ExportActivity extends Activity {
	final String TAG =  this.getClass().getName();

	private EditText mFldInfo;
	private Button mBtnShare;
	private Button mBtnToSd;
	private Button mBtnClose;
	private String mTimeDate;
	private UsefulBits mUsefulBits;

	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "^ Intent started");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.export);

		final Bundle extras = getIntent().getExtras();
		mUsefulBits = new UsefulBits(getApplicationContext());

		mFldInfo = (EditText) findViewById(R.id.fld_export_text);
		mBtnShare = (Button) findViewById(R.id.buttonshare);
		mBtnToSd = (Button) findViewById(R.id.buttontosd);
		mBtnClose = (Button) findViewById(R.id.buttoncloseexport);

		if(extras !=null)
		{
			mTimeDate = extras.getString("time");
			mFldInfo.setText(getString(R.string.text_wifi_password_recovery)  + " @ " + mTimeDate +"\n\n");
			mFldInfo.append(extras.getString("info"));
		}

		mBtnShare.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				shareResults();
			}
		});

		mBtnToSd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				try {
					final File folder = Environment.getExternalStorageDirectory();
					final String filename = "wifikeyrecovery_" + mTimeDate + ".txt";
					final String contents = mFldInfo.getText().toString();
					mUsefulBits.saveToFile(filename, folder, contents);
				} catch (Exception e) {
					Log.e(TAG, "^ " + e.getMessage());
				}
			}
		});

		mBtnClose.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void shareResults(){
		final Intent t = new Intent(Intent.ACTION_SEND);
		final String text = mFldInfo.getText().toString();
		final String subject =  getString(R.string.text_wifi_password_recovery)  + " @ " + mTimeDate;

		t.setType("text/plain");
		t.putExtra(Intent.EXTRA_TEXT, text);
		t.putExtra(Intent.EXTRA_SUBJECT, subject);
		t.addCategory(Intent.CATEGORY_DEFAULT);
		final Intent share = Intent.createChooser(
				t,
				getString(R.string.label_share_dialogue_title));
		startActivity(share);
	}
}
