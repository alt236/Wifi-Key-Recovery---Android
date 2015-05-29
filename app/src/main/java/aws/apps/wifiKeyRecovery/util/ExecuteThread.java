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

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import aws.apps.wifiKeyRecovery.R;
import aws.apps.wifiKeyRecovery.util.ExecTerminal.ExecResult;
import uk.co.alt236.wifipasswordaccess.WifiPasswordFileParser;
import uk.co.alt236.wifipasswordaccess.container.WifiNetworkInfo;

public class ExecuteThread extends Thread {
    public final static int STATE_DONE = 0;
    public final static int STATE_RUNNING = 1;
    public final static int WORK_COMPLETED = 50;
    public final static int WORK_INTERUPTED = 51;
    private final String TAG = this.getClass().getName();

    private Handler mHandler;
    private Context mContext;
    private int mState;
    private boolean mIsRooted;

    public ExecuteThread(Handler h,
                         Context ctx,
                         Bundle b) {

        mHandler = h;
        mContext = ctx;
        mIsRooted = new ExecTerminal().checkSu();
    }

    private String execute(String cmd) {
        final ExecTerminal et = new ExecTerminal();
        final ExecResult res;

        if (mIsRooted) {
            res = et.execSu(cmd);
        } else {
            res = et.exec(cmd);
        }

        return res.getStdOut();
    }

    private List<WifiNetworkInfo> getWiFiPasswordList() {
        final WifiPasswordFileParser parser = new WifiPasswordFileParser();
        final String[] shellCommands = mContext.getResources().getStringArray(R.array.shellCommands);

        if (Constants.USE_DEBUG_DATA) {
            final FileUtil fileUtil = new FileUtil(mContext);
            return parser.parseWifiPasswordFileContents(fileUtil.readAssetsFileAsText("wpa_supplicant_example.conf"));
        } else {
            for (final String command : shellCommands) {
                String result = execute(command);
                if (result.trim().length() > 0) {
                    return parser.parseWifiPasswordFileContents(result);
                }
            }
        }

        final List<WifiNetworkInfo> l = new ArrayList<>();
        return l;
    }

    @Override
    public void run() {
        mState = STATE_RUNNING;
        Bundle b = new Bundle();
        Message msg = new Message();

        Log.d(TAG, "^ Thread: Thread Started");

        while (mState == STATE_RUNNING) {
            try {
                Thread.sleep(100);
                b.clear();

                b.putParcelableArrayList(
                        "passwords",
                        new ArrayList<>(getWiFiPasswordList()));

                msg = new Message();
                msg.what = WORK_COMPLETED;
                msg.setData(b);
                mHandler.sendMessage(msg);

                this.setState(STATE_DONE);
            } catch (InterruptedException e) {
                Log.e(TAG, "^ Thread: Thread Interrupted");
                b.clear();
                int what = WORK_INTERUPTED;
                msg.what = what;
                msg.setData(b);
                mHandler.sendMessage(msg);
                this.setState(STATE_DONE);
            } catch (Exception e) {
                Log.e(TAG, "^ Thread: exception " + e.getMessage());
                msg = new Message();
                b.clear();
                int what = WORK_INTERUPTED;
                msg.what = what;
                msg.setData(b);
                mHandler.sendMessage(msg);
                this.setState(STATE_DONE);
            }
        }
        Log.d(TAG, "^ Thread: Thread Exited");
    }

    /*
     * sets the current state for the thread, used to stop the thread
     */
    public void setState(int state) {
        mState = state;
    }
}
