package uk.co.alt236.wifipasswordaccess.container;

import android.os.Parcel;

import uk.co.alt236.wifipasswordaccess.WifiNetworkType;

/**
 * Created by alex on 06/04/15.
 */
public class OpenNetworkInfo implements WifiNetworkInfo {
    public static final Creator<OpenNetworkInfo> CREATOR = new Creator<OpenNetworkInfo>() {
        @Override
        public OpenNetworkInfo createFromParcel(final Parcel in) {
            return new OpenNetworkInfo(in);
        }

        @Override
        public OpenNetworkInfo[] newArray(final int size) {
            return new OpenNetworkInfo[size];
        }
    };

    private final String mSsid;
    private final String mPassword;
    private final WifiNetworkType mNetType = WifiNetworkType.NO_ENCRYPTION;

    /*package*/ OpenNetworkInfo(final WifiNetworkBuilder builder) {
        this.mSsid = builder.getSsid();
        this.mPassword = null;
    }

    private OpenNetworkInfo(final Parcel in) {
        mSsid = in.readString();
        mPassword = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public WifiNetworkType getNetType() {
        return mNetType;
    }

    @Override
    public String getSsid() {
        return mSsid;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(mSsid);
        dest.writeString(mPassword);
    }
}
