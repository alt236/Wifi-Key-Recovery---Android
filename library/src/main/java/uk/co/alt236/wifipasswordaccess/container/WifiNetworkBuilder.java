package uk.co.alt236.wifipasswordaccess.container;

/**
 * Created by alex on 06/04/15.
 */
public class WifiNetworkBuilder {
    private String ssid;
    private String[] wepPasswords = new String[4];
    private String psk;
    private String password;

    public WifiNetworkInfo build() {
        return null;
    }

    public WifiNetworkBuilder setPassword(final String password) {
        this.password = password;
        return this;
    }

    public WifiNetworkBuilder setPsk(final String psk) {
        this.psk = psk;
        return this;
    }

    public WifiNetworkBuilder setSsid(final String ssid) {
        this.ssid = ssid;
        return this;
    }

    public WifiNetworkBuilder setWepPassword(final int position, final String password) {
        wepPasswords[position] = password;
        return this;
    }
}
