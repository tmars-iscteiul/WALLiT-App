package com.example.wallit_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;
import android.support.v7.widget.Toolbar;

public class HomeActivity extends ToolBarBasedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String username = intent.getStringExtra(LoginActivity.LOGIN_USER);

        TextView textView = findViewById(R.id.welcome_text);
        textView.setText("Welcome " + username + "!");
    }

    public void depositScreen(View view)    {
        startActivity(new Intent(this, DepositActivity.class));
    }

    public void withdrawScreen(View view)   {
        startActivity(new Intent(this, WithdrawActivity.class));
    }

    public void fundInfoScreen(View view)   {
        startActivity(new Intent(this, FundInfoActivity.class));
    }

    public void statsScreen(View view)   {
        startActivity(new Intent(this, StatsActivity.class));
    }

}
