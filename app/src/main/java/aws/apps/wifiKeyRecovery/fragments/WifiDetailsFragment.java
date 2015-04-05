package aws.apps.wifiKeyRecovery.fragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import aws.apps.wifiKeyRecovery.R;
import aws.apps.wifiKeyRecovery.util.QRCodeUtils;
import uk.co.alt236.wifipasswordaccess.WifiNetworkInfo;

public class WifiDetailsFragment extends Fragment {
    private static final String WIFI_NETWORK = "wifi_network";
    private final ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        @SuppressWarnings("deprecation")
        public void onGlobalLayout() {
            final int size;

            if (mIvQrCode.getHeight() > mIvQrCode.getWidth()) {
                size = mIvQrCode.getWidth();
            } else {
                size = mIvQrCode.getHeight();
            }

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
    };
    private ImageView mIvQrCode;
    private WifiNetworkInfo mNetworkInfo;

    public WifiDetailsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_wifi_details, container, false);

        final TextView mTextViewSsid = (TextView) view.findViewById(R.id.ssid);
        mIvQrCode = (ImageView) view.findViewById(R.id.qrcode);


        mNetworkInfo = getArguments().getParcelable(WIFI_NETWORK);

        mTextViewSsid.setText(mNetworkInfo.getQrSsid());
        mIvQrCode.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);

        return view;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onDestroyView(){
        super.onDestroyView();
        if(Build.VERSION.SDK_INT < 16){
            mIvQrCode.getViewTreeObserver().removeGlobalOnLayoutListener(mGlobalLayoutListener);
        }else{
            mIvQrCode.getViewTreeObserver().removeOnGlobalLayoutListener(mGlobalLayoutListener);
        }
    }

    public static Fragment getInstance(final WifiNetworkInfo info) {
        final Fragment fragment = new WifiDetailsFragment();
        final Bundle bundle = new Bundle();
        bundle.putParcelable(WIFI_NETWORK, info);
        fragment.setArguments(bundle);
        return fragment;
    }
}
