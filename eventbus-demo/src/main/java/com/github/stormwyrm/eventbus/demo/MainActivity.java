package com.github.stormwyrm.eventbus.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.stormwyrm.eventbus.EventBus;
import com.github.stormwyrm.eventbus.annotation.Subscribe;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onClick(View view) {
        startActivity(new Intent(this, OtherActivity.class));
    }

    @Subscribe()
    public void hahah(String hahah) {
        Log.d(TAG, "hahah: " + 1);
    }

    @Subscribe()
    public void hahah1(Integer b) {
        Log.d(TAG, "hahah1: " + b);
    }
}
