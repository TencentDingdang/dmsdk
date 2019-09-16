package com.tencent.ai.tvs.dmsdk.demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class LogView extends ScrollView {
    private final TextView mLogTextView;
    private String log;

    public LogView(Context context) {
        this(context, null);
    }

    public LogView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LogView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLogTextView = new TextView(context);
        mLogTextView.setTextIsSelectable(true);
        addView(mLogTextView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        log = "";
    }

    public void appendLogLn(String text) {
        log += text + "\n";
        mLogTextView.setText(log);
        fullScroll(FOCUS_DOWN);
    }
}
