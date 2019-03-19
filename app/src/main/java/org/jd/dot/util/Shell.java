package org.jd.dot.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Shell extends Thread {
    BlockingQueue<String> buffer = new LinkedBlockingQueue<>(10);
    OutputStream out;
    InputStream in;
    CallBack onIn;

    public Shell() {
        start();
    }

    void setInOut(OutputStream out, InputStream in) {
        this.out = out;
        this.in = in;
    }

    public void exec(String cmd) {
        Log.i("缓存命令：", cmd);
        buffer.offer(cmd);
    }
    public abstract boolean isDead();
    public boolean isDeadAndClose(){
        if(isDead()){
            close();
            return true;
        }else return false;
    }
    public void close(){
        IOUtil.close(out,in);
    };
    void out(String cmd) throws IOException {
        out.write(cmd.getBytes());
        out.write('\n');
        out.flush();
    }
    public Shell setOnIn(CallBack onIn) {
        this.onIn = onIn == null ? defaultCallBack : onIn;
        return this;
    }
    @Override
    public void run() {
        try {
            new Thread(onIn.ready(in)).start();
            while (true) {
                String cmd = buffer.take();
                Log.i("输出命令：", cmd);
                out(cmd);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public interface CallBack {
        Runnable ready(InputStream in);
    }

    static final CallBack defaultCallBack = (in) -> () -> {
        try {
            while (in.read() != -1) ;
        } catch (IOException e) {
            Log.e(e);
        }
    };
}