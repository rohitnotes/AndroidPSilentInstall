package com.tonyyang.androidpsilentinstall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.util.Log;

/**
 * @author tonyyang
 */
public class InstallResultReceiver extends BroadcastReceiver {

    private static final String TAG = InstallResultReceiver.class.getSimpleName();

    public static final String ACTION_INSTALL_RESULT = "com.tonyyang.common.testforruntimeexecute.INSTALL_RESULT";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && ACTION_INSTALL_RESULT.equals(intent.getAction())) {
            final int status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS,
                    PackageInstaller.STATUS_FAILURE);
            if (status == PackageInstaller.STATUS_SUCCESS) {
                Log.e(TAG, "success!");
            } else {
                Log.e(TAG, intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE));
            }
        }
    }
}
