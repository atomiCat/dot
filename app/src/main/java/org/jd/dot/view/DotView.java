package org.jd.dot.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import org.jd.dot.R;
import org.jd.dot.util.LocalShell;
import org.jd.dot.util.Log;
import org.jd.dot.util.RemoteShell;
import org.jd.dot.util.Shell;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cuijiandong on 2018/4/19.
 * 白点
 * 拖动，白点移动，十字双倍速度移动
 */

public class DotView extends LinearLayout {
    WindowManager windowManager;
    DotWindowParam dotParam;
    CrossWindowParam crossParam;
    View btn;
    View cross;


    public DotView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    OnLongPress onLongPress;

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        Log.i("悬浮窗加载完毕");
        getShell();
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display dp = windowManager.getDefaultDisplay();
        Point dpSize = new Point();
        dp.getSize(dpSize);
        crossParam.initDpSize(dpSize);

        windowManager.addView(this, dotParam);
        //指示十字光标
        cross = LayoutInflater.from(getContext()).inflate(R.layout.cross, null);
        windowManager.addView(cross, crossParam);

        btn = findViewById(R.id.dot_btn);
        btn.setOnTouchListener((v, e) -> {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dotParam.downPos(e);
                    dotParam.moving = false;

                    crossParam.downPos(e);
                    new Timer().schedule(onLongPress = new OnLongPress(), 700);
                    return true;
                case MotionEvent.ACTION_UP:
                    if (dotParam.time2down() < 200 && !dotParam.moving) {
                        v.performClick();
                    }
                    onLongPress.cancel();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (dotParam.moving) {
                        windowManager.updateViewLayout(this, dotParam.updateXY(e));
                        windowManager.updateViewLayout(cross, crossParam.updateXY(e, dotParam, 4));
                    } else {
                        if (Math.abs(dotParam.x2down(e)) + Math.abs(dotParam.y2down(e)) > 20) {
                            dotParam.moving = true;//移动距离超过20像素才算滑动
                            onLongPress.cancel();
                        }
                    }
            }
            return true;
        });
        btn.setOnClickListener((v) -> {
            Log.i("clicked", new Date().getTime());
            try {
                getShell().exec("input tap " + crossParam.x + " " + crossParam.y);
            } catch (Exception e) {
                Log.e(e);
                Log.toast(getContext(), "服务端未启动");
            }

        });
    }

    private Shell shell;

    private Shell getShell() {
        if (shell == null || shell.isDead()) {
            if (new File("/system/bin/su").exists() || new File("/system/xbin/su").exists()) {
                Shell.CallBack cb = (in) -> () -> {
                    try {
                        byte[] buf = new byte[256];
                        while (true) {
                            int i = in.read(buf);
                            if (i > 0)
                                Log.i("---", new String(buf, 0, i));
                        }
                    } catch (Exception e) {
                        Log.e(e);
                    }
                };
                shell = new LocalShell().setOnErr(cb).setOnIn(cb);
                shell.exec("su");
                Log.toast(getContext(), "root");
            } else {
                shell = new RemoteShell(getContext());
                Log.toast(getContext(), "shell");
            }
        }
        return shell;
    }

    public void remove() {
        windowManager.removeView(this);
        windowManager.removeView(cross);
    }

    {   //白点
        dotParam = new DotWindowParam(500, 500);
        //十字光标
        crossParam = new CrossWindowParam(dotParam);
    }

    private Handler handler = new Handler() {
        Drawable background;

        @Override
        public void handleMessage(Message msg) {
            if (background == null) {
                background = btn.getBackground();
                btn.setBackgroundColor(0xff);//透明
                cross.setVisibility(INVISIBLE);
                Log.toast(getContext(), "已隐藏，再次长按以显示");
            } else {
                btn.setBackground(background);
                background = null;
                cross.setVisibility(VISIBLE);
            }
        }
    };

    class OnLongPress extends TimerTask {
        @Override
        public void run() {
            Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(100);
            handler.sendEmptyMessage(0);
        }
    }

}

class DotWindowParam extends WindowManager.LayoutParams {
    public int fx, fy;//记录滑动初始状态手指坐标
    public int wx, wy;//记录移动前窗口坐标
    private long downTime;
    volatile boolean moving = false;

    long time2down() {
        return new Date().getTime() - downTime;
    }

    void downPos(MotionEvent e) {
        fx = (int) e.getRawX();
        fy = (int) e.getRawY();
        this.wx = x;
        this.wy = y;
        downTime = new Date().getTime();
    }

    int x2down(MotionEvent e) {
        return (int) e.getRawX() - fx;
    }

    int y2down(MotionEvent e) {
        return (int) e.getRawY() - fy;
    }

    DotWindowParam updateXY(MotionEvent e) {
        x = x2down(e) + wx;
        y = y2down(e) + wy;
        return this;
    }

    /**
     * 白点悬浮窗参数
     *
     * @param x0 初始位置
     * @param y0 初始位置
     */
    public DotWindowParam(int x0, int y0) {
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        type = TYPE_SYSTEM_ERROR;
        format = PixelFormat.RGBA_8888;//背景透明.
        //设置flags.不可聚焦 以全屏为坐标系
        flags = FLAG_NOT_FOCUSABLE | FLAG_LAYOUT_IN_SCREEN;
        gravity = Gravity.LEFT | Gravity.TOP;//坐标原点
        x = x0;
        y = y0;
        width = 150;
        height = 150;
    }
}

/**
 * 十字光标窗口参数
 */
class CrossWindowParam extends DotWindowParam {
    int xMax, yMax;

    public CrossWindowParam(DotWindowParam dp) {
        super(0, 0);
        flags = flags | FLAG_NOT_TOUCHABLE;
        width = 50;
        height = 50;
        //十字放置在白点中间
        x = dp.x + dp.width / 2 - width / 2;
        y = dp.y + dp.height / 2 - height / 2;
    }

    public void initDpSize(Point dpSize) {
        xMax = dpSize.x - width;
        yMax = dpSize.y - height;
    }

    DotWindowParam updateXY(MotionEvent e, DotWindowParam dp, float speed) {
        x = (int) (dp.x2down(e) * speed) + wx;
        y = (int) (dp.y2down(e) * speed) + wy;

        if (x > xMax || x < 0 || y > yMax || y < 0) {//如果超出边界，则以当前位置为参考继续移动
            x = shrink(x, 0, xMax);
            y = shrink(y, 0, yMax);
            dp.downPos(e);
            downPos(e);
        }

        return this;
    }

//    @Override
//    void downPos(MotionEvent e) {
//        super.downPos(e);
//        xl = -1;
//    }
//
//    float xl, yl;//上次MotionEvent坐标

//    DotWindowParam updateXY(MotionEvent e, DotWindowParam dp, float speed) {
//        if (xl != -1) {
//            x = shrink(d(e.getRawX() - xl) + x, 0, xMax);
//            y = shrink(d(e.getRawY() - yl) + y, 0, yMax);
//        }
//
//        xl = e.getRawX();
//        yl = e.getRawY();
//        return this;
//    }
//
//    private int d(float dxy) {
//        int d = dxy > -1 && dxy < 0 ? -1 : dxy > 0 && dxy < 1 ? 1 : Math.round(dxy);
//        return d * speed[Math.abs(d)];
//    }
//
//    int[] speed = new int[100];
//
//    {
//        for (int i = 0; i < speed.length; i++) {
//            speed[i] = i % 3 + 1;
//        }
//    }

    private static int shrink(int n, int min, int max) {
        return n < min ? min : n > max ? max : n;
    }
}
