package com.example.wallit_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import android.view.View;
import android.support.v7.widget.Toolbar;

import com.example.wallit_app.networking.NetworkingService;

public class HomeActivity extends ToolBarBasedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String username = intent.getStringExtra(LoginActivity.LOGIN_USER);

        this.username = username;
        TextView textView = findViewById(R.id.welcome_text);
        textView.setText("Welcome " + username + "!");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        //Changes 'back' button action
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            showLogoutDialog();
        }
        return true;
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
