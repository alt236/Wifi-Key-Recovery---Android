package uk.co.alt236.wifipasswordaccess.container;

import android.os.Parcel;

import uk.co.alt236.wifipasswordaccess.WifiNetworkType;

/**
 * Created by alex on 06/04/15.
 */
public class WepNetworkInfo implements WifiProtectedNetworkInfo {
    public static final Creator<WepNetworkInfo> CREATOR = new Creator<WepNetworkInfo>() {
        @Override
        public WepNetworkInfo createFromParcel(final Parcel in) {
            return new WepNetworkInfo(in);
        }

        @Override
        public WepNetworkInfo[] newArray(final int size) {
            return new WepNetworkInfo[size];
        }
    };
    private static final WifiNetworkType mNetType = WifiNetworkType.WEP;
    private final String mSsid;
    private final String mPassword;
    private final String[] mPasswords;

    /*package*/ WepNetworkInfo(final WifiNetworkBuilder builder) {
        this.mSsid = builder.getSsid();
        this.mPasswords = builder.getWepPasswords();
        this.mPassword = getCorrectPassword(builder);
    }

    private WepNetworkInfo(final Parcel in) {
        mSsid = in.readString();
        mPassword = in.readString();

        final int passwordNo = in.readInt();
        mPasswords = new String[passwordNo];
        in.readStringArray(mPasswords);
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

    public String getPassword(final int position) {
        return mPasswords[position];
    }

    public int getPasswordCount(){
        return mPasswords.length;
    }

    @Override
    public String getSsid() {
        return mSsid;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(mSsid);
        dest.writeString(mPassword);
        dest.writeInt(mPasswords.length);
        dest.writeArray(mPasswords);
    }

    private static String getCorrectPassword(final WifiNetworkBuilder builder) {
        if (builder.getWepPasswords()[0] == null) {
            return builder.getPassword();
        } else {
            return builder.getWepPasswords()[0];
        }
    }
}
