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
package aws.apps.wifiKeyRecovery.containers;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class WifiNetworkInfo implements Parcelable{
	private static final String DOUBLE_QUOTE = "\"";

	public final static int TYPE_UNKNOWN = -1;
	public final static int TYPE_NO_ENC = 0;
	public final static int TYPE_WEP = 1;
	public final static int TYPE_WPA = 2;

	private final String mDisplayedString;
	private final String mSsid;
	private final String mNetTypeAsString;
	private final String mPassword;
	private final int mNetType;

	public static final Parcelable.Creator<WifiNetworkInfo> CREATOR = new Parcelable.Creator<WifiNetworkInfo>() {
		@Override
		public WifiNetworkInfo createFromParcel(Parcel in) { return new WifiNetworkInfo(in); }

		@Override
		public WifiNetworkInfo[] newArray(int size) { return new WifiNetworkInfo[size]; }
	};

	public WifiNetworkInfo(Parcel in) {
		final Bundle b = in.readBundle(getClass().getClassLoader());

		mDisplayedString = b.getString("EXTRA_DIPLAYED_STRING");
		mSsid = b.getString("EXTRA_SSID");
		mNetTypeAsString = b.getString("EXTRA_NET_TYPE");
		mPassword = b.getString("EXTRA_PASSWORD");
		mNetType = b.getInt("EXTRA_NET_TYPE");
	}

	public WifiNetworkInfo(String displayedString) {
		this(displayedString, "", "", -1);
	}

	public WifiNetworkInfo(String displayedString, String ssid, String password, int netType){
		mDisplayedString = displayedString;
		mSsid = ssid;
		mPassword = password;
		mNetType = netType;
		mNetTypeAsString = getNetworkTypeAsString(netType);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public String getDisplayedString(){
		return mDisplayedString;
	}

	public int getNetType() {
		return mNetType;
	}

	public String getPassword(){
		return stripLeadingAndTrailingQuotes(mPassword);
	}

	public String getQrcodeString(){
		if(!(mSsid != null && mSsid.length()>0)){
			return "";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("WIFI:");
		sb.append("S:" + mSsid + ";");
		sb.append("T:" + mNetTypeAsString + ";");

		if(mPassword.length() > 0){
			sb.append("P:" + mPassword + ";");
		}

		sb.append(";");
		return sb.toString();
	}

	public String getQrPassword() {
		return mPassword;
	}



	public String getQrSsid() {
		return mSsid;
	}


	@Override
	public String toString() {
		return "NetInfo [mDisplayedString=" + mDisplayedString + ", mQrSsid=" + mSsid + ", mQrNetType=" + mNetTypeAsString + ", mQrPassword=" + mPassword + ", mNetType=" + mNetType + "]";
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		final Bundle b = new Bundle(getClass().getClassLoader());

		b.putString("EXTRA_DIPLAYED_STRING", mDisplayedString);
		b.putString("EXTRA_SSID",mSsid);
		b.putString("EXTRA_NET_TYPE",mNetTypeAsString);
		b.putString("EXTRA_PASSWORD",mPassword);
		b.putInt("EXTRA_NET_TYPE",mNetType);

		dest.writeBundle(b);
	}

	private static String getNetworkTypeAsString(int netType){
		final String result;

		switch (netType) {
		case TYPE_WEP:
			result = "WEP";
			break;
		case TYPE_WPA:
			result = "WPA";
			break;
		default:
			result = "nopass";
		}

		return result;
	}

	private static String stripLeadingAndTrailingQuotes(String str){
		if(str == null || str.length() <=0){
			return "";
		}

		if (str.startsWith(DOUBLE_QUOTE)){
			str = str.substring(1, str.length());
		}

		if (str.endsWith(DOUBLE_QUOTE)){
			str = str.substring(0, str.length() - 1);
		}

		return str;
	}
}