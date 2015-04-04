/*******************************************************************************
 * Copyright 2011 Alexandros Schillings
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package aws.apps.wifiKeyRecovery.containers;

import java.util.ArrayList;
import java.util.List;

import uk.co.alt236.wifipasswordaccess.WifiNetworkInfo;

public class SavedData {
    final String TAG = this.getClass().getName();

    private List<WifiNetworkInfo> tWifiPasswords = new ArrayList<WifiNetworkInfo>();

    private String dateTime = "";
    private boolean areWeRooted = false;
    private int textSize;

    public boolean getAreWeRooted() {
        return areWeRooted;
    }

    public void setAreWeRooted(boolean areWeRooted) {
        this.areWeRooted = areWeRooted;
    }

    public String getDateTime() {
        return dateTime;
    }


    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int size) {
        textSize = size;
    }

    public List<WifiNetworkInfo> getWifiPasswordList() {
        return tWifiPasswords;
    }

    public void setWiFiPasswordList(List<WifiNetworkInfo> l) {
        tWifiPasswords = l;
    }
}
