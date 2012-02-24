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
package aws.apps.wifiKeyRecovery.util;

import android.content.Context;
import android.content.Intent;
import android.text.ClipboardManager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import aws.apps.wifiKeyRecovery.R;

public class UiUtils {

	private Context c;
	private UsefulBits uB;

	public UiUtils(Context c) {
		super();
		this.c = c;
		uB = new UsefulBits(c);
	}

	public View.OnClickListener txtCopyOnClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			TextView t = (TextView) v;
			String text = t.getText() + "";

			if (text.length() > 0) {
				String msgtext = "";
				if (text.length()>150) {
					msgtext = text.substring(0, 150) + "...";
				} else {
					msgtext = text;
				}
				String message = "'" + msgtext + "' " + c.getString(R.string.text_copied);
				uB.showToast(message, Toast.LENGTH_SHORT, Gravity.TOP,0,0);

				ClipboardManager ClipMan = (ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
				ClipMan.setText(text);
			}
		}
	};

	public View.OnClickListener btnQrCodeOnClickListener = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			ImageButton bt = (ImageButton) v;
			String text = bt.getTag() + "";				

			if (text.length() > 0) {
				if (uB.isIntentAvailable(c, "com.google.zxing.client.android.ENCODE")){		
					Intent i = new Intent();
					i.setAction("com.google.zxing.client.android.ENCODE");
					i.putExtra ("ENCODE_TYPE", "TEXT_TYPE");
					i.putExtra ("ENCODE_DATA", text);
					c.startActivity(i); 
				} else {
					uB.showApplicationMissingAlert(
							c.getString(R.string.component_missing), 
							c.getString(R.string.you_need_the_barcode_scanner_application), 
							c.getString(R.string.dismiss), 
							c.getString(R.string.zxing_market_url));
				}
			}
		}
	};
}
