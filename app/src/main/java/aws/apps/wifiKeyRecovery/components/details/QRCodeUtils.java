package aws.apps.wifiKeyRecovery.components.details;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

import uk.co.alt236.wpasupplicantparser.container.WifiNetworkInfo;
import uk.co.alt236.wpasupplicantparser.container.WifiNetworkType;
import uk.co.alt236.wpasupplicantparser.container.WifiProtectedNetworkInfo;

/*package*/ final class QRCodeUtils {
    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    private QRCodeUtils() {
    }

    public static Bitmap encodeAsBitmap(final String payload, final BarcodeFormat format, final int width, final int height) throws WriterException {
        return encodeAsBitmap(payload, format, width, height, WHITE, BLACK);
    }

    public static Bitmap encodeAsBitmap(final String payload, final BarcodeFormat format, final int width, final int height, final int bgColor, final int fgColor) throws WriterException {
        if (!has(payload)) {
            return null;
        }

        final Map<EncodeHintType, Object> hints;
        final String encoding = guessAppropriateEncoding(payload);
        final BitMatrix result;

        if (encoding != null) {
            hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        } else {
            hints = null;
        }

        try {
            result = new MultiFormatWriter().encode(payload, format, width, height, hints);
        } catch (final IllegalArgumentException iae) { // Unsupported format
            return null;
        }

        final int bmWidth = result.getWidth();
        final int bmHeight = result.getHeight();
        final int[] pixels = new int[bmWidth * bmHeight];

        for (int y = 0; y < bmHeight; y++) {
            final int offset = y * bmWidth;
            for (int x = 0; x < bmWidth; x++) {
                pixels[offset + x] = result.get(x, y) ? fgColor : bgColor;
            }
        }

        final Bitmap bitmap = Bitmap.createBitmap(bmWidth, bmHeight, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, bmWidth, 0, 0, bmWidth, bmHeight);
        return bitmap;
    }

    private static String guessAppropriateEncoding(final CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    private static boolean has(final String value) {
        return value != null;

    }

    private static String getNetworkTypeAsString(final WifiNetworkType netType) {
        final String result;

        switch (netType) {
            case WEP:
                result = "WEP";
                break;
            case WPA:
                result = "WPA";
                break;
            default:
                result = "nopass";
        }

        return result;
    }

    public static String getQrCodeString(final WifiNetworkInfo networkInfo) {
        if (TextUtils.isEmpty(networkInfo.getSsid())) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();
        sb.append("WIFI:");
        sb.append("S:").append(networkInfo.getSsid()).append(";");
        sb.append("T:").append(getNetworkTypeAsString(networkInfo.getNetType())).append(";");

        if (networkInfo instanceof WifiProtectedNetworkInfo) {
            final WifiProtectedNetworkInfo protectedNet = (WifiProtectedNetworkInfo) networkInfo;
            if (!TextUtils.isEmpty(protectedNet.getPassword())) {
                sb.append("P:").append(protectedNet.getPassword()).append(";");
            }
        }

        sb.append(";");
        return sb.toString();
    }
}
