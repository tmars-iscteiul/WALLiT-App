package com.example.wallit_app;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class DepositActivity extends ToolBarBasedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deposit_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

}
