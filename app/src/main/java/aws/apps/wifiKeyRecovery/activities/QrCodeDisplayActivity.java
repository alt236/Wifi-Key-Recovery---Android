package aws.apps.wifiKeyRecovery.activities;


import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import aws.apps.wifiKeyRecovery.R;
import aws.apps.wifiKeyRecovery.util.QRCodeUtils;
import uk.co.alt236.wifipasswordaccess.WifiNetworkInfo;

public class QrCodeDisplayActivity extends ActionBarActivity implements OnGlobalLayoutListener {
    public final static String EXTRAS_NETWORK_INFO = "aws.apps.wifiKeyRecovery.activities.EXTRAS_NETWORK_INFO";
    private final String TAG = this.getClass().getName();
    private ImageView mIvQrCode;
    private WifiNetworkInfo mNetworkInfo;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_qrcode);
        mIvQrCode = (ImageView) findViewById(R.id.qrcode);
        TextView mTextViewSsid = (TextView) findViewById(R.id.ssid);

        mNetworkInfo = getIntent().getExtras().getParcelable(EXTRAS_NETWORK_INFO);

        mTextViewSsid.setText(mNetworkInfo.getQrSsid());
        mIvQrCode.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    public void onGlobalLayout() {
        final int size;

        if (mIvQrCode.getHeight() > mIvQrCode.getWidth()) {
            size = mIvQrCode.getWidth();
        } else {
            size = mIvQrCode.getHeight();
        }

        Log.d(TAG, "^ onGlobalLayout() - " + size);

        if (Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.JELLY_BEAN) {
            mIvQrCode.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
            mIvQrCode.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }

        try {
            mIvQrCode.setImageBitmap(QRCodeUtils.encodeAsBitmap(
                    mNetworkInfo.getQrcodeString(),
                    BarcodeFormat.QR_CODE,
                    size,
                    size));
        } catch (WriterException e) {
            mIvQrCode.setBackgroundColor(getResources().getColor(android.R.color.black));
        }
    }

}
