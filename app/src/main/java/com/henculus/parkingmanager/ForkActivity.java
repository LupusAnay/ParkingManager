package com.henculus.parkingmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ForkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fork);
    }

    public void goto_register(View view) {
        Intent intent = new Intent(ForkActivity.this, RegActivity.class);
        startActivity(intent);
    }

    public void goto_inspector(View view) {
        Intent intent = new Intent(ForkActivity.this, InspectorActivity.class);
        startActivity(intent);
    }
}
