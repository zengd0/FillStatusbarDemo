package com.zengd.fillstatusbardemo;


import android.content.Intent;
import android.view.View;

public class MainActivity extends BaseActivity {

    @Override
    protected int getViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {

    }

    public void doClick(View view) {
        startActivity(new Intent(this, SecondActivity.class));
    }
}
