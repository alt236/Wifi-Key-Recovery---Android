package uk.co.alt236.wpasupplicantparser.container;

public class OpenNetworkInfo implements WifiNetworkInfo {
    private final String mSsid;
    private final WifiNetworkType mNetType = WifiNetworkType.NO_ENCRYPTION;

    /*package*/ OpenNetworkInfo(final WifiNetworkBuilder builder) {
        this.mSsid = builder.getSsid();
    }

    @Override
    public WifiNetworkType getNetType() {
        return mNetType;
    }

    @Override
    public String getSsid() {
        return mSsid;
    }
}
