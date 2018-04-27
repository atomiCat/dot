package org.jd.dot.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * Created by cuijiandong on 2018/4/13.
 */

public class IOUtil {
    public static void close(Closeable... resources) {
        for (Closeable c : resources) {
            try {
                c.close();
            } catch (Exception e) {
                Log.e(e);
            }
        }
    }
    public static void write(WritableByteChannel out, ByteBuffer buf) throws IOException {
        while (buf.hasRemaining())
            out.write(buf);
    }

    public static void write(OutputStream out, ByteBuffer buf) throws IOException {
        out.write(buf.array(), buf.position(), buf.limit());
        out.flush();
    }
}
