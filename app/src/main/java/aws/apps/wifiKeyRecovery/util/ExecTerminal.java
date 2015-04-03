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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.util.Log;

public class ExecTerminal {
	final String TAG =  this.getClass().getName();

	public class ExecResult{
		final String TAG =  this.getClass().getName();

		String stdOut = "";
		String stdErr = "";
		String stdIn = "";

		public ExecResult(String stdIn, String stdOut, String stdErr){
			this.stdOut = stdOut;
			this.stdErr = stdErr;
			this.stdIn = stdIn;
		}

		public ExecResult(){
			clear();
		}

		public void clear(){
			stdOut = "";
			stdErr = "";
			stdIn = "";
		}

		public String getStdOut() {
			return stdOut;
		}

		public String getStdErr() {
			return stdErr;
		}

		public String getStdIn() {
			return stdIn;
		}
	};

	public boolean checkSu(){
		final ExecTerminal et = new ExecTerminal();
		ExecResult res = et.execSu("su && echo 1");

		if (res.getStdOut().trim().equals("1")){
			Log.i(TAG, "^ got root!");
			return true;
		}

		Log.w(TAG, "^ could not get root.");
		return false;
	}

	public ExecResult exec(String cmd) {
		Log.d(TAG, "^ exec(): '" + cmd + "'");
		String stdOut = "";
		String stdErr = "";

		try {
			final Process process = Runtime.getRuntime().exec("sh");

			final DataInputStream is = new DataInputStream(process.getInputStream());
			final DataInputStream eis = new DataInputStream(process.getErrorStream());
			final DataOutputStream os = new DataOutputStream(process.getOutputStream());

			os.writeBytes(cmd + "\n");
			os.writeBytes("exit\n");
			os.flush();
			os.close();

			final BufferedReader readerOut = new BufferedReader(	new InputStreamReader(is));
			final BufferedReader readerErr = new BufferedReader(new InputStreamReader(eis));

			process.waitFor();

			try {
				String line = "";
				while ((line = readerOut.readLine()) != null) {
					stdOut = stdOut + line + "\n";
				}
				readerOut.close();
			} catch (IOException e) {
				Log.e(TAG, "^ exec() - IOException in sdtdOut Loop: " + e.getMessage());
			}


			try {
				String line = "";
				while ((line = readerErr.readLine()) != null) {
					stdErr = stdErr + line + "\n";
				}
				readerErr.close();
			} catch (IOException e) {
				Log.e(TAG, "^ exec() - IOException in stdErr Loop: " + e.getMessage());
			}

		} catch (IOException e) {
			Log.e(TAG, "^ exec() IOException: " + e.getMessage());

		} catch (InterruptedException e) {
			Log.e(TAG, "^ exec() InterruptedException: " + e.getMessage());
		}

		ExecResult res = new ExecResult(cmd, stdOut, stdErr);

		return res;
	}

	public ExecResult execSu(String cmd) {
		Log.d(TAG, "^ execSu(): '" + cmd + "'");
		String stdOut = "";
		String stdErr = "";

		try {
			final Process process = Runtime.getRuntime().exec("su");
			final DataInputStream is = new DataInputStream(process.getInputStream());
			final DataInputStream eis = new DataInputStream(process.getErrorStream());
			final DataOutputStream os = new DataOutputStream(process.getOutputStream());

			os.writeBytes(cmd + "\n");
			os.writeBytes("exit\n");
			os.flush();
			os.close();

			final BufferedReader readerOut = new BufferedReader(new InputStreamReader(is));
			final BufferedReader readerErr = new BufferedReader(new InputStreamReader(eis));

			process.waitFor();

			try {
				String line = "";
				while ((line = readerOut.readLine()) != null) {
					stdOut = stdOut + line + "\n";
				}
				readerOut.close();
			} catch (IOException e) {
				Log.e(TAG, "^ execSu() - IOException in sdtdOut Loop: " + e.getMessage());
			}

			try {
				String line = "";
				while ((line = readerErr.readLine()) != null) {
					stdErr = stdErr + line + "\n";
				}
				readerErr.close();
			} catch (IOException e) {
				Log.e(TAG, "^ execSu() - IOException in sdtdOut Loop: " + e.getMessage());
			}

		} catch (IOException e) {
			Log.e(TAG, "^ execSu() - IOException: " + e.getMessage());
		} catch (InterruptedException e) {
			Log.e(TAG, "^ execSu() - InterruptedException: " + e.getMessage());
		}

		ExecResult res = new ExecResult(cmd, stdOut, stdErr);
		return res;
	}
}

