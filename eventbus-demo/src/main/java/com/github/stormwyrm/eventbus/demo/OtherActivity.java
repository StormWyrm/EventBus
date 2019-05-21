package com.github.stormwyrm.eventbus.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.stormwyrm.eventbus.EventBus;

/**
 * Autor: LiQingfeng
 * Date: 2019/5/21
 * Desc:
 **/
public class OtherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
    }

    public void onClick(View view) {
        EventBus.getDefault().postEvent("HAHAH");
        EventBus.getDefault().postEvent(1);
    }
}
