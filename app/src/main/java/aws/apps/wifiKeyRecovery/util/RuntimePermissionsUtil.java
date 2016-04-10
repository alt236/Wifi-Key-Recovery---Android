package aws.apps.wifiKeyRecovery.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

/**
 *
 */
public class RuntimePermissionsUtil {
    private static final String TAG = RuntimePermissionsUtil.class.getSimpleName();

    public static boolean isStoragePermissionGranted(final Context context, final String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(permission)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission '" + permission + "' is granted");
                return true;
            } else {
                Log.v(TAG, "Permission '" + permission + "' is revoked");
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission '" + permission + "' is granted");
            return true;
        }


    }
}
