package uk.co.alt236.wpasupplicantparser.container;

import java.io.Serializable;

public interface WifiNetworkInfo extends Serializable {
    WifiNetworkType getNetType();

    String getSsid();
}
