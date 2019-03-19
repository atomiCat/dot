package org.jd.dot.util;

import java.net.Socket;

/**
 * Created by cuijiandong on 2018/4/13.
 */

public class RemoteShell extends Shell {
    private Socket ss;
    private String host;
    private int port;

    @Override
    public boolean isDead() {
        return ss.isClosed() || !isAlive();
    }

    @Override
    public void close() {
        IOUtil.close(ss);
        super.close();
    }

    public RemoteShell(String host, int port) {
        this.host = host;
        this.port = port;
        onIn = defaultCallBack;
    }

    @Override
    public void run() {
        try {
            ss = new Socket(host, port);
            setInOut(ss.getOutputStream(), ss.getInputStream());
            super.run();
        } catch (Exception e) {
            Log.e(e);
            IOUtil.close(ss);
        }
    }
}
