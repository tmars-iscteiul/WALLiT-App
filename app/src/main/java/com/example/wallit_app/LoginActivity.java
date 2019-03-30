package com.example.wallit_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    public static final String LOGIN_USER = "com.example.wallit_app.LOGINUSER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
    }


    public void loginUser(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        EditText editText = (EditText) findViewById(R.id.username);
        String username = editText.getText().toString();
        intent.putExtra(LOGIN_USER, username);
        startActivity(intent);
    }
}
