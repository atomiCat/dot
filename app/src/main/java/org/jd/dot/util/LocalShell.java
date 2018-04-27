package org.jd.dot.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by cuijiandong on 2018/4/13.
 */

public class LocalShell extends Shell {
    private Process p;
    CallBack onErr;
//    public void execSynchronously(String cmd) {
//        try {
//            Log.i("同步执行--->| ", cmd);
//            out(cmd);
//            p.waitFor();
//            Log.i(" |<---同步执行完毕\n");
//        } catch (Exception e) {
//            Log.i("\n---同步执行异常--->", e.getClass().getName(), ":", e.getMessage(), "\n");
////            onErr.onMessage("\n---ERROR--->", e.getClass().getName(), ":", e.getMessage(), "\n");
//        }
//    }

//    public LocalShell(CallBack onIn, CallBack onErr) {
//        this.onIn = onIn == null ? defaultCallBack : onIn;
//        this.onErr = onErr == null ? defaultCallBack : onErr;
//    }

    public LocalShell() {
        onIn = defaultCallBack;
        onErr = defaultCallBack;
    }

    @Override
    public boolean isDead() {
        return !isAlive();
    }

    @Override
    public void run() {
        try {
            String osName = System.getProperty("os.name");
            Log.i("shell线程开始", osName);
            p = Runtime.getRuntime().exec("Linux".equalsIgnoreCase(osName) ? "sh" : "cmd");
            setInOut(p.getOutputStream(), p.getInputStream());
            new Thread(onErr.ready(p.getErrorStream())).start();
            super.run();
        } catch (Exception e) {
            Log.e(e);
        }
    }

    public Shell setOnErr(CallBack onErr) {
        this.onErr = onErr;
        return this;
    }
}
