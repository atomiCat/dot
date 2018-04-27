package org.jd.dotserver;

/**
 * Created by cuijiandong on 2018/4/25.
 */

public interface OnIn {
    void onIn(byte[] buf, int len, IOPump self);
}