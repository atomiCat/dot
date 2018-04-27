package org.jd.dotserver;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

public class IOUtil {
    public static void close(Closeable... resources) {
        if (resources != null)
            for (Closeable c : resources)
                try {
                    c.close();
                } catch (Exception e) {
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
    public static void syso(Object... a) throws IOException {
        for (Object s : a) {
            System.out.write(s.toString().getBytes(Charset.forName("gbk")));
        }
        System.out.write('\n');
    }
}