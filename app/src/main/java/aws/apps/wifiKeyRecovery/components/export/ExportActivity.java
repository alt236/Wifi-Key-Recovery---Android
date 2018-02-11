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
package aws.apps.wifiKeyRecovery.components.export;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import aws.apps.wifiKeyRecovery.R;
import aws.apps.wifiKeyRecovery.components.common.base.BaseActivity;
import aws.apps.wifiKeyRecovery.util.FileUtil;
import aws.apps.wifiKeyRecovery.util.RuntimePermissionsUtil;
import uk.co.alt236.wpasupplicantparser.container.WifiNetworkInfo;

public class ExportActivity extends BaseActivity {
    private static final String EXTRAS_CONTENT = ExportActivity.class.getName() + ".EXTRA_CONTENT";
    private static final String EXTRAS_TIMESTAMP = ExportActivity.class.getName() + ".EXTRA_TIMESTAMP";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL = 213;

    private final String TAG = this.getClass().getName();

    private EditText mFldInfo;
    private long mTimeDate;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        final View shareButton = findViewById(R.id.buttonshare);
        final View saveToSdButton = findViewById(R.id.buttontosd);

        mFldInfo = findViewById(R.id.fld_export_text);

        final Bundle extras = getIntent().getExtras();
        if (extras == null) {
            throw new IllegalStateException("Extras were null!");
        }

        //noinspection unchecked
        final List<WifiNetworkInfo> networkInfos =
                (ArrayList<WifiNetworkInfo>) extras.getSerializable(EXTRAS_CONTENT);
        final ExportedDataFormatter formatter = new ExportedDataFormatter(this);

        mTimeDate = extras.getLong(EXTRAS_TIMESTAMP);
        mFldInfo.setText(getString(R.string.text_wifi_password_recovery) + " @ " + mTimeDate + "\n\n");
        mFldInfo.append(formatter.getString(networkInfos));

        shareButton.setOnClickListener(v -> shareResults());
        saveToSdButton.setOnClickListener(v -> saveToFile());
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String permissions[],
                                           @NonNull final int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveToFile();
                } else {
                    Toast.makeText(
                            this,
                            "Cannot write to external storage as the required permission was refused",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveToFile() {

        if (RuntimePermissionsUtil.isStoragePermissionGranted(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            final FileUtil fileUtil = new FileUtil(this);

            try {
                final File folder = Environment.getExternalStorageDirectory();
                final String filename = createFilename(mTimeDate);
                final String contents = mFldInfo.getText().toString();
                final boolean res = fileUtil.saveToFile(filename, folder, contents);
                if (res) {
                    Toast.makeText(this, "File saved successfully", Toast.LENGTH_SHORT).show();
                }
            } catch (final Exception e) {
                Log.e(TAG, "^ " + e.getMessage());
            }
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL);
        }

    }

    private void shareResults() {
        final String text = mFldInfo.getText().toString();
        final String subject = getString(R.string.text_wifi_password_recovery) + " @ " + mTimeDate;
        getIntentDispatcher().shareText(text, subject);
    }

    private static String createFilename(final long timestamp) {
        return "wifikeyrecovery_" + timestamp + ".txt";
    }

    public static Intent createIntent(final Context context,
                                      final ArrayList<WifiNetworkInfo> content,
                                      final long timestamp) {
        final Intent intent = new Intent(context, ExportActivity.class);
        intent.putExtra(EXTRAS_CONTENT, content);
        intent.putExtra(EXTRAS_TIMESTAMP, timestamp);
        return intent;
    }
}
