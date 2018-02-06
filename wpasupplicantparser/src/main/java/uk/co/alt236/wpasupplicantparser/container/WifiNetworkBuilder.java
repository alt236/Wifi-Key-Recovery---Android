package uk.co.alt236.wpasupplicantparser.container;

import uk.co.alt236.wpasupplicantparser.util.TextUtils;

public class WifiNetworkBuilder {
    private final String[] wepPasswords = new String[4];
    private boolean hasWepPassword;
    private String ssid;
    private String psk;
    private String password;
    private String keyManagement;
    private String eap;

    public WifiNetworkInfo build() {
        if (isOpen()) {
            return new OpenNetworkInfo(this);
        } else if (isWep()) {
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

    private boolean isOpen() {
        return TextUtils.isEmpty(password)
                && TextUtils.isEmpty(psk)
                && (TextUtils.isEmpty(keyManagement) || "NONE".equalsIgnoreCase(keyManagement))
                && TextUtils.isEmpty(eap)
                && !hasWepPassword;
    }

    private boolean isWep() {
        return hasWepPassword || "LEAP".equals(eap);
    }

    public WifiNetworkBuilder setEap(final String eap) {
        this.eap = eap;
        return this;
    }

    public WifiNetworkBuilder setKeyManagement(final String keyManagement) {
        this.keyManagement = keyManagement;
        return this;
    }

    public WifiNetworkBuilder setWepPassword(final int position, final String password) {
        wepPasswords[position] = password;
        if (password != null && password.length() > 0) {
            hasWepPassword = true;
        }
        return this;
    }
}
