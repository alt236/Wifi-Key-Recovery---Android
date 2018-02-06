package uk.co.alt236.wpasupplicantparser.container;

public class WepNetworkInfo implements WifiProtectedNetworkInfo {
    private static final WifiNetworkType mNetType = WifiNetworkType.WEP;
    private final String mSsid;
    private final String mPassword;
    private final String[] mPasswords;

    /*package*/ WepNetworkInfo(final WifiNetworkBuilder builder) {
        this.mSsid = builder.getSsid();
        this.mPasswords = builder.getWepPasswords();
        this.mPassword = getCorrectPassword(builder);
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

    public int getPasswordCount() {
        return mPasswords.length;
    }

    @Override
    public String getSsid() {
        return mSsid;
    }

    private static String getCorrectPassword(final WifiNetworkBuilder builder) {
        if (builder.getWepPasswords()[0] == null) {
            return builder.getPassword();
        } else {
            return builder.getWepPasswords()[0];
        }
    }
}
