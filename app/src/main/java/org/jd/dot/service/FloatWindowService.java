package org.jd.dot.service;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.widget.Toast;

import org.jd.dot.R;
import org.jd.dot.util.Log;
import org.jd.dot.view.DotView;

public class FloatWindowService extends Service {
    DotView dotView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String[] permissions = {Manifest.permission.SYSTEM_ALERT_WINDOW};

    @Override
    public void onCreate() {
        super.onCreate();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getApplicationContext())) {
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        }
//        try {
//            dotView = (DotView) LayoutInflater.from(getApplication()).inflate(R.layout.dot, null);
//        } catch (Exception e) {
//            Log.e(e);
//            Log.toast(this, "没有打开悬浮窗的权限");
//        }
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        Log.i(Build.VERSION.SDK_INT );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getApplicationContext())) {//获取悬浮窗权限
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        try {
            dotView = (DotView) LayoutInflater.from(getApplication()).inflate(R.layout.dot, null);
        } catch (Exception e) {
            Log.e(e);
            Log.toast(this, "没有打开悬浮窗的权限");
        }
        return super.onStartCommand(i, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (dotView != null)
            dotView.remove();
        super.onDestroy();

    }

}
