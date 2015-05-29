package uk.co.alt236.wifipasswordaccess.container;

import android.os.Parcel;

import uk.co.alt236.wifipasswordaccess.WifiNetworkType;

/**
 * Created by alex on 06/04/15.
 */
public class WepNetworkInfo implements WifiNetworkInfo {
    public static final Creator<WepNetworkInfo> CREATOR = new Creator<WepNetworkInfo>() {
        @Override
        public WepNetworkInfo createFromParcel(Parcel in) {
            return new WepNetworkInfo(in);
        }

        @Override
        public WepNetworkInfo[] newArray(int size) {
            return new WepNetworkInfo[size];
        }
    };

    private final String mSsid;
    private final String mPassword;
    private final WifiNetworkType mNetType = WifiNetworkType.WEP;

    /*package*/ WepNetworkInfo(final WifiNetworkBuilder builder) {
        this.mSsid = builder.getSsid();
        this.mPassword = getFirstPassword(builder);
    }

    private WepNetworkInfo(final Parcel in) {
        mSsid = in.readString();
        mPassword = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private String getFirstPassword(final WifiNetworkBuilder builder) {
        if (builder.getWepPasswords()[0] == null) {
            return builder.getPassword();
        } else {
            return builder.getWepPasswords()[0];
        }
    }

    @Override
    public WifiNetworkType getNetType() {
        return mNetType;
    }

    @Override
    public String getPassword() {
        return mPassword;
    }

    @Override
    public String getSsid() {
        return mSsid;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mSsid);
        dest.writeString(mPassword);
    }
}
