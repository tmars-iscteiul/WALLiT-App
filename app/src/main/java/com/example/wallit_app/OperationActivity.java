package com.example.wallit_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;
import android.support.v7.widget.Toolbar;

public class OperationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.operation_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(LoginActivity.LOGIN_USER);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.welcome_text);
        textView.setText("Welcome " + message + "!");


    }

    public void logoutUser(View view)   {

    }

}
