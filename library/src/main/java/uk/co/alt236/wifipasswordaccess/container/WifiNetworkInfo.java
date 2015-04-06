package uk.co.alt236.wifipasswordaccess.container;

import uk.co.alt236.wifipasswordaccess.WifiNetworkType;

/**
 * Created by alex on 06/04/15.
 */
public interface WifiNetworkInfo {
    public WifiNetworkType getNetType();

    public String getPassword();

    public String getSsid();
}
