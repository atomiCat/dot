package org.jd.dot.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.LayoutInflater;

import org.jd.dot.R;
import org.jd.dot.util.Log;
import org.jd.dot.view.DotView;

public class FloatWindowService extends Service {
    DotView dotView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
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
