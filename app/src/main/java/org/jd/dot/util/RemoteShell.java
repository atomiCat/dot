package org.jd.dot.util;

import android.content.Context;
import android.os.Looper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.System.in;

/**
 * Created by cuijiandong on 2018/4/13.
 */

public class RemoteShell extends Shell {
    private static volatile RemoteShell shell;
    Socket ss;

    @Override
    public boolean isDead() {
        boolean isDead = ss.isClosed() || !isAlive();
        if (isDead)
            IOUtil.close(ss);
        return isDead;
    }
    Context ctxt;
    public RemoteShell(Context c) {
        ctxt=c;
        onIn = defaultCallBack;
    }

    @Override
    public void run() {
        try {
            ss = new Socket("127.0.0.1", 10098);
            setInOut(ss.getOutputStream(), ss.getInputStream());
            super.run();
        } catch (Exception e) {
            Log.e(e);
            IOUtil.close(ss);
        }
    }
}
