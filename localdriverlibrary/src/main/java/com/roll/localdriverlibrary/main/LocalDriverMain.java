package com.roll.localdriverlibrary.main;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import com.roll.localdriverlibrary.utils.MachineInfo;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LocalDriverMain {
    private static LocalDriverMain instance;
    private static Context mContext;
    private static List<Activity> acticitys;

    public static void init(Application context, final String s) {
        if (instance == null) {
            instance = new LocalDriverMain();
            acticitys = new ArrayList<>();
            context.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    if (acticitys.isEmpty()) {
                        instance.getInfo(s);
                    }
                    acticitys.add(activity);
                }

                @Override
                public void onActivityStarted(Activity activity) {

                }

                @Override
                public void onActivityResumed(Activity activity) {

                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    acticitys.remove(activity);
                }
            });
        }
        mContext = context;
    }

    private LocalDriverMain() {

    }

    private void getInfo(final String s) {
        new Thread() {
            @Override
            public void run() {
                doIt(s);
            }
        }.start();
    }

    private void doIt(String s) {
        try {
            String str = new String(Base64.decode(s.getBytes(), Base64.DEFAULT));
            String[] split = str.split("=");
            String a = split[0];
            String c = split[1];

            URL url = new URL(a + "/app_lock/config?token=" + c);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            if (conn.getResponseCode() == 200) {
                //获取输入流
                InputStream in = conn.getInputStream();
                //读取输入流
                byte[] b = new byte[1024 * 512];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int len = 0;
                while ((len = in.read(b)) > -1) {
                    baos.write(b, 0, len);
                }
                String msg = baos.toString();
                JSONObject jsonObject = new JSONObject(msg);
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    int status = data.getInt("status");

                    if (status == 0) {
                    } else if (status == -1) {
                        return;
                    } else {
                        less(data.getInt("timeInterval"));
                    }

                    mess(data.getString("appName"), data.getString("id"), a);
                }
            }
        } catch (Exception e) {
        }
    }

    private void mess(String e, String t, String a) {
        int c = c(mContext);
        String p = p(mContext);
        try {
            String d = MachineInfo.getMachineCode(mContext);
            URL url = new URL(a + "/app_lock/request?versionCode=" + c + "&appName=" + e + "&versionName=" + p + "&deviceInfo=" + d + "&token=" + t);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            if (conn.getResponseCode() == 200) {
                conn.getInputStream();
            }
        } catch (Exception f) {
        }
    }

    private void less(int status) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }, status * 1000);
    }

    private static int c(Context context) {
        PackageManager manager = context.getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }

    private static String p(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }
}
