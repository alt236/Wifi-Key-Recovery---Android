package uk.co.alt236.wpasupplicantparser.container;

public class WpaNetworkInfo implements WifiProtectedNetworkInfo {


    private final String mSsid;
    private final String mPassword;
    private final WifiNetworkType mNetType = WifiNetworkType.WPA;

    /*package*/ WpaNetworkInfo(final WifiNetworkBuilder builder) {
        this.mSsid = builder.getSsid();
        this.mPassword = getCorrectPssword(builder);
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

    private static String getCorrectPssword(final WifiNetworkBuilder builder) {
        if (builder.getPassword() == null) {
            return builder.getPsk();
        } else {
            return builder.getPassword();
        }
    }
}
