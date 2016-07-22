package com.agpfd.crazyeights;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CrazyEightsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrazyEightsView crazyEightsView = new CrazyEightsView(this);
        setContentView(crazyEightsView);
    }
}
