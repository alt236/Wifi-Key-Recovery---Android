package aws.apps.wifiKeyRecovery.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by alex on 04/04/15.
 */
public class FileUtil {
    private final String TAG = this.getClass().getName();
    private final Context mContext;

    public FileUtil(final Context cntx) {
        mContext = cntx.getApplicationContext();
    }

    public String readAssetsFileAsText(final String fileName) {
        return readAssetsFileAsText(fileName, "UTF-8");
    }

    public String readAssetsFileAsText(final String fileName, final String encoding) {

        BufferedReader br = null;
        final StringBuilder sb = new StringBuilder();

        String line;
        try {
            final InputStream is = mContext.getAssets().open(fileName);
            br = new BufferedReader(new InputStreamReader(is, encoding));
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }


    public boolean saveToFile(final String fileName, final File directory, final String contents) {
        Log.d(TAG, "^ Saving file.");

        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            try {

                if (directory.canWrite()) {
                    final File gpxfile = new File(directory, fileName);
                    final FileWriter gpxwriter = new FileWriter(gpxfile);
                    final BufferedWriter out = new BufferedWriter(gpxwriter);
                    out.write(contents);
                    out.close();
                    Log.d(TAG, "^ Saved to SD as '" + directory.getAbsolutePath() + "/" + fileName + "'");
                    return true;
                } else {
                    Log.e(TAG, "^ Could not write file - directory is not writable");
                }

            } catch (final Exception e) {
                Log.e(TAG, "^ Could not write file " + e.getMessage());
            }

        } else {
            Log.e(TAG, "^ No SD card is mounted.");
        }
        return false;
    }


}
