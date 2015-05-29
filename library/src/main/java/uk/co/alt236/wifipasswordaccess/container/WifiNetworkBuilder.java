package uk.co.alt236.wifipasswordaccess.container;

/**
 * Created by alex on 06/04/15.
 */
public class WifiNetworkBuilder {
    private String ssid;
    private String[] wepPasswords = new String[4];
    private String psk;
    private String password;
    private boolean hasWepPassword;

    public WifiNetworkInfo build() {
        if (hasWepPassword) {
            return new WepNetworkInfo(this);
        } else {
            return new WpaNetworkInfo(this);
        }
    }

    /*pacage*/ String getPassword() {
        return password;
    }

    public WifiNetworkBuilder setPassword(final String password) {
        this.password = password;
        return this;
    }

    /*pacage*/ String getPsk() {
        return psk;
    }

    public WifiNetworkBuilder setPsk(final String psk) {
        this.psk = psk;
        return this;
    }

    /*pacage*/ String getSsid() {
        return ssid;
    }

    public WifiNetworkBuilder setSsid(final String ssid) {
        this.ssid = ssid;
        return this;
    }

    /*pacage*/ String[] getWepPasswords() {
        return wepPasswords;
    }

    public WifiNetworkBuilder setWepPassword(final int position, final String password) {
        wepPasswords[position] = password;
        hasWepPassword = true;
        return this;
    }
}
