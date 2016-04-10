package aws.apps.wifiKeyRecovery.components.details;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import aws.apps.wifiKeyRecovery.R;
import uk.co.alt236.wifipasswordaccess.container.WifiNetworkInfo;

public class WifiDetailsFragment extends Fragment {
    private static final String WIFI_NETWORK = "wifi_network";
    private ImageView mIvQrCode;
    private WifiNetworkInfo mNetworkInfo;
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
                final String payload = QRCodeUtils.getQrCodeString(mNetworkInfo);
                mIvQrCode.setImageBitmap(QRCodeUtils.encodeAsBitmap(
                        payload,
                        BarcodeFormat.QR_CODE,
                        size,
                        size));
            } catch (final WriterException e) {
                mIvQrCode.setBackgroundColor(getResources().getColor(android.R.color.black));
            }
        }
    };

    public WifiDetailsFragment() {
    }

    public static Fragment getInstance(final WifiNetworkInfo info) {
        final Fragment fragment = new WifiDetailsFragment();
        final Bundle bundle = new Bundle();
        bundle.putParcelable(WIFI_NETWORK, info);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_wifi_details, container, false);

        final TextView mTextViewSsid = (TextView) view.findViewById(R.id.ssid);
        mIvQrCode = (ImageView) view.findViewById(R.id.qrcode);

        mNetworkInfo = getArguments().getParcelable(WIFI_NETWORK);

        if(mNetworkInfo == null){
            throw new IllegalStateException("The passed WIFI network cannot be null");
        }

        mTextViewSsid.setText(mNetworkInfo.getSsid());
        mIvQrCode.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);

        return view;
    }

    private void copyStringToClipboard(final String text) {
        if (text.length() > 0) {
            final ClipboardManager ClipMan = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipMan.setText(text);


            final String msgtext;
            if (text.length() > 150) {
                msgtext = text.substring(0, 150) + "...";
            } else {
                msgtext = text;
            }

            final String message = "'" + msgtext + "' " + getString(R.string.text_copied);
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onDestroyView() {
        super.onDestroyView();
        if (Build.VERSION.SDK_INT < 16) {
            mIvQrCode.getViewTreeObserver().removeGlobalOnLayoutListener(mGlobalLayoutListener);
        } else {
            mIvQrCode.getViewTreeObserver().removeOnGlobalLayoutListener(mGlobalLayoutListener);
        }
    }
}
