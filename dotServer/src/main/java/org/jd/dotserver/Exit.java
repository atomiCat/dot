package org.jd.dotserver;

/**
 * Created by cuijiandong on 2018/4/25.
 *
 */

public class Exit implements OnIn {
    @Override
    public void onIn(byte[] buf, int len, IOPump self) {
        if (stringEq("exit", buf, len)) {
            System.out.print("system exit ");
            self.stopPump();
            System.exit(0);
            System.out.print("system exit over");
        }
    }

    boolean stringEq(String s, byte[] buf, int len) {
        if (len >= s.length()) {
            for (int i = 0; i < s.length(); i++) {
                if (buf[i] != s.charAt(i))
                    return false;
            }
            return true;
        }
        return false;
    }
}