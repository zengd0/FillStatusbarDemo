package com.zengd.fillstatusbardemo;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;


public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getViewId());
        setFillStatusBar();
        init();
    }


    protected abstract int getViewId();

    protected abstract void init();

    /**
     * 设置沉浸式通知栏
     */
    private void setFillStatusBar() {
        if (getFillStatusBar()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            @SuppressLint("WrongViewCast") RelativeLayout barView = (RelativeLayout) findViewById(R.id.bar_view);
            if (barView != null) {
                barView.setVisibility(View.VISIBLE);
                barView.getLayoutParams().height = getStatusBarHeight();
            }

            //防止输入法遮挡UI
            controlKeyboardLayout(getWindow().getDecorView().findViewById(android.R.id.content));
        }
    }

    /**
     * 是否使用沉浸式通知栏
     *
     * @return true使用
     */
    public boolean getFillStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return true;
        }
        return false;
    }

    /**
     * 用于获取状态栏的高度。 使用Resource对象获取（推荐这种方式）
     *
     * @return 返回状态栏高度的像素值。
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private boolean mIsShowKeyboard;
    /**
     * 控制输入法不遮挡UI
     *
     * @param root 根标签，最外层布局，需要调整的布局
     */
    protected void controlKeyboardLayout(final View root) {
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                // 应用可以显示的区域。此处包括应用占用的区域，
                // 以及ActionBar和状态栏，但不含设备底部的虚拟按键。
                Rect r = new Rect();
                root.getWindowVisibleDisplayFrame(r);

                // 屏幕高度。这个高度不含虚拟按键的高度
                int screenHeight = root.getRootView().getHeight();

                int heightDiff = screenHeight - (r.bottom - r.top);

                int statusBarHeight = getStatusBarHeight();
                // 在不显示软键盘时，heightDiff等于状态栏的高度
                // 在显示软键盘时，heightDiff会变大，等于软键盘加状态栏的高度。
                // 所以heightDiff大于状态栏高度时表示软键盘出现了，
                // 这时可算出软键盘的高度，即heightDiff减去状态栏的高度
                // 软键盘的高度
                int keyboardHeight = 0;
                if (keyboardHeight == 0 && heightDiff > statusBarHeight) {
                    keyboardHeight = heightDiff - statusBarHeight;
                }

                if (mIsShowKeyboard) {
                    // 如果软键盘是弹出的状态，并且heightDiff小于等于状态栏高度，
                    // 说明这时软键盘已经收起
                    if (heightDiff <= statusBarHeight) {
                        mIsShowKeyboard = false;
                        ViewGroup.LayoutParams params = root.getLayoutParams();
                        params.height = screenHeight;
                        root.setLayoutParams(params);

                    }
                } else {
                    // 如果软键盘是收起的状态，并且heightDiff大于状态栏高度，
                    // 说明这时软键盘已经弹出
                    if (heightDiff > statusBarHeight) {
                        mIsShowKeyboard = true;
                        ViewGroup.LayoutParams params = root.getLayoutParams();
                        params.height = screenHeight - keyboardHeight;
                        root.setLayoutParams(params);
                    }
                }
            }
        });
    }
}
