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
	public final static int TYPE_NO_ENC = 0;
	public final static int TYPE_WEP = 1;
	public final static int TYPE_WPA = 2;
	
	//private List<String> networkProperties = new ArrayList<String>();
	
	private String displayedString = "";
	private String qrSsid = "";
	private String qrNetType = "";
	private String qrPassword = "";
	
	
	public NetInfo(String displayedString) {
		super();
		this.displayedString = displayedString;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	//
	///////////////////////////////////////////////////////
	//
	
	@Override
	public String toString() {
		return displayedString;
	}

	public String getWifiQrCodeString(){
		if(!(qrSsid.length()>0)){
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("WIFI:");
		sb.append("S:" + qrSsid + ";");
		sb.append("T:" + qrNetType + ";");
		
		if(qrPassword.length() > 0){
			sb.append("P:" + qrPassword + ";");
		}
		
		sb.append(";");
		return sb.toString();
	}	
	
	public void setDisplayedString(String displayedString) {
		this.displayedString = displayedString;
	}

	public void setQrCodeInfo(String ssid, String password, int netType){
		qrSsid = ssid;
		qrPassword = password;
		
		switch (netType) {
		case TYPE_WEP:
			qrNetType = "WEP";
			break;
		case TYPE_WPA:
			qrNetType = "WPA";
			break;
		default:
			qrNetType = "nopass";
		}
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(displayedString);
		dest.writeString(qrSsid);
		dest.writeString(qrNetType);
		dest.writeString(qrPassword);
	}
}
