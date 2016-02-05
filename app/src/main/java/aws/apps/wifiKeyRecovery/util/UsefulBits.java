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
package aws.apps.wifiKeyRecovery.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.Toast;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import aws.apps.wifiKeyRecovery.R;
import aws.apps.wifiKeyRecovery.ui.MyAlertBox;

public class UsefulBits {
    private final Context mContext;

    public UsefulBits(final Context cntx) {
        mContext = cntx;
    }


    public String formatDateTime(final String formatString, final Date d) {
        final Format formatter = new SimpleDateFormat(formatString);
        return formatter.format(d);
    }

    public String getAppVersion() {
        final PackageInfo pi;
        try {
            pi = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            return pi.versionName;
        } catch (final NameNotFoundException e) {
            return "";
        }

    }

    public void showAboutDialogue() {
        final String title = mContext.getString(R.string.app_name) + " v" + getAppVersion();

        final StringBuilder sb = new StringBuilder();

        sb.append(mContext.getString(R.string.app_changelog));
        sb.append("\n\n");
        sb.append(mContext.getString(R.string.app_notes));
        sb.append("\n\n");
        sb.append(mContext.getString(R.string.app_acknowledgements));
        sb.append("\n\n");
        sb.append(mContext.getString(R.string.app_copyright));

        MyAlertBox.create(mContext, sb.toString(), title, mContext.getString(android.R.string.ok)).show();
    }

    public void showToast(final String message, final int duration, final int location, final int x_offset, final int y_offset) {
        final Toast toast = Toast.makeText(mContext.getApplicationContext(), message, duration);
        toast.setGravity(location, x_offset, y_offset);
        toast.show();
    }
}
