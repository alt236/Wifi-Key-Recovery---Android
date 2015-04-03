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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import aws.apps.wifiKeyRecovery.R;
import aws.apps.wifiKeyRecovery.containers.NetInfo;
import aws.apps.wifiKeyRecovery.util.ExecTerminal.ExecResult;

public class ExecuteThread extends Thread {
	private final static String WIFI_BLOCK_START = "network={";
	private final static String WIFI_BLOCK_END = "}";
	private final static int RESULT_TITLE_LENGTH = 14;

	public final static int STATE_DONE = 0;
	public final static int STATE_RUNNING = 1;

	public final static int WORK_COMPLETED = 50;
	public final static int WORK_INTERUPTED = 51;

	private final String TAG = this.getClass().getName();

	private Handler mHandler;
	private Context mContext;
	private UsefulBits mUsefulBits;
	private int mState;
	private boolean mIsRooted;

	public ExecuteThread(Handler h,
			Context ctx,
			Bundle b) {

		this.mHandler = h;
		this.mContext = ctx;
		this.mUsefulBits = new UsefulBits(mContext);

		this.mIsRooted = new ExecTerminal().checkSu();
	}

	private String appendBlanks(String text, int size) {
		String res = text.trim();

		if (res.length() < size) {
			final int change = size - res.length();

			for (int i = 0; i < change; i++) {
				res += " ";
			}

			return res;
		} else {
			return res;
		}

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

	private ArrayList<NetInfo> getWiFiPasswordList() {
		final String[] shellCommands = mContext.getResources().getStringArray(R.array.shellCommands);
		ArrayList<NetInfo> l = new ArrayList<NetInfo>();

		for (int i = 0; i < shellCommands.length; i++) {
			String result = execute(shellCommands[i]);
			if (result.trim().length() > 0) {
				l = parseWifiPasswords(l, result);
				return l;
			}
		}

		l.add(new NetInfo(mContext.getString(R.string.could_not_find_password_files)));
		return l;
	}

	private ArrayList<NetInfo> parseWifiPasswords(ArrayList<NetInfo> l, String wifiPasswordString) {
		final String passwordBlocks[] = wifiPasswordString.split("\n\n");
		final Map<String, String> passKeys = new HashMap<String, String>();
		final Map<String, String> settings = new HashMap<String, String>();

		String ssid = "";
		String password = ""; // only one, for the qr code;
		int type = -1;

		if (wifiPasswordString.length() <= 0) {
			return l;
		}

		for (int i = 0; i < passwordBlocks.length; i++) {
			String block = passwordBlocks[i].trim();

			if (block.startsWith(WIFI_BLOCK_START) && block.endsWith(WIFI_BLOCK_END)) {
				passKeys.clear();
				settings.clear();
				ssid = "";

				String blockLines[] = block.split("\n");

				for (int j = 0; j < blockLines.length; j++) {
					String line = blockLines[j].trim();

					if (line.startsWith("ssid=")) {
						ssid = line.replace("ssid=", "");

						// Network Keys:
					} else if (line.startsWith("psk=")) {
						passKeys.put("psk", line.replace("psk=", ""));
						password = line.replace("psk=", "");
						type = NetInfo.TYPE_WPA;
					} else if (line.startsWith("wep_key0=")) {
						passKeys.put("WEP Key 0", line.replace("wep_key0=", ""));
						password = line.replace("psk=", "");
						type = NetInfo.TYPE_WEP;
					} else if (line.startsWith("wep_key1=")) {
						passKeys.put("WEP Key 1", line.replace("wep_key1=", ""));
					} else if (line.startsWith("wep_key2=")) {
						passKeys.put("WEP Key 2", line.replace("wep_key2=", ""));
					} else if (line.startsWith("wep_key3=")) {
						passKeys.put("WEP Key 3", line.replace("wep_key3=", ""));
					} else if (line.startsWith("password=")) {
						passKeys.put("Password", line.replace("password=", ""));
						password = line.replace("psk=", "");

						// Settings:
					} else if (line.startsWith("key_mgmt=")) {
						settings.put("Key MGMT", line.replace("key_mgmt=", ""));
					} else if (line.startsWith("group=")) {
						settings.put("Group", line.replace("group=", ""));
					} else if (line.startsWith("auth_alg=")) {
						settings.put("Algorithm", line.replace("auth_alg=", ""));
					} else if (line.startsWith("eap=")) {
						settings.put("EAP", line.replace("eap=", ""));
					} else if (line.startsWith("identity=")) {
						settings.put("Identity", line.replace("identity=", ""));
					} else if (line.startsWith("anonymous_identity=")) {
						settings.put("Anonymous ID", line.replace("anonymous_identity=", ""));
					} else if (line.startsWith("phase2=")) {
						settings.put("Phase2 Auth", line.replace("phase2=", ""));
					}
				}

				String result = "";

				if (!passKeys.isEmpty()) {
					if (ssid.length() > 0) {
						result += appendBlanks("SSID:", RESULT_TITLE_LENGTH) + ssid + "\n";
					}

					Iterator<Entry<String, String>> it = passKeys.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
						result += appendBlanks(pairs.getKey() + ":", RESULT_TITLE_LENGTH) + pairs.getValue() + "\n";
					}

					it = settings.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
						result += appendBlanks(pairs.getKey() + ":", RESULT_TITLE_LENGTH) + pairs.getValue() + "\n";
					}

				}
				if (result.trim().length() > 0) {
					NetInfo ni = new NetInfo(result.trim());
					ni.setQrCodeInfo(ssid, password, type);
					l.add(ni);
				}
			}
		}
		// l.add("Protected Networks: " + protectedNetworkCount);
		return l;
	}

	public void run() {
		mState = STATE_RUNNING;
		Bundle b = new Bundle();
		Message msg = new Message();

		Log.d(TAG, "^ Thread: Thread Started");

		while (mState == STATE_RUNNING) {
			try {
				Thread.sleep(100);
				b.clear();

				b.putParcelableArrayList("passwords", getWiFiPasswordList());

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
	// //////////////////////////////////////////////////////////////////////////////////////////////////////
}
