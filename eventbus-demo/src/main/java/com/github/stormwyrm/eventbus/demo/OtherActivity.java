package com.github.stormwyrm.eventbus.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.github.stormwyrm.eventbus.EventBus;
import com.github.stormwyrm.eventbus.annotation.Subscribe;

/**
 * Autor: liqingfeng
 * Date: 2019/5/21
 * Desc:
 **/
public class OtherActivity extends AppCompatActivity {
    private static final String TAG = "OtherActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(isSticky = true)
    public void onReceiverStickyEvent(String stickyEventStr){
        Log.d(TAG, "onReceiverStickyEvent: "+stickyEventStr);
    }

    public void onClick(View view) {
        EventBus.getDefault().postEvent("HAHAH");
        EventBus.getDefault().postEvent(1);
    }
}
