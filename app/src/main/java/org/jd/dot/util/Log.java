package org.jd.dot.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by cuijiandong on 2018/4/15.
 */

public class Log {
    private static final String TAG = "dot";

    public static void i(Object... s) {
        android.util.Log.i(TAG, toS(s));
    }

    public static void e(Throwable e, String... s) {
        android.util.Log.e(TAG, toS(s), e);
    }

    private static String toS(Object[] s) {
        StringBuilder sb = new StringBuilder();
        for (Object ss : s) {
            sb.append(ss);
        }
        return sb.toString();
    }
    public static void main(String[] a){
        System.out.print("hello log");
    }
    public static void toast(Context c,String text){
        Toast.makeText(c, text, Toast.LENGTH_SHORT).show();
    }
}
