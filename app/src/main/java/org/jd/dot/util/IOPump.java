package org.jd.dot.util;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Created by cuijiandong on 2018/4/18.
 */

public class IOPump extends Thread {
    private ReadableByteChannel cIn;
    private InputStream sIn;
    private WritableByteChannel cOut;
    private OutputStream sOut;
    private int bufferSize = 128;
    private volatile boolean continu = true;
    private Closeable[] closeFinally;

    Finally theFinally;

    public IOPump setFinally(Finally aFinally) {
        theFinally = aFinally;
        return this;
    }

    public IOPump setIn(ReadableByteChannel cIn) {
        this.cIn = cIn;
        return this;
    }

    public IOPump setIn(InputStream sIn) {
        this.sIn = sIn;
        return this;
    }

    public IOPump setOut(WritableByteChannel cOut) {
        this.cOut = cOut;
        return this;
    }

    public IOPump setOut(OutputStream sOut) {
        this.sOut = sOut;
        return this;
    }

    public IOPump closeFinally(Closeable... c) {
        this.closeFinally = c;
        return this;
    }

    public IOPump(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public IOPump() {
    }

    @Override
    public void run() {
        try {//有任何异常结束线程
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            byte[] bufByte = buffer.array();
            while (continu) {
                buffer.clear();
                if (cIn != null) {
                    if (cIn.read(buffer) < 0)
                        throw new RuntimeException("读到channel结尾");
                } else {
                    int read = sIn.read(bufByte);
                    if (read < 0)
                        throw new RuntimeException("读到stream结尾");
                    buffer.position(read);
                }
                if (buffer.position() == 0) {//未读到数据
                    sleep(1);
                    continue;
                }
//                syso(buffer.limit(), ":", new Date().getTime());
                buffer.flip();
//                System.out.print("ioPump----"+new String(bufByte,0,buffer.limit()));
                if (cOut != null)
                    IOUtil.write(cOut, buffer);
                else
                    IOUtil.write(sOut, buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.close(closeFinally);
            if(theFinally!=null)
                theFinally.finallly();
        }
    }

    public void stopPump() {
        continu = false;
        interrupt();
    }
    public interface Finally {
        void finallly();
    }
}


