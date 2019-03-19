package org.jd.dot;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import org.jd.dot.util.IOPump;
import org.jd.dot.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {
    public static MainActivity that;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        that = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            String path = "/data/data/org.jd.dot/";
            File sh = new File(path + "dot");
//            if (!sh.exists()) {
            OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(sh), Charset.forName("utf-8"));
            //   app_process -Djava.class.path=/data/local/tmp/shd.dex /data/local/tmp org.jd.shellserver.Shell
//                w.write("app_process -Djava.class.path=" + path + "shd.dex " + path + " org.jd.shellserver.Shell ");
            w.write("dalvikvm -cp " + path + "shd.dex " + " org.jd.shellserver.Shell & echo ok !");
            w.flush();
            w.close();
//                Log.toast(this, "启动脚本写入成功 " + sh.getAbsolutePath());
//            }
            File dex = new File(path + "shd.dex");
//            if (!dex.exists()) {
            InputStream in = getAssets().open("shd.dex");
            OutputStream out = new FileOutputStream(dex);
            new IOPump().setIn(in).setOut(out).closeFinally(in, out).setFinally(() -> {
                try {
                    Runtime.getRuntime().exec("chmod 744 " + dex.getAbsolutePath());
                    Runtime.getRuntime().exec("chmod 755 " + sh.getAbsolutePath());
                } catch (IOException e) {
                    Log.e(e);
                }
            }).start();
//            }
//            Log.toast(this, "服务端文件存在" + sh.exists() + "," + dex.exists());
        } catch (Exception e) {
            Log.e(e);
            Log.toast(this, "无法写入服务端文件");
        }
    }

    public void requestOverlayPermission() {

        Log.i("api level ", Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//获取悬浮窗权限
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent,1);
            }
        }
    }
}
