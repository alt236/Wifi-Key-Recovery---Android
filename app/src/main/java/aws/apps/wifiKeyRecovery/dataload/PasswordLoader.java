package aws.apps.wifiKeyRecovery.dataload;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

import aws.apps.wifiKeyRecovery.BuildConfig;
import aws.apps.wifiKeyRecovery.util.ExecTerminal;
import aws.apps.wifiKeyRecovery.util.FileUtil;
import uk.co.alt236.wpasupplicantparser.container.WifiNetworkInfo;
import uk.co.alt236.wpasupplicantparser.parser.WpaSupplicantParser;

public class PasswordLoader {
    private final String[] commands;
    private final Handler handler;
    private final FileUtil fileUtil;
    private final ExecTerminal exec;

    public PasswordLoader(final String[] commands) {
        this.exec = new ExecTerminal();
        this.commands = commands;
        this.fileUtil = null;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public PasswordLoader(final FileUtil fileUtil) {
        this.exec = new ExecTerminal();
        this.commands = null;
        this.fileUtil = fileUtil;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void loadPasswords(final PasswordLoadCallback callback) {
        final Runnable runnable = () -> {
            final List<WifiNetworkInfo> list = getWiFiPasswordList();
            handler.post(() -> callback.onPasswordsLoaded(list));
        };

        new Thread(runnable).run();
    }


    private List<WifiNetworkInfo> getWiFiPasswordList() {
        final WpaSupplicantParser parser = new WpaSupplicantParser();

        if (BuildConfig.USE_DEBUG_DATA) {
            return parser.parseWifiPasswordFileContents(fileUtil.readAssetsFileAsText("wpa_supplicant_example.conf"));
        } else {
            final boolean rooted = exec.checkSu();
            for (final String command : commands) {
                final String result = execute(command, rooted);
                if (result.trim().length() > 0) {
                    return parser.parseWifiPasswordFileContents(result);
                }
            }
        }

        return new ArrayList<>();
    }


    private String execute(final String cmd, boolean asRoot) {
        final ExecTerminal.ExecResult res;
        if (asRoot) {
            res = exec.execSu(cmd);
        } else {
            res = exec.exec(cmd);
        }

        return res.getStdOut();
    }

    public interface PasswordLoadCallback {
        void onPasswordsLoaded(List<WifiNetworkInfo> list);

        void onError();
    }
}
