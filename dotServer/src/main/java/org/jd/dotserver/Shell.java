package org.jd.dotserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Shell extends Thread {
    public static void main(String[] a) throws Exception {
        ServerSocket ss = new ServerSocket(10098);
        while (true) {
            IOUtil.syso("等待连接 ");
            Socket s = ss.accept();
            new Shell(s).start();
        }
//        System.out.print("hello shell");
    }


    public Shell(Socket s) throws IOException {
        this.s = s;
        sIn = s.getInputStream();
        sOut = s.getOutputStream();
    }

    Process p;
    OutputStream pOut;
    InputStream pIn;
    InputStream pErr;

    Socket s;
    InputStream sIn;
    OutputStream sOut;

    @Override
    public void run() {

        try {
            IOUtil.syso("建立连接成功 ", s.getRemoteSocketAddress());

            String osName = System.getProperty("os.name");
            p = Runtime.getRuntime().exec("Linux".equalsIgnoreCase(osName) ? "sh" : "cmd");
            pOut = p.getOutputStream();
            pIn = p.getInputStream();
            pErr = p.getErrorStream();

            new IOPump().setIn(pIn).setOut(sOut).setOnIn(onIn).start();
            new IOPump().setIn(pErr).setOut(sOut).start();
            new IOPump().setIn(sIn).setOut(pOut).run();//在当前线程中执行,run() 会一直阻塞
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (p != null)
                p.destroy();
            IOUtil.close(pOut, pErr, pIn, s, sIn, sOut);
        }
    }

    OnIn onIn = new Exit();
}
