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

import android.os.Parcel;
import android.os.Parcelable;

public class NetInfo implements Parcelable{
	public final static int TYPE_UNKNOWN = -1;
	public final static int TYPE_NO_ENC = 0;
	public final static int TYPE_WEP = 1;
	public final static int TYPE_WPA = 2;

	private String mDisplayedString = "";
	private String mQrSsid = "";
	private String mQrNetType = "";
	private String mQrPassword = "";
	private int mNetType = -1;

	public NetInfo(String displayedString) {
		super();
		this.mDisplayedString = displayedString;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public int getNetType() {
		return mNetType;
	}

	public String getQrcodeString(){
		if(!(mQrSsid.length()>0)){
			return "";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("WIFI:");
		sb.append("S:" + mQrSsid + ";");
		sb.append("T:" + mQrNetType + ";");

		if(mQrPassword.length() > 0){
			sb.append("P:" + mQrPassword + ";");
		}

		sb.append(";");
		return sb.toString();
	}	

	public String getQrPassword() {
		return mQrPassword;
	}

	public String getQrSsid() {
		return mQrSsid;
	}

	public void setDisplayedString(String displayedString) {
		this.mDisplayedString = displayedString;
	}

	public void setQrCodeInfo(String ssid, String password, int netType){
		mQrSsid = ssid;
		mQrPassword = password;
		mNetType = netType;

		switch (netType) {
		case TYPE_WEP:
			mQrNetType = "WEP";
			break;
		case TYPE_WPA:
			mQrNetType = "WPA";
			break;
		default:
			mQrNetType = "nopass";
		}
	}

	@Override
	public String toString() {
		return mDisplayedString;
	}

	public String getPassword(){
		return stripLeadingAndTrailingQuotes(mQrPassword);
	}

	private String stripLeadingAndTrailingQuotes(String str){
		if(str == null || str.length() <=0){
			return "";
		}

		if (str.startsWith("\"")){
			str = str.substring(1, str.length());
		}
		if (str.endsWith("\"")){
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mDisplayedString);
		dest.writeString(mQrSsid);
		dest.writeString(mQrNetType);
		dest.writeString(mQrPassword);
		dest.writeInt(mNetType);
	}
}