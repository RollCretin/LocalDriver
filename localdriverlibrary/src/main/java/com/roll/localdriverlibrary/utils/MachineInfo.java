package com.roll.localdriverlibrary.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
 * Created by houhualiang on 2015/6/15.
 */
public class MachineInfo {

    public static String getMachineCode(Context context) {
        String sn = getSN();
        if (!TextUtils.isEmpty(sn) && !"unknown".equals(sn)) {
            return sn;
        }
        String deviceID = getDeviceID(context);
        if (!TextUtils.isEmpty(deviceID)) {
            return deviceID;
        }
        String wifiMac = getWifiMac(context);
        if (null != wifiMac) {
            return wifiMac;
        }
        String UUID = getUUID(context);
        if (null != UUID) {
            return UUID;
        }
        return getInstallTime(context);
    }

    private static String getSN() {
        return Build.SERIAL;
    }

    @SuppressLint("MissingPermission")
    private static String getDeviceID(Context context) {
        TelephonyManager tm =
            (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        return tm.getDeviceId();
    }

    private static String getWifiMac(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wm.getConnectionInfo().getMacAddress();
    }

    private static String sID          = null;
    private static final String INSTALLATION = "INSTALLATION";

    private synchronized static String getUUID(Context context) {
        if (sID == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists()) {
                    writeInstallationFile(installation);
                }
                sID = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sID;
    }

    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }

    private static String getInstallTime(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return String.valueOf(packageInfo.firstInstallTime);
        } catch (Exception e) {
            return e.toString();
        }
    }

    public static final String getRandomUUUID() {
        return UUID.randomUUID().toString();
    }
}
