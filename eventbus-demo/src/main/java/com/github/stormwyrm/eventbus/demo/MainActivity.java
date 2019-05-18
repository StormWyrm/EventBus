package com.github.stormwyrm.eventbus.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.stormwyrm.eventbus.annotation.Subscribe;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Subscribe()
    public void hahah(String hahah){

    }

    @Subscribe()
    public void hahah1(int b){

    }
}
