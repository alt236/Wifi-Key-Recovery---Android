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
package uk.co.alt236.wifipasswordaccess;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class WifiNetworkInfo implements Parcelable {
    public static final Parcelable.Creator<WifiNetworkInfo> CREATOR = new Parcelable.Creator<WifiNetworkInfo>() {
        @Override
        public WifiNetworkInfo createFromParcel(Parcel in) {
            return new WifiNetworkInfo(in);
        }

        @Override
        public WifiNetworkInfo[] newArray(int size) {
            return new WifiNetworkInfo[size];
        }
    };

    private final String mDisplayedString;
    private final String mSsid;
    private final String mPassword;
    private final WifiNetworkType mNetType;

    private WifiNetworkInfo(Parcel in) {
        mDisplayedString = in.readString();
        mSsid = in.readString();
        mPassword = in.readString();
        mNetType = (WifiNetworkType) in.readSerializable();
    }

    public WifiNetworkInfo(String displayedString, String ssid, String password, WifiNetworkType netType) {
        mDisplayedString = displayedString;
        mSsid = ssid;
        mPassword = password;
        mNetType = netType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getDisplayedString() {
        return mDisplayedString;
    }

    public WifiNetworkType getNetType() {
        return mNetType;
    }

    public String getPassword() {
        return Util.stripLeadingAndTrailingQuotes(mPassword);
    }

    public String getQrSsid() {
        return mSsid;
    }


    @Override
    public String toString() {
        return "WifiNetworkInfo{" +
                ", mSsid='" + mSsid + '\'' +
                ", mPassword='" + mPassword + '\'' +
                ", mNetType=" + mNetType +
                '}';
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDisplayedString);
        dest.writeString(mSsid);
        dest.writeString(mPassword);
        dest.writeSerializable(mNetType);
    }
}