package org.jd.dot.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.jd.dot.MainActivity;
import org.jd.dot.R;
import org.jd.dot.service.FloatWindowService;


/**
 * Created by cuijiandong on 2018/4/15.
 */

public class OptionView extends LinearLayout {

    Switch helper;

    public OptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        helper = findViewById(R.id.helper);
        helper.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                MainActivity.that.requestOverlayPermission();
                getContext().startService(new Intent(getContext(), FloatWindowService.class));
            } else {
                getContext().stopService(new Intent(getContext(), FloatWindowService.class));
            }
        });
        TextView helpText = findViewById(R.id.helpText);
        String cmd = "adb shell \"sh /data/data/org.jd.dot/dot &\"";
        helpText.setText("adb连结好手机后，执行下面命令以开启服务端\n");
        helpText.append(cmd);

        Button btn = findViewById(R.id.copyCmd);
        btn.setOnClickListener((v) -> {
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText(null, cmd));
        });
    }
}
