package com.tonyyang.androidpsilentinstall;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * @author tonyyang
 */
public class InstallUtils {

    private static final String TAG = InstallUtils.class.getSimpleName();

    private Context context;

    public InstallUtils(Context context) {
        this.context = context;
    }

    public void install28(String apkFilePath) {
        File apkFile = new File(apkFilePath);
        PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
        PackageInstaller.SessionParams sessionParams
                = new PackageInstaller.SessionParams(PackageInstaller
                .SessionParams.MODE_FULL_INSTALL);
        sessionParams.setSize(apkFile.length());

        int sessionId = createSession(packageInstaller, sessionParams);
        if (sessionId != -1) {
            boolean copySuccess = copyInstallFile(packageInstaller, sessionId, apkFilePath);
            if (copySuccess) {
                execInstallCommand(packageInstaller, sessionId);
            }
        }
    }

    private int createSession(PackageInstaller packageInstaller,
                              PackageInstaller.SessionParams sessionParams) {
        int sessionId = -1;
        try {
            sessionId = packageInstaller.createSession(sessionParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sessionId;
    }

    private boolean copyInstallFile(PackageInstaller packageInstaller,
                                    int sessionId, String apkFilePath) {
        boolean success = false;
        File apkFile = new File(apkFilePath);
        try (PackageInstaller.Session session = packageInstaller.openSession(sessionId);
             OutputStream out = session.openWrite("base.apk", 0, apkFile.length());
             InputStream in = new FileInputStream(apkFile)) {
            int total = 0, c;
            byte[] buffer = new byte[65536];
            while ((c = in.read(buffer)) != -1) {
                total += c;
                out.write(buffer, 0, c);
            }
            session.fsync(out);
            Log.i(TAG, "streamed " + total + " bytes");
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

    private void execInstallCommand(PackageInstaller packageInstaller, int sessionId) {
        try (PackageInstaller.Session session = packageInstaller.openSession(sessionId)) {
            Intent intent = new Intent(context, InstallResultReceiver.class);
            intent.setAction(InstallResultReceiver.ACTION_INSTALL_RESULT);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    1, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            session.commit(pendingIntent.getIntentSender());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
