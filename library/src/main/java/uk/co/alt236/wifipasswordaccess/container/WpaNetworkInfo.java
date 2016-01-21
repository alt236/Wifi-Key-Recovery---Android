package uk.co.alt236.wifipasswordaccess.container;

import android.os.Parcel;
import android.os.Parcelable;

import uk.co.alt236.wifipasswordaccess.WifiNetworkType;

/**
 * Created by alex on 06/04/15.
 */
public class WpaNetworkInfo implements WifiProtectedNetworkInfo, Parcelable {
    public static final Parcelable.Creator<WpaNetworkInfo> CREATOR = new Parcelable.Creator<WpaNetworkInfo>() {
        @Override
        public WpaNetworkInfo createFromParcel(final Parcel in) {
            return new WpaNetworkInfo(in);
        }

        @Override
        public WpaNetworkInfo[] newArray(final int size) {
            return new WpaNetworkInfo[size];
        }
    };

    private final String mSsid;
    private final String mPassword;
    private final WifiNetworkType mNetType = WifiNetworkType.WPA;

    /*package*/ WpaNetworkInfo(final WifiNetworkBuilder builder) {
        this.mSsid = builder.getSsid();
        this.mPassword = getCorrectPssword(builder);
    }

    private WpaNetworkInfo(final Parcel in) {
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
    public String getPassword() {
        return mPassword;
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

    private static String getCorrectPssword(final WifiNetworkBuilder builder) {
        if (builder.getPassword() == null) {
            return builder.getPsk();
        } else {
            return builder.getPassword();
        }
    }
}
